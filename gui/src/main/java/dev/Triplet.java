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

public class Triplet<T,U,V> {
    public T t;
    public U u;
    public V v;
    public static <T,U,V> Triplet<T,U,V> with(T t, U u, V v) {
        return new Triplet(t,u,v);
    }
    public Triplet(T t, U u, V v){
        this.t = t;
        this.u = u;
        this.v = v;
    }
}
