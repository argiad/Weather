import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.implementation(dependency: String) {
    add("implementation", dependency)
}

fun DependencyHandler.kapt(dependency: String) {
    add("kapt", dependency)
}

fun DependencyHandler.debug(dependency: String) {
    add("debugImplementation", dependency)
}

fun DependencyHandler.test(dependency: String) {
    add("testImplementation", dependency)
}

fun DependencyHandler.androidTest(dependency: String) {
    add("androidTestImplementation", dependency)
}