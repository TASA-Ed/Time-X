# Ktor's suspend UDP send path is miscompiled by the current R8 optimizer on Android.
-keep class io.ktor.network.sockets.DatagramSendChannel { *; }
