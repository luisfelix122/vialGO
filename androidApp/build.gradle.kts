import java.util.Properties

plugins {
    id("vialgo.android.application")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

// Load local.properties for secrets not committed to VCS
val localProperties = Properties().also { props ->
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        props.load(localFile.inputStream())
    }
}

fun secret(key: String): String =
    System.getenv(key)
        ?: localProperties.getProperty(key)
        ?: throw GradleException(
            "Missing required build secret: '$key'. " +
            "Add it to local.properties or set the environment variable."
        )

android {
    namespace = "com.vialgo.app"

    defaultConfig {
        applicationId = "com.vialgo.app"
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "SUPABASE_URL", "\"${secret("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${secret("SUPABASE_ANON_KEY")}\"")
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":shared"))

    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.compose.navigation)

    // Lifecycle
    implementation(libs.bundles.lifecycle)
    implementation(libs.activity.compose)

    // Koin BOM
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin.android)

    // Supabase BOM + client artifacts
    implementation(platform(libs.supabase.bom))
    implementation(libs.bundles.supabase)
    implementation(libs.bundles.ktor)

    // Firebase BOM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // Media3
    implementation(libs.bundles.media3)

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Debug
    debugImplementation(libs.compose.ui.tooling)

    // Test
    testImplementation(libs.kotlin.test)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}
