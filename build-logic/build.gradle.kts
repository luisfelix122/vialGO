plugins {
    `kotlin-dsl`
}

group = "com.vialgo.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.9.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.3.21")
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "vialgo.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "vialgo.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
