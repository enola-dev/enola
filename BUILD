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

# https://github.com/aignas/rules_shellcheck
load("@com_github_aignas_rules_shellcheck//:def.bzl", "shellcheck_test")

# TODO https://github.com/aignas/rules_shellcheck/issues/17 this does not work reliably... :-(
shellcheck_test(
    name = "shellcheck_test",
    size = "small",
    data = glob(
        [
            "**/*.bash",
            "**/*.sh",
        ],
        exclude = [
            "**/bazel-bin/**",
            "**/bazel-out/**",
            "site/**",
            ".venv/**",
            "**/node_modules/**",
        ],
    ),
    tags = ["lint"],
)
