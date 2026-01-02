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

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import com.google.common.graph.Traverser;

import org.junit.Test;

@SuppressWarnings("UnstableApiUsage")
public class ImmutableTreeBuilderTest {

    @Test
    public void root() {
        var treeBuilder = new ImmutableTreeBuilder<String>().root("🌳");
        assertThat(treeBuilder.root()).isEqualTo("🌳");
        assertThat(treeBuilder.successors("🌳")).isEmpty();
        assertThat(treeBuilder.successors("?")).isEmpty();

        var tree = treeBuilder.build();
        assertThat(tree.root()).isEqualTo("🌳");
        assertThat(tree.successors("🌳")).isEmpty();
        assertThat(tree.successors("?")).isEmpty();
    }

    /**
     * Test {@link TreeBuilder#addChild(Object, Object)}.
     *
     * <pre>
     *      A
     *     /|\
     *    B D E
     *    |   |
     *    C   F
     *        |
     *        G
     * </pre>
     */
    @Test
    public void addChild() {
        var treeBuilder = new ImmutableTreeBuilder<String>().root("A");
        treeBuilder.addChild("A", "B").addChild("B", "C").addChild("A", "D");
        treeBuilder.addChild("A", "E").addChild("E", "F").addChild("F", "G");
        var tree = treeBuilder.build();

        assertThat(Traverser.forTree(tree).depthFirstPreOrder("A"))
                .containsExactly("A", "B", "C", "D", "E", "F", "G")
                .inOrder();

        assertThat(Traverser.forTree(tree).breadthFirst("A"))
                .containsExactly("A", "B", "D", "E", "C", "F", "G")
                .inOrder();

        assertThat(Traverser.forTree(tree).depthFirstPostOrder("A"))
                .containsExactly("C", "B", "D", "G", "F", "E", "A")
                .inOrder();
    }

    @Test
    public void cycle() {
        var treeBuilder = new ImmutableTreeBuilder<String>().root("A");
        treeBuilder.addChild("A", "B");
        assertThrows(IllegalStateException.class, () -> treeBuilder.addChild("B", "A"));
    }

    @Test
    public void selfLink() {
        var treeBuilder = new ImmutableTreeBuilder<String>().root("A");
        treeBuilder.addChild("A", "B");
        assertThrows(IllegalStateException.class, () -> treeBuilder.addChild("B", "B"));
    }

    @Test
    public void noParent() {
        var treeBuilder = new ImmutableTreeBuilder<String>().root("A");
        assertThrows(IllegalStateException.class, () -> treeBuilder.addChild("X", "B"));
    }

    @Test
    public void root2() {
        var tree = new ImmutableTreeBuilder<String>().root("🌳");
        assertThrows(IllegalStateException.class, () -> tree.root("X"));
    }

    @Test
    public void root0() {
        assertThrows(IllegalStateException.class, () -> new ImmutableTreeBuilder<String>().build());
    }
}
