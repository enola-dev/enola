/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource.repository;

import dev.enola.common.io.resource.AbstractResource;
import dev.enola.common.io.resource.Resource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.data.Repository;

// TODO Think this through... e.g. directories, writeable ZIPs, remote SSH FS - ThingRepository!!
// TODO Thing through relationship to and integration of with GlobResolver...
public interface ResourceRepository
        extends AbstractResource, Repository<Resource>, ResourceProvider {}
