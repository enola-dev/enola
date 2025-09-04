/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.enola.cli;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClasspathHellDuplicatesCheckerTest {

    @Test
    public void NOOP() {}

    @Test
    @Ignore // TODO Fix why Netty from grpc-java maven@ CP on *OUR* maven@ ?!
    public void testIfThereAreAnyDuplicateJARsOnTheClasspath() throws Exception {
        var problems = new HashMap<String, List<String>>();
        try (ScanResult scanResult = new ClassGraph().scan()) {
            var results = scanResult.getAllResources().classFilesOnly().findDuplicatePaths();
            System.out.println("ClassGraph duplicate results: " + results);
            for (var duplicate : results) {
                String resourceName = duplicate.getKey();
                if (!isHarmlessDuplicate(resourceName)) {
                    boolean addIt = true;
                    List<String> jars =
                            duplicate.getValue().stream()
                                    .map(resource -> resource.getURL().toExternalForm())
                                    .collect(Collectors.toList());
                    for (String jar : jars) {
                        if (skipJAR(jar)) {
                            addIt = false;
                            break;
                        }
                    }
                    if (addIt) {
                        problems.put(resourceName, jars);
                    }
                }
            }
        }
        if (!problems.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : problems.entrySet()) {
                sb.append(entry.getKey());
                sb.append('\n');
                for (String location : entry.getValue()) {
                    sb.append("    ");
                    sb.append(location);
                    sb.append('\n');
                }
            }
            Assert.fail(sb.toString());
        }
    }

    private boolean skipJAR(String jarPath) {
        // Bazel Rules etc. not runtime classpath
        return jarPath.contains("java_tools/Runner_deploy.jar")

                // TODO Declare Maven dependencies in 1 single place, instead of both in
                // maven.install() of MODULE.bazel and in maven_install() of WORKSPACE.bazel.
                || jarPath.contains("/external/rules_jvm_external%7e5.3%7emaven%7emaven/")

                // TODO: Fix the sad mess :( of duplicate Protobuf & gRPC JARs!
                || jarPath.contains("protobuf")
                || jarPath.contains("grpc")

                // TODO: Fix org.glassfish:jakarta.json & jakarta.json:jakarta.json-api mess...
                || jarPath.contains("org/glassfish");
    }

    protected boolean isHarmlessDuplicate(String resourcePath) {
        return resourcePath.equals("module-info.class");
    }
}
