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
package dev.enola.model.enola.maven.connect.mima;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 * GAVR is a Maven GroupID, ArtifactID, Version, Extension (AKA Type), Classifier + Repository.
 *
 * <p>The GroupID, ArtifactID & Version are mandatory and cannot be empty. The Extension, Classifier
 * & Repository can be empty, but never null.
 *
 * <p>This class itself does NOT imply any "defaults" for Extension, Classifier & Repository; but
 * it's users may well.
 */
public record GAVR(
        String groupId,
        String artifactId,
        String extension,
        String classifier,
        String version,
        String repo) {

    // TODO Consider #performance - make this a class to cache Gradle & PkgURL representations?

    /**
     * Parse a "short Gradle-style" GAV in the
     * <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version> format, like e.g.
     * "ch.vorburger.mariaDB4j:mariaDB4j-core:3.1.0" to a GAVR. NB: This syntax does not allow
     * specifying a repository!
     */
    public static GAVR parseGradle(String gav) {
        var artifact = new DefaultArtifact(gav);
        return new GAVR(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getExtension(),
                artifact.getClassifier(),
                artifact.getVersion(),
                "");
    }

    // TODO public GAVR parsePkgURL(String purl), with
    //   https://github.com/package-url/packageurl-java

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
        requireNonNull(extension, "extension");
        requireNonNull(classifier, "classifier");
        requireNonEmpty(version, "version");
        requireNonNull(repo, "repo");
    }

    private void requireNonNull(Object object, String field) {
        if (object == null) throw new IllegalStateException(field + " cannot be null");
    }

    private void requireNonEmpty(String string, String field) {
        if (isNullOrEmpty(string))
            throw new IllegalStateException(field + " cannot be null or empty");
    }

    /** Return a String in the same format that {@link #parseGradle(String)} uses. */
    @SuppressWarnings("StringBufferReplaceableByString") // pre-sizing is more efficient (?)
    public String toGradle() {
        var sb = // w.o. repo.length()
                new StringBuilder(
                        4 // max. 4x ':'
                                + groupId.length()
                                + artifactId.length()
                                + extension.length()
                                + classifier.length()
                                + version.length());

        sb.append(groupId);
        sb.append(':');
        sb.append(artifactId);

        if (!extension.isEmpty()) {
            sb.append(':');
            sb.append(extension);
        }

        if (!classifier.isEmpty()) {
            sb.append(':');
            sb.append(classifier);
        }

        sb.append(':');
        sb.append(version);
        return sb.toString();
    }

    // TODO String toPkgURL()

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
