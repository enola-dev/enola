# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

# This builds the container image to use Enola as a "product" as end-user.

# TODO Make this be a multi-stage build which uses Dockerfile-DevEnv?!

FROM gcr.io/distroless/java21-debian12:nonroot

# https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#labelling-container-images
LABEL org.opencontainers.image.source=https://github.com/enola-dev/enola
LABEL org.opencontainers.image.description="https://Enola.dev"
LABEL org.opencontainers.image.licenses=Apache-2.0

# Loosely inspired by https://github.com/GoogleContainerTools/distroless/blob/main/examples/java/Dockerfile

# For consistency, use Enola's standard "distro"
# (instead of e.g. directly COPY bazel-bin/java/dev/enola/cli/enola_deploy.jar)...

# ...BUT note that we still CANNOT just do e.g. ENTRYPOINT ["enola"],
# because in a (non :debug!) distroless we (intentionally!) do not
# even have any shell - so we still just have to "java -jar enola".

# Nota bene: The /app/CWD/ and ../enola circus is to be able to use
# 'docker run ... -v "$PWD":/app/CWD/:Z' in the 'enola-c' launch script;
# see docs/use/index.md.

WORKDIR /app/CWD/
COPY --chmod=0777 --chown=nonroot:nonroot site/download/latest/enola /app/enola
ENTRYPOINT [ "java", "-jar", "../enola" ]

# To debug, replace FROM :nonroot with :debug-nonroot,
# and use ENTRYPOINT [ "/busybox/sh" ] instead of above,
# and then "docker run" WITHOUT any additional arguments.
