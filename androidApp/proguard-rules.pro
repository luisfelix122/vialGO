# ============================================================
# VialGo ProGuard / R8 Rules
# ============================================================

# --- kotlinx.serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.vialgo.app.**$$serializer { *; }
-keepclassmembers class com.vialgo.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.vialgo.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- VialGo DTOs (never strip) ---
-keep class com.vialgo.app.datos.dtos.** { *; }

# --- Supabase-kt ---
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# --- Ktor ---
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keepclassmembers class io.ktor.** { volatile <fields>; }

# --- Koin ---
-keep class org.koin.** { *; }
-dontwarn org.koin.**
-keepnames class * extends org.koin.core.module.Module

# --- Coil ---
-keep class coil3.** { *; }
-dontwarn coil3.**

# --- Kotlin coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# --- Firebase ---
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# --- Compose Navigation type-safe routes (@Serializable data objects/classes) ---
-keep @kotlinx.serialization.Serializable class com.vialgo.app.presentacion.navegacion.** { *; }
-keepnames @kotlinx.serialization.Serializable class com.vialgo.app.presentacion.navegacion.**

# --- Enum classes ---
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
