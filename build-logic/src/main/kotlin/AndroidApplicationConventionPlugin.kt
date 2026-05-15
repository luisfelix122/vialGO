import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            tasks.withType(KotlinCompile::class.java).configureEach {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 36

                defaultConfig {
                    minSdk = 26
                    targetSdk = 35
                }

                compileOptions {
                    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
                    targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
                }


                signingConfigs {
                    create("release") {
                        val keystorePath = System.getenv("VIALGO_KEYSTORE_PATH")
                            ?: project.findProperty("VIALGO_KEYSTORE_PATH") as? String

                        if (keystorePath == null) {
                            // Signing config is optional for debug builds.
                            // assembleRelease will fail at signing step with a clear message
                            // because storeFile will be null.
                            logger.warn(
                                "VIALGO_KEYSTORE_PATH is not set. Release signing is disabled. " +
                                "Set VIALGO_KEYSTORE_PATH, VIALGO_KEYSTORE_PASSWORD, " +
                                "VIALGO_KEY_ALIAS and VIALGO_KEY_PASSWORD to enable it."
                            )
                        } else {
                            storeFile = file(keystorePath)
                            storePassword = System.getenv("VIALGO_KEYSTORE_PASSWORD")
                                ?: project.findProperty("VIALGO_KEYSTORE_PASSWORD") as? String
                                ?: throw GradleException(
                                    "VIALGO_KEYSTORE_PASSWORD is required when VIALGO_KEYSTORE_PATH is set."
                                )
                            keyAlias = System.getenv("VIALGO_KEY_ALIAS")
                                ?: project.findProperty("VIALGO_KEY_ALIAS") as? String
                                ?: throw GradleException(
                                    "VIALGO_KEY_ALIAS is required when VIALGO_KEYSTORE_PATH is set."
                                )
                            keyPassword = System.getenv("VIALGO_KEY_PASSWORD")
                                ?: project.findProperty("VIALGO_KEY_PASSWORD") as? String
                                ?: throw GradleException(
                                    "VIALGO_KEY_PASSWORD is required when VIALGO_KEYSTORE_PATH is set."
                                )
                        }
                    }
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        val releaseSigningConfig = signingConfigs.findByName("release")
                        if (releaseSigningConfig?.storeFile != null) {
                            signingConfig = releaseSigningConfig
                        } else {
                            // Fail fast with a descriptive error if release is requested without keys
                            tasks.whenTaskAdded {
                                if (name.contains("Release", ignoreCase = true) &&
                                    name.startsWith("assemble")
                                ) {
                                    doFirst {
                                        throw GradleException(
                                            "Cannot assemble release build: VIALGO_KEYSTORE_PATH " +
                                            "environment variable is not set. " +
                                            "Set VIALGO_KEYSTORE_PATH, VIALGO_KEYSTORE_PASSWORD, " +
                                            "VIALGO_KEY_ALIAS and VIALGO_KEY_PASSWORD."
                                        )
                                    }
                                }
                            }
                        }
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                    getByName("debug") {
                        isMinifyEnabled = false
                        // No applicationIdSuffix — google-services.json only has com.vialgo.app.
                        // Add .debug suffix once Firebase is configured for the debug app ID.
                    }
                }

                buildFeatures {
                    compose = true
                    buildConfig = true
                }
            }
        }
    }
}
