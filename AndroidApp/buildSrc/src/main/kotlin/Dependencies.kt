import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {

    const val roomRuntime = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"

    const val hiltAgp = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val hiltCompose = "androidx.hilt:hilt-navigation-compose:${Versions.hiltCompose}"

    const val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}"
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigationCompose}"

    const val androidXCoreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val androidXLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeKtx}"
    const val androidXActivity = "androidx.activity:activity-compose:${Versions.activityCompose}"

    const val composeUi = "androidx.compose.ui:ui"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeMaterial3 = "androidx.compose.material3:material3"
    const val composeMaterial = "androidx.compose.material:material"
    const val composeLiveData = "androidx.compose.runtime:runtime-livedata"
    const val composeDebugUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeDebugUiTest = "androidx.compose.ui:ui-test-manifest"

    const val composeBOM = "androidx.compose:compose-bom:${Versions.composeBOM}"
    const val permissionRequest = "com.google.accompanist:accompanist-permissions:${Versions.accompanist}"

    const val testJUnit = "junit:junit:${Versions.testJUnit}"
    const val testJUnitJupiter = "org.junit.jupiter:junit-jupiter:${Versions.testJUnitJupiter}"
    const val testKotlinxCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test"
    const val androidTestExt = "androidx.test.ext:junit:${Versions.androidTestExt}"
    const val androidTestEspresso = "androidx.test.espresso:espresso-core:${Versions.androidTestEspresso}"
    const val androidTestComposeJuint = "androidx.compose.ui:ui-test-junit4"

    const val retrofit2 = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
}

fun DependencyHandler.androidX() {
    implementation(Dependencies.androidXCoreKtx)
    implementation(Dependencies.androidXLifecycle)
    implementation(Dependencies.androidXActivity)
}

fun DependencyHandler.compose() {
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeUiGraphics)
    implementation(Dependencies.composeUiToolingPreview)
    implementation(Dependencies.composeMaterial3)
    implementation(Dependencies.composeLiveData)
    implementation(Dependencies.composeMaterial)

    implementation(Dependencies.permissionRequest)
}

fun DependencyHandler.composeDebug() {
    debug(Dependencies.composeDebugUiTooling)
    debug(Dependencies.composeDebugUiTest)
}

fun DependencyHandler.room() {
    implementation(Dependencies.roomRuntime)
    implementation(Dependencies.roomKtx)
    kapt(Dependencies.roomCompiler)
}

fun DependencyHandler.hilt() {
    implementation(Dependencies.hiltAndroid)
    implementation(Dependencies.hiltCompose)
    kapt(Dependencies.hiltCompiler)
}

fun DependencyHandler.tests(){
    test(Dependencies.testJUnit)
    test(Dependencies.testJUnitJupiter)
    test(Dependencies.testKotlinxCoroutines)
    androidTest(Dependencies.androidTestExt)
    androidTest(Dependencies.androidTestEspresso)
    androidTest(Dependencies.androidTestComposeJuint)
}

fun DependencyHandler.retrofit(){
    implementation(Dependencies.retrofit2)
    implementation(Dependencies.retrofitConverter)
    implementation(Dependencies.okhttp)
    implementation(Dependencies.okhttpLogging)
}