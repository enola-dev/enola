
plugins {
    id("dev.enola.java-library-conventions")
}

dependencies {
    // TODO: Move "base dependencies" into build-logic/src/main/kotlin/dev.enola.java-common-conventions.gradle.kts
    implementation(libs.slf4j)
    implementation(libs.guava)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
}
