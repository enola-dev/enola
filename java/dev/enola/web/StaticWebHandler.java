/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.web;

import static com.google.common.util.concurrent.Futures.immediateFuture;

import com.google.common.util.concurrent.ListenableFuture;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.FileResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.File;
import java.net.URI;

/**
 * {@link WebHandler} which servers static web content using Enola Resource I/O.
 *
 * <p>Useful e.g. for fixed HTML, CSS + JS files, or <a href="https://www.webjars.org">WebJars</a>.
 */
public class StaticWebHandler implements WebHandler {

    private final String uriPrefix;
    private final ResourceProvider resourceProvider;

    public StaticWebHandler(String uriPrefix, String classpathPrefix) {
        this.uriPrefix = uriPrefix;
        this.resourceProvider = new ClasspathResource.Provider(classpathPrefix);
    }

    public StaticWebHandler(String uriPrefix, File directory) {
        this.uriPrefix = uriPrefix;
        this.resourceProvider = new FileResource.Provider(directory);
    }

    @Override
    public ListenableFuture<ReadableResource> handle(URI uri) {
        var path = uri.getPath();
        if (path.contains("..")) {
            throw new IllegalArgumentException("URI cannot contain '..':" + path);
        }
        if (!path.startsWith(uriPrefix)) {
            throw new IllegalStateException(path + " does not start with " + uriPrefix);
        }
        var cutPath = path.substring(uriPrefix.length());
        var resource = resourceProvider.get(cutPath);
        return immediateFuture(resource);
    }
}
