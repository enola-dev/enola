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
package dev.enola.common.io.iri;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HostAndPort;
import com.google.common.net.HostSpecifier;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

/**
 * URL is <a href="https://url.spec.whatwg.org">WHATWG URL Living Standard</a> inspired
 * implementation. That standard is the de-facto successor of RFC 3987 &amp; RFC 3986, which were
 * the successors that obsoleted the original RFCs 2396 (with more minor RFC RF 2732 in between
 * them).
 *
 * <p>This class is intentionally named the same as {@link java.net.URL}, because that class anyway
 * should never ever be used anymore in modern Java (<a
 * href="https://errorprone.info/bugpattern/URLEqualsHashCode">because its equals() and hashCode()
 * methods use blocking outgoing internet connections</a>).
 *
 * <p>This class strictly speaking represents an <i>URL <b>Reference</b></i>, not just an
 * <i>URL</i>; meaning that it can either an absolute with a <code>scheme:</code>, or relative.
 *
 * <p>This class is logically (but not technically, for efficiency) immutable. It has a {@link
 * Builder} to programmatically configure instances of it. You can also just construct it from a
 * String with {@link #parseUnencoded(String)}. To modify, use {@link #newBuilder()}, set what you
 * need, and {@link Builder#build()} it.
 *
 * <p>This class never throws any runtime exceptions for supposedly "invalid" input. It allows e.g.
 * "http://example.org/~{username}" (e.g. URI Templates à la RFC 6570, or other similar syntaxes) or
 * (Glob-like) "**.{txt,json,yaml}" or "?.txt" or "[a-c].txt", etc. It may however not always parse
 * a String input as you intended... ;-) Jokes apart, if you must validate, you can - with {@link
 * #validate()}; this may throw an an exception. (The {@link #toURI()} method is the only other one
 * which <code>throws
 * </code> - naturally.)
 *
 * <p>This class leaves parsing (decoding) its {@link #authority()} to host/IPv4 &amp; IPv6/port,
 * and IDNA for host, up to others. E.g. Guava's {@link HostAndPort} and {@link
 * com.google.common.net.InternetDomainName} and {@link com.google.common.net.InetAddresses} may be
 * useful; they are used by {@link #validate()}.
 *
 * <p>This class never ever by itself changes an URL you construct based on a String input.
 *
 * <p>This class can {@link #normalize()}! But {@link #equals(Object)} does <b>*NOT*</b> normalize!
 * (This makes it suitable for use in RDF-like applications; see e.g. <a
 * href="https://en.wikipedia.org/wiki/Uniform_Resource_Identifier">Wikipedia</a> for background
 * reading.) You <i>can</i> use {@link #equalsNormalized(URL)}, if you must.
 *
 * <p>This class does not yet directly support Windows Drive Letter scheme; you're welcome to add
 * support for that, if you need it.
 *
 * <p>TODO TBC How does this class deal with escaping? AVOID URI's getRawXYZ() methods!
 *
 * <p>This class accepts International Domain Names (IDN) in the host part of an authority, but it
 * does not yet have RFC 3490 Puny Code conversion support; as in, it cannot (itself) transform e.g.
 * "https://☃.net" (a Snowman!) to "https://xn--n3h.net/" in {@link #normalize()}; you're welcome to
 * add support for that, if you need it.
 *
 * <p>This class if null-safe. Its accessor methods never return null, but empty Strings instead.
 *
 * <p>This class never makes any network access! (Yes, looking at you, {@link
 * java.net.URL#equals(Object)} - OMG!)
 *
 * <p>This class does not know about any specific schemes, and doesn't treat e.g. http: different
 * from any other scheme. There are no hard-coded default ports or anything like that here. (TODO
 * except to {@link #normalize()} e.g. ports, maybe?) TODO Re-read and re-think about if this really
 * makes sense, and is Spec compliant? This may not really work?
 *
 * <p>This class is performance efficient, and parses lazily, not (possibly unnecessarily) ahead of
 * time.
 *
 * <p>This class is memory efficient, and uses as little Java heap space as possible.
 *
 * <p>This class is intentionally final and not extensible.
 *
 * <p>This class has no dependencies on any other framework, and could be single-file copy/pasted!
 * TODO For realz?! ;-) Or do depend on (just) Guava? It's handy e.g. for Multimap...
 *
 * @author <a href="https://www.vorburger.ch">Michael Vorburger.ch</a> originally wrote this class
 *     for <a href="https://enola.dev">Enola.dev</a>.
 */
public final class URL implements Comparable<URL> {
    // TODO extends IRI

    // TODO If you are reading this, please help us to make this class stop lying about itself? ;-)
    // It currently doesn't really do what's described above - yet... but you can help to make it!

    // TODO Actually fully read https://url.spec.whatwg.org first.. :=)

    // TODO Research existing implementations for inspiration...
    // - https://github.com/square/okhttp/issues/1486
    // - https://github.com/palominolabs/url-builder
    // - https://github.com/dmfs/uri-toolkit
    // - https://github.com/RustedBones/capturl
    // - https://github.com/lemonlabsuk/scala-uri
    // - others?

    // TODO Add more shit to URLTest and make it pass

    // TODO Consider using CharSequence instead of String, for substring performance?

    // TODO Use com.google.common.net.UrlEscapers where appropriate

    // TODO Test with https://jsdom.github.io/whatwg-url/

    // TODO Test against https://github.com/web-platform-tests/wpt/tree/master/url

    // TODO See if there is interest in getting this accepted into Guava
    // see https://github.com/google/guava/issues/1005
    // and https://github.com/google/guava/issues/1691
    // and https://github.com/google/guava/issues/1756
    // et al.

    // TODO Factor out into separate GitHub repo, e.g. https://github.com/vorburger/jiri

    // TODO Get listed on https://url.spec.whatwg.org ?

    // TODO What's the right naming - encoded, or escaped?
    public static URL parseUnencoded(String unencoded) {
        var url = new URL();
        url.string = Objects.requireNonNull(unencoded);
        return url;
    }

    // TODO public static URL fromEscaped(String unencoded, Charset cs) {
    // see e.g. https://github.com/lemonlabsuk/scala-uri?tab=readme-ov-file#character-sets why CS

    public static URL from(URI uri) {
        // TODO Use getRawXYZ() here, or non-raw?!
        return builder()
                .scheme(uri.getScheme())
                .authority(uri.getAuthority())
                .path(uri.getPath())
                .query(uri.getQuery())
                .fragment(uri.getFragment())
                .build();
        // TODO Or?! return of(uri.toString());
        // TODO Or?! return of(uri.toASCIIString());
    }

    /**
     * Returns a new {@link Builder}. This method exists purely for convenience for folks used to
     * typing <code>URL#builder()</code> (e.g. as is popular in generated <a
     * href="https://protobuf.dev">Protocol Buffers</a> and similar code) instead of <code>
     * new URL.Builder()</code> - they are equivalent.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        // Keep this in sync with below!
        private @Nullable String scheme;
        private @Nullable String authority;
        private @Nullable String path;
        // TODO private @Nullable List<String> paths;
        private @Nullable String query;
        // TODO private @Nullable Multimap<String, String> queryMap;
        private @Nullable String fragment;

        public URL build() {
            var url = new URL();
            url.scheme = scheme;
            url.authority = authority;
            url.path = path;
            // TODO url.paths = paths;
            url.query = query;
            // TODO url.queryMap = queryMap;
            url.fragment = fragment;
            return url;
        }

        @CanIgnoreReturnValue
        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder authority(String authority) {
            this.authority = authority;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder fragment(String fragment) {
            this.fragment = fragment;
            return this;
        }
    }

    // TODO Save memory by using Object which is either String or a Parsed..
    // Keep this in sync with above!
    private @Nullable String scheme;
    private @Nullable String authority;
    private @Nullable String path;
    // TODO private @Nullable List<String> paths;
    private @Nullable String query;
    // TODO private @Nullable Multimap<String, String> queryMap;
    private @Nullable String fragment;
    private @Nullable String string;

    public Builder newBuilder() {
        return new Builder();
    }

    public String scheme() {
        if (scheme == null) scheme = find_scheme();
        return scheme;
    }

    private String find_scheme() {
        return null; // TODO
    }

    public boolean hasScheme(String scheme) {
        // TODO Implement more optimized
        return scheme().equals(scheme);
    }

    public boolean isAbsolute() {
        return !scheme().isBlank();
    }

    // /** Scheme specific part is just everything after the : colon of the scheme. */
    /* public CharSequence schemeSpecificPart() {
        return null; // TODO
    } */

    public String authority() {
        return null; // TODO
    }

    public String path() {
        return null; // TODO
    }

    public String query() {
        return null; // TODO
    }

    public String fragment() {
        return null; // TODO
    }

    // TODO Allow both & and ; as query delimiters?!
    public ImmutableMultimap<String, String> queryMap() {
        return null; // TODO
    }

    public ImmutableMultimap<String, String> queryParameter(String key) {
        return null; // TODO
    }

    public URL base() {
        return null; // TODO as in URIs.base()
    }

    /** Resolve, e.g. as in {@link URI#resolve(URI)}. */
    public URL resolve(URL url) {
        return null; // TODO
    }

    /** Resolve, e.g. as in {@link URI#resolve(URI)}. */
    public URL resolve(String string) {
        return resolve(URL.parseUnencoded(string));
    }

    /** Relativize, e.g. as in {@link URI#relativize(URI)}. */
    public URL relativize(URL url) {
        return null; // TODO
    }

    @Override
    public String toString() {
        if (string == null) string = stringify();
        return string;
    }

    private String stringify() {
        return "TODO";
    }

    public URI toURI() throws URISyntaxException {
        return new URI(toString());
    }

    public URL normalize() {
        // TODO Keep result in a lazily initialized field? But... memory?!
        var builder = newBuilder();
        builder.scheme(scheme().toLowerCase(Locale.ROOT));
        // TODO ... FIXME
        // TODO Should we drop default ports for a few well-known schemes?
        return builder.build();
    }

    /** Equality check, with {@link #normalize()}-ation. */
    public boolean equalsNormalized(URL o) {
        return this.normalize().toString().equals(o.normalize().toString());
    }

    /**
     * Equality check, based on {@link #toString()}.
     *
     * <p>This does <b>NOT</b> {@link #normalize()}! See {@link #equalsNormalized(URL)}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Comparison, based purely on {@link #toString()}. This does <b>NOT</b> {@link #normalize()}!
     */
    @Override
    public int compareTo(URL o) {
        return toString().compareTo(o.toString());
    }

    public void validate() throws ValidationException {
        var scheme = scheme();
        if (isAbsolute() && scheme.isBlank()) throw new ValidationException(this, "Blank scheme");
        if (isAbsolute() && !CharAscii.INSTANCE.matchesAllOf(scheme))
            throw new ValidationException(this, "Invalid scheme: " + scheme);

        try {
            var authority = authority();
            if (!authority.isBlank())
                validateHostname(HostAndPort.fromString(authority()).getHost());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(this, "Invalid authority", e);
        }

        // TODO ...
    }

    private void validateHostname(String host) throws ValidationException {
        try {
            HostSpecifier.from(host);
        } catch (ParseException e) {
            throw new ValidationException(this, "Host invalid", e);
        }
        // TODO Is this still required, or did HostSpecifier.from() already do (exactly?!) this...
        if (InetAddresses.isUriInetAddress(host)) return;
        if (!InternetDomainName.isValid(host))
            throw new ValidationException(
                    this,
                    "Host is neither an URI IP Address nor a valid Internet Domain Name (IDN): "
                            + host);
    }

    private URL() {}

    private static final class CharAscii extends CharMatcher {

        static final CharMatcher INSTANCE = new CharAscii();

        @Override
        public boolean matches(char c) {
            // https://url.spec.whatwg.org/#url-representation *permits* upper-case
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
        }
    }

    public static class ValidationException extends Exception {
        // This was originally loosely inspired by java.net.URISyntaxException

        private final URL url;

        private ValidationException(URL url, String reason) {
            super(reason);
            this.url = url;
        }

        public ValidationException(URL url, String reason, Exception e) {
            super(reason, e);
            this.url = url;
        }

        public String getReason() {
            return super.getMessage();
        }

        public URL getURL() {
            return url;
        }

        @Override
        public String getMessage() {
            return getReason() + ": " + getURL();
        }
    }
}
