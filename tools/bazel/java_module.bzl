# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2026 The Enola <https://enola.dev> Authors
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
"""Enola Java Module macro."""

load("@rules_java//java:defs.bzl", "java_library")
load("@rules_jvm_external//:defs.bzl", "java_export")

MAVEN_GROUP_ID = "dev.enola"
MAVEN_VERSION = "0.0.1-SNAPSHOT"

def java_module(name, export_maven = None, tags = [], **kwargs):
    """Enola Java Module macro.

    Wraps java_library by default, and java_export when export_maven is specified.

    Args:
        name: Name of the target.
        export_maven: Maven artifact ID string (e.g. "common", "common-context").
            If None (default), the target is a standard java_library and not exported to Maven.
        tags: Target tags.
        **kwargs: Standard java_library arguments (srcs, deps, resources, visibility, etc.).
    """
    if not export_maven:
        java_library(
            name = name,
            tags = tags,
            **kwargs
        )
    else:
        if type(export_maven) != "string":
            fail("export_maven must be a string representing the Maven artifact ID, e.g. 'common'")

        coords = "%s:%s:%s" % (MAVEN_GROUP_ID, export_maven, MAVEN_VERSION)

        export_tags = list(tags)
        if "no-javadocs" not in export_tags:
            export_tags.append("no-javadocs")

        java_export(
            name = name,
            maven_coordinates = coords,
            tags = export_tags,
            **kwargs
        )
