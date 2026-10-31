# Keep-alive for SLF4J and Ktor base reflection classes
-keep class org.slf4j.** { *; }

# Preserve Compose and Kotlinx runtime classes
-keep class kotlinx.** { *; }
-keep class androidx.** { *; }
-keepattributes *Annotation*, Signature
