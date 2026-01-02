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
package dev.enola.common.io.testlib;

import static com.google.common.truth.Truth.assertAbout;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

import dev.enola.common.canonicalize.Canonicalizer;
import dev.enola.common.diff.testlib.DiffingStringSubject;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.yamljson.JSON;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

/**
 * <a href="https://github.com/google/truth">Google Truth</a> custom <code>Subject</code> for {@link
 * dev.enola.common.io.resource.Resource}.
 *
 * <p>Resource content is <i>canonicalized</i> (AKA <i>normalized</i>), based on <code>mediaType
 * </code>, before comparisons.
 */
public final class ResourceSubject extends Subject {

    private static final ResourceProvider rp = new ClasspathResource.Provider();
    private static final Canonicalizer canonicalizer = new Canonicalizer(rp);

    public static ResourceSubject assertThat(@Nullable ReadableResource actual) {
        return assertAbout(resources()).that(actual);
    }

    private static Factory<ResourceSubject, ReadableResource> resources() {
        return ResourceSubject::new;
    }

    private final @Nullable ReadableResource actual;

    public ResourceSubject(FailureMetadata metadata, @Nullable ReadableResource actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    private String canonicalize(@Nullable ReadableResource resource) throws IOException {
        if (resource == null) return "";
        if (resource.byteSource().isEmpty()) return "";
        var canonicalized = new MemoryResource(resource.uri(), resource.mediaType());
        canonicalizer.canonicalize(resource, canonicalized, true);
        return canonicalized.charSource().read();
    }

    // TODO drop *Chars* - after making it work for any Resource - even just binary
    public void hasCharsEqualTo(@Nullable ReadableResource expected) throws IOException {
        var actualChars = canonicalize(actual);
        var expectedChars = canonicalize(expected);
        check(actualChars).that(actualChars).isEqualTo(expectedChars);
    }

    // TODO Improve confusing output for multiline diff
    public void hasCharsEqualToOrDiff(@Nullable ReadableResource expected) throws IOException {
        var actualChars = canonicalize(actual);
        var expectedChars = canonicalize(expected);
        DiffingStringSubject.assertThat(actualChars).isEqualTo(expectedChars);
    }

    // TODO drop *Chars* - after making it work for any Resource - even just binary
    public void containsCharsOf(@Nullable ReadableResource expected) throws IOException {
        var actualChars = canonicalize(actual);
        var expectedChars = canonicalize(expected);
        if (expectedChars.isBlank())
            throw new IllegalArgumentException("BLANK " + (expected != null ? expected : ""));
        check("charSource").that(actualChars).contains(expectedChars);
    }

    // TODO Make sure canonicalize always either adds or does not add EOL, and get rid of this!
    public void containsCharsOfIgnoreEOL(@Nullable ReadableResource expected) throws IOException {
        var actualChars = canonicalize(actual);
        var expectedChars = canonicalize(expected);
        if (expectedChars.isBlank())
            throw new IllegalArgumentException("BLANK " + (expected != null ? expected : ""));
        check("charSource").that(trimLineEndWhitespace(actualChars)).contains(expectedChars);
    }

    /**
     * @deprecated Normally, this should be able to be replaced with hasCharsEqualTo now?
     */
    @Deprecated
    // TODO Test if this everything works as intended and remove this if replacement is OK?
    public void hasJSONEqualTo(@Nullable ReadableResource expected) throws IOException {
        var actualChars = canonicalize(actual);
        var expectedChars = canonicalize(expected);
        check("charSourceAsJSON")
                .that(JSON.canonicalize(actualChars, true))
                .isEqualTo(JSON.canonicalize(expectedChars, true));
    }

    // TODO other checks?

    private String trimLineEndWhitespace(String string) {
        return string.replaceAll("(?m) +$", "");
    }
}
