/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.connect.maven;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.PackageURLBuilder;
import com.google.common.base.Strings;

import eu.maveniverse.maven.mima.extensions.mmr.ModelResponse;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import java.util.Map;

/**
 * GAVR is a Maven GroupID, ArtifactID, Version, Extension (AKA Type), Classifier + Repository.
 *
 * <p>The GroupID, ArtifactID &amp; Version are mandatory and cannot be empty. The Extension,
 * Classifier &amp; Repository can be empty (but never null).
 *
 * <p>The Maven default extension "jar" are hidden in the GAV coordinates and Package URL syntax
 * (but is returned by {@link #extension()} API). This is different from Maven core, which always
 * shows "jar".
 *
 * <p>This class itself does NOT imply any other "defaults" for Classifier &amp; Repository. Callers
 * of this class may resolve a GAVR without repo to one with a repo using {@link
 * Mima#origin(ModelResponse)}.
 */
public record GAVR(
        String groupId,
        String artifactId,
        String extension,
        String classifier,
        String version,
        String repo) {

    // TODO Consider #performance - make this a class to cache GAV & PkgURL its representations?

    /**
     * Parse a coordinates in the {@code
     * <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>} format, like e.g.
     * "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0" to a GAVR.
     *
     * <p>This format is not a URL; use {@link #parsePkgURL(String)} if you have a URL, or {@link
     * #toPkgURL()} if you want one.
     *
     * <p>PS: This syntax does not allow specifying a repository!
     */
    public static GAVR parseGAV(String gav) {
        var artifact = new DefaultArtifact(gav);
        return new GAVR(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getExtension(),
                artifact.getClassifier(),
                artifact.getVersion(),
                "");
    }

    /** Return a String in the same format that {@link #parsePkgURL(String)} uses. */
    public String toPkgURL() {
        var builder = PackageURLBuilder.aPackageURL();
        builder.withType("maven");
        builder.withNamespace(groupId);
        builder.withName(artifactId);
        builder.withVersion(version);
        if (!extension.isEmpty() && !"jar".equals(extension)) {
            builder.withQualifier("type", extension);
        }
        if (!classifier.isEmpty()) {
            builder.withQualifier("classifier", classifier);
        }
        if (!repo.isEmpty()) {
            builder.withQualifier("repository_url", repo);
        }
        try {
            return builder.build().canonicalize();
        } catch (MalformedPackageURLException e) {
            throw new IllegalStateException(toGAV(), e);
        }
    }

    /**
     * Parse a Maven Package URL. For example,
     * "pkg:maven/ch.vorburger.mariaDB4j/mariaDB4j-core@3.1.0?classifier=javadoc". See <a
     * href="https://github.com/package-url/purl-spec/blob/master/PURL-TYPES.rst#maven">Type
     * definition</a> and its underlying <a
     * href="https://spdx.github.io/spdx-spec/v3.0.1/annexes/pkg-url-specification/">SPDX
     * specification</a>.
     */
    public static GAVR parsePkgURL(String purl) {
        try {
            var p = new PackageURL(purl);
            var q = p.getQualifiers();
            if (!"maven".equals(p.getType())) throw new IllegalArgumentException(purl);
            // https://github.com/package-url/purl-spec/blob/master/PURL-TYPES.rst#maven
            return new GAVR(
                    p.getNamespace(),
                    p.getName(),
                    get(q, "type"),
                    get(q, "classifier"),
                    p.getVersion(),
                    get(q, "repository_url"));

        } catch (MalformedPackageURLException e) {
            throw new IllegalArgumentException(purl, e);
        }
    }

    private static String get(Map<String, String> q, String key) {
        if (q == null) return "";
        var value = q.get(key);
        return Strings.nullToEmpty(value);
    }

    public static class Builder {
        private String groupId;
        private String artifactId;
        private String extension;
        private String classifier;
        private String version;
        private String repo;

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder artifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public Builder extension(String extension) {
            this.extension = extension;
            return this;
        }

        public Builder classifier(String classifier) {
            this.classifier = classifier;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder repo(String repo) {
            this.repo = repo;
            return this;
        }

        public GAVR build() {
            return new GAVR(
                    groupId,
                    artifactId,
                    nullToEmpty(extension),
                    nullToEmpty(classifier),
                    version,
                    nullToEmpty(repo));
        }

        private String nullToEmpty(String string) {
            if (string == null) return "";
            else return string;
        }
    }

    public GAVR {
        requireNonEmpty(groupId, "groupId");
        requireNonEmpty(artifactId, "artifactId");
        extension = useDefaultIfNullOrEmpty(extension, "jar");
        requireNonNull(classifier, "classifier");
        requireNonEmpty(version, "version");
        requireNonNull(repo, "repo");
    }

    private String useDefaultIfNullOrEmpty(String string, String defaultValue) {
        if (string == null) return defaultValue;
        if (string.isEmpty()) return defaultValue;
        return string;
    }

    private void requireNonNull(Object object, String field) {
        if (object == null) throw new IllegalStateException(field + " cannot be null");
    }

    private void requireNonEmpty(String string, String field) {
        if (isNullOrEmpty(string))
            throw new IllegalStateException(field + " cannot be null or empty");
    }

    /**
     * Return a String in the same format that {@link #parseGAV(String)} uses.
     *
     * <p>This omits extension "jar" when it can (contrary to how
     * org.eclipse.aether.artifact.AbstractArtifact#toString() does it).
     */
    public String toGAV() {
        var sb = // w.o. repo.length()
                new StringBuilder(
                        4 // max. 4x ':'
                                + groupId.length()
                                + artifactId.length()
                                + extension.length()
                                + classifier.length()
                                + version.length());

        sb.append(groupId).append(':').append(artifactId);

        if ((!extension.isEmpty() && !"jar".equals(extension))
                || ("jar".equals(extension) && !classifier.isEmpty()))
            sb.append(':').append(extension);

        if (!classifier.isEmpty()) sb.append(':').append(classifier);

        sb.append(':');
        sb.append(version);
        return sb.toString();
    }

    public Builder toBuilder() {
        return new Builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .extension(extension)
                .classifier(classifier)
                .version(version);
    }

    // NOT public - this is a package private internal method!
    // NB: Artifact does NOT include the Repository! Callers will use repository() to obtain that.
    Artifact toArtifact() {
        return new DefaultArtifact(groupId, artifactId, classifier, extension, version);
    }
}
