/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.gui;

public class ColumnDescriptor {
    public String name;
    public AggregationMethod aggregationMethod;
    public boolean isUrl;
    public ColumnDescriptor(String name, AggregationMethod aggregationMethod) {
        this.name = name;
        this.aggregationMethod = aggregationMethod;
        this.isUrl = false;
    }
    public ColumnDescriptor(String name, AggregationMethod aggregationMethod, boolean isUrl) {
        this.name = name;
        this.aggregationMethod = aggregationMethod;
        this.isUrl = isUrl;
    }
}
