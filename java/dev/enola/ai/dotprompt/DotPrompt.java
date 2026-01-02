/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.ai.dotprompt;

import dev.enola.common.io.object.WithSchema;
import dev.enola.common.template.Template;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Dot Prompt struct, see <a
 * href="https://google.github.io/dotprompt/reference/frontmatter/">Spec</a> and the (TypeScript) <a
 * href="https://github.com/google/dotprompt/blob/main/js/src/types.ts">Reference
 * Implementation</a>.
 *
 * @author <a href="http://www.vorburger.ch">Michael Vorburger.ch</a>
 */
public class DotPrompt extends WithSchema {

    // TODO Rethink @Nullable ... it's a mess because some fields MAY be null
    //   initially, e.g. in a YAML frontmatter, but then WONT be null after DotPromptLoader;
    //   should there be 2 different (sub?)classes - just because of that?!

    // This is a class instead of a record to allow users to extend it.

    // This is a trivial "struct" instead of a (*Builder) "bean", with getters and setters,
    // because... there is really no need for that, here - this is totally fine and enough.

    /** The URL of where this DotPrompt originated. */
    public @Nullable URI id;

    /**
     * The name of the prompt. If not specified, it will be inferred from the filename in the URL of
     * the loaded prompt (e.g. {@code http://example.org/stuff/example.prompt.md} has an inferred
     * name of {@code example}).
     */
    public @Nullable String name;

    /**
     * The variant name for the prompt. If null, then inferred from the filename in the URL of the
     * loaded prompt (e.g. {@code http://example.org/stuff/example.variant1.prompt.md} has an
     * inferred name of {@code example} and inferred variant of {@code variant1}).
     */
    // TODO public @Nullable String variant;

    /**
     * The name of the model to use for this prompt, based on the <a
     * href="https://docs.enola.dev/specs/aiuri/">Enola.dev AI URI specification</a>; so e.g.,
     * {@code google://?model=gemini-2.5-flash}. May be omitted, in which case a default model will
     * be used.
     */
    public @Nullable String model;

    /**
     * Configuration to be passed to the model. The specific options may vary depending on the
     * model. Note that version, temperature, topK, topP & maxOutputTokens are already specified as
     * query parameters in the model AI URI instead of here.
     */
    // TODO public final Map<String, Object> config = new HashMap<>();

    /** Names of registered tools to allow use of in this prompt. */
    public final Set<String> tools = new HashSet<>();

    /** Arbitrary metadata to be used by code, tools, and libraries. */
    // TODO public final Map<String, Object> metadata = new HashMap<>();

    /** Defines the (schema of the) input variables the prompt. */
    public @Nullable Input input;

    /** Defines the expected model output format. */
    public @Nullable Output output;

    /**
     * Template of Prompt, as text.
     *
     * <p>This is typically in the body (after the YAML frontmatter) of a .prompt file.
     */
    public @Nullable String prompt;

    /** Template of Prompt, as Template (from parsed {@link #prompt}). */
    public @Nullable Template template;

    // TODO How to class input Map<String, Object> extends Map<String, Object> ?!
    public static class Input {

        /**
         * Defines the default input variable values to use if none are provided. Input values
         * passed from the implementation should be merged into these values with a shallow merge
         * strategy.
         */
        // TODO @JsonProperty("default") // cauz "default" is a reserved keyword
        // TODO https://github.com/google/dotprompt/issues/306 re. Map<String, Object>
        // TODO public final Map<String, Object> defaults = new HashMap<>();

        /**
         * Schema representing the input values for the prompt. Must correspond to a JSON Schema
         * {@code object} type.
         */
        // TODO Validate by parsing with JSON Schema parser, not (just) text to basic JSON Map
        public final Map<String, Object> schema = new HashMap<>();

        // TODO public @Nullable URI schemaRef;
    }

    public static class Output {

        public enum Format {
            json,
            text
        }

        /**
         * Desired output format for this prompt. Implicitly set to JSON if the schema is specified.
         */
        // TODO Remove? ADK doesn't seem to support this; it just has LlmAgent.outputSchema(Schema)
        public Format format = Format.text;

        /**
         * Schema representing the expected output from the prompt. Must correspond to a JSON Schema
         * {@code object} type.
         */
        // TODO Validate by parsing with JSON Schema parser, not (just) text to basic JSON Map
        public final Map<String, Object> schema = new HashMap<>();

        // TODO public @Nullable URI schemaRef;

        // TODO public @Nullable String outputKey;
    }
}
