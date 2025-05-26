# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023 The Enola <https://enola.dev> Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

load("@rules_java//java:defs.bzl", "java_library")
load("@rules_jvm_external//:defs.bzl", "java_export", "javadoc")

java_library(
    name = "api",
    srcs = glob(
        ["*.java"],
    ),
    visibility = ["//:__subpackages__"],
    deps = [
        "//java/dev/enola/thing:thing_java",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:org_jspecify_jspecify",
    ],
)

java_export(
    name = "enola",
    maven_coordinates = "dev.enola:enola:0.0.1-SNAPSHOT",
    # NB: JavaDoc does not work here, but below it's fine
    tags = ["no-javadocs"],
    visibility = ["//:__subpackages__"],
    # TODO runtime_deps or exports = [ ?!
    runtime_deps = [
        # TODO Keep this list (manually) updated; using:
        # bazel query "deps(//...)" --output package | grep java/dev/enola/ | sed 's/^/"\/\//;s/$/",/' | sort | wl-copy
        # TODO This ^^^ this doesn't entirely "just work" quite yet... FIXME!
        "//java/dev/enola/ai/langchain4j",
        "//java/dev/enola/cas",
        "//java/dev/enola/chat",
        "//java/dev/enola/chat/sshd",
        "//java/dev/enola/cli:lib",
        "//java/dev/enola/cli/common",
        "//java/dev/enola/cli/demo",
        "//java/dev/enola/common",
        "//java/dev/enola/common/canonicalize",
        "//java/dev/enola/common/collect",
        "//java/dev/enola/common/concurrent",
        "//java/dev/enola/common/context",
        "//java/dev/enola/common/context/testlib",
        "//java/dev/enola/common/convert",
        "//java/dev/enola/common/diff/testlib",
        "//java/dev/enola/common/exec",
        "//java/dev/enola/common/exec/pty",
        "//java/dev/enola/common/function",
        "//java/dev/enola/common/html",
        "//java/dev/enola/common/io",
        "//java/dev/enola/common/io/iri",
        "//java/dev/enola/common/io/metadata",
        "//java/dev/enola/common/io/testlib",
        "//java/dev/enola/common/linereader",
        "//java/dev/enola/common/linereader/jline",
        "//java/dev/enola/common/locale",
        "//java/dev/enola/common/protobuf",
        "//java/dev/enola/common/secret",
        "//java/dev/enola/common/secret/auto",
        "//java/dev/enola/common/secret/exec",
        "//java/dev/enola/common/secret/gnome",
        "//java/dev/enola/common/secret/yaml",
        "//java/dev/enola/common/string2long",
        "//java/dev/enola/common/text",
        "//java/dev/enola/common/time",
        "//java/dev/enola/common/tree",
        "//java/dev/enola/common/xml",
        "//java/dev/enola/common/yamljson",
        "//java/dev/enola/common/yamljson/testlib",
        "//java/dev/enola/connect/maven:mima",
        "//java/dev/enola/core",
        "//java/dev/enola/data",
        "//java/dev/enola/data/id",
        "//java/dev/enola/data/iri",
        "//java/dev/enola/data/iri/namespace",
        "//java/dev/enola/data/iri/namespace/repo",
        "//java/dev/enola/data/iri/template",
        "//java/dev/enola/datatype",
        "//java/dev/enola/format/tika",
        "//java/dev/enola/format/tika/rdf",
        "//java/dev/enola/format/xml",
        "//java/dev/enola/identity",
        "//java/dev/enola/infer/datalog",
        "//java/dev/enola/infer/rdf",
        "//java/dev/enola/model",
        "//java/dev/enola/rdf/io",
        "//java/dev/enola/rdf/proto",
        "//java/dev/enola/thing:thing_java",
        "//java/dev/enola/thing/gen",
        "//java/dev/enola/thing/testlib",
        "//java/dev/enola/web",
        "//java/dev/enola/web/netty",
        "//java/dev/enola/web/testlib",
        "//java/dev/enola/zimpl",
        "@enola_maven//:com_google_guava_guava",
        "@enola_maven//:com_google_protobuf_protobuf_java",
        "@enola_maven//:org_apache_logging_log4j_log4j_to_jul",
        "@enola_maven//:org_eclipse_rdf4j_rdf4j_model",
        "@enola_maven//:org_eclipse_rdf4j_rdf4j_rio_api",
        "@enola_maven//:org_jspecify_jspecify",
        "@enola_maven//:org_slf4j_slf4j_api",
        "@enola_maven//:org_slf4j_slf4j_jdk14",
    ],
)

# TODO Replace Bazel JavaDoc with tools/javadoc/build.bash (but Maven JAR?)
# This is used by tools/docs/build.bash (and tools/maven/install.bash)
javadoc(
    name = "javadoc",
    doc_url = "https://docs.enola.dev/dev/javadoc/",
    # Note that even merely javadocopts = [] makes the build fail?! ;=(
    # TODO javadocopts = [
    #   TODO "-Xdoclint:all",
    #   TODO "-Werror",
    #"-encoding",
    #"UTF-8",
    #"-charset",
    #"UTF-8",
    #],
    deps = [
        ":enola",
        # TODO Remove, but how not to fail?! https://github.com/bazel-contrib/rules_jvm_external/issues/1344
        "@enola_maven//:com_google_protobuf_protobuf_java",
        "@enola_maven//:com_google_protobuf_protobuf_java_util",
        "@enola_maven//:javax_annotation_javax_annotation_api",
        # NB: We cannot use "@enola_maven//:com_google_guava_guava" here,
        # due to https://github.com/bazel-contrib/rules_jvm_external/issues/1343.
    ],
)
