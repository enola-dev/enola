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
package dev.enola.common.secret.auto;

import java.util.Locale;

class DesktopDetector {

    enum DesktopEnvironment {
        GNOME,
        KDE,
        XFCE,
        UNITY,
        OTHER,
        UNKNOWN
    }

    static DesktopEnvironment detectDesktopEnvironment() {
        // XDG_CURRENT_DESKTOP is the most standard way according to Freedesktop.org specs
        String currentDesktop = System.getenv("XDG_CURRENT_DESKTOP");
        if (currentDesktop != null) return detectDesktopEnvironment(currentDesktop);

        // DESKTOP_SESSION is an older variable but still commonly used
        String desktopSession = System.getenv("DESKTOP_SESSION");
        if (desktopSession != null) return detectDesktopEnvironment(desktopSession);

        return DesktopEnvironment.UNKNOWN;
    }

    private static DesktopEnvironment detectDesktopEnvironment(String desktop) {
        String lowerDesktopSession = desktop.toLowerCase(Locale.ENGLISH);
        if (lowerDesktopSession.contains("gnome")) {
            return DesktopEnvironment.GNOME;
        }
        if (lowerDesktopSession.contains("kde")) {
            return DesktopEnvironment.KDE;
        }
        if (lowerDesktopSession.contains("xfce")) {
            return DesktopEnvironment.XFCE;
        }
        if (lowerDesktopSession.contains("unity")) {
            return DesktopEnvironment.UNITY;
        }
        return DesktopEnvironment.OTHER;
    }
}
