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
package dev.enola.common.tree;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ImmutableTypeParameter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class ImmutableTreeBuilder<@ImmutableTypeParameter N> implements TreeBuilder<N>, Tree<N> {

    private @Nullable N root;
    private final Set<N> nodes = new HashSet<>();
    private final Map<@NonNull N, ImmutableList.Builder<N>> map = new HashMap<>();

    @Override
    @CanIgnoreReturnValue
    public ImmutableTreeBuilder<N> root(N root) {
        if (this.root != null) throw new IllegalStateException("Root already set: " + root);
        this.root = requireNonNull(root, "root");
        map.put(root, ImmutableList.builder());
        nodes.add(root);
        return this;
    }

    @Override
    public N root() {
        if (this.root == null) throw new IllegalStateException("Root not set");
        return root;
    }

    @Override
    @CanIgnoreReturnValue
    public ImmutableTreeBuilder<N> addChild(N parent, N child) {
        if (nodes.contains(child))
            throw new IllegalStateException("Tree already contains child: " + child);
        var entry = map.get(parent);
        if (entry == null) throw new IllegalStateException("Parent Node not found: " + parent);
        entry.add(child);
        map.put(child, ImmutableList.builder());
        nodes.add(child);
        return this;
    }

    @Override
    public Tree<N> build() {
        if (this.root == null) throw new IllegalStateException("Root not set");
        var builder =
                ImmutableMap.<@NonNull N, ImmutableList<N>>builderWithExpectedSize(map.size());
        map.forEach((node, children) -> builder.put(node, children.build()));
        return new ImmutableListTree<>(root, builder.build());
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public Iterable<? extends N> successors(N node) {
        var successorsBuilder = map.get(node);
        if (successorsBuilder != null) return successorsBuilder.build();
        else return ImmutableList.of();
    }

    private record ImmutableListTree<@ImmutableTypeParameter N>(
            N root, ImmutableMap<@NonNull N, ImmutableList<N>> map) implements ImmutableTree<N> {

        @Override
        @SuppressWarnings("UnstableApiUsage")
        public Iterable<? extends N> successors(N node) {
            return requireNonNull(map.getOrDefault(node, ImmutableList.of()), "TODO Annotate?");
        }
    }
}
