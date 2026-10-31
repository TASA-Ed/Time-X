# Keep-alive for SLF4J and Ktor base reflection classes
-keep class org.slf4j.** { *; }
-keep class io.ktor.network.sockets.DatagramSendChannel { *; }

# Preserve Compose and Kotlinx runtime classes
-keep class kotlinx.** { *; }
-keep class androidx.** { *; }
-keepattributes *Annotation*, Signature
