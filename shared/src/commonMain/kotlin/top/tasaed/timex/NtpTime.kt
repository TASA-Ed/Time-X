package top.tasaed.timex

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlin.time.Instant
import kotlin.time.TimeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

private const val NtpPort = 123
private const val NtpPacketSize = 48
private const val NtpEpochOffsetSeconds = 2_208_988_800L

internal suspend fun queryNtpTime(server: String, port: Int = NtpPort): Instant =
  withContext(Dispatchers.IO) {
    require(server.isNotBlank()) { "NTP server must not be blank" }

    val selectorManager = SelectorManager(Dispatchers.IO)
    try {
      val socket = aSocket(selectorManager).udp().bind()
      try {
        val request = Buffer()
        repeat(NtpPacketSize) { index ->
          request.writeByte(if (index == 0) 0x23.toByte() else 0.toByte())
        }
        val requestStartedAt = TimeSource.Monotonic.markNow()
        withTimeout(4_000L) {
          socket.send(Datagram(request, InetSocketAddress(server, port)))
          val response = socket.receive()
          val roundTrip = requestStartedAt.elapsedNow()
          val serverTransmitTime = parseNtpResponse(response.packet.readByteArray())
          serverTransmitTime + roundTrip / 2
        }
      } finally {
        socket.close()
      }
    } finally {
      selectorManager.close()
    }
  }

internal suspend fun queryNtpTimeDifference(
  server: String,
  port: Int = NtpPort,
): kotlin.time.Duration {
  val serverTime = queryNtpTime(server, port)
  val localTime = kotlin.time.Clock.System.now()
  return serverTime - localTime
}

internal fun parseNtpResponse(response: ByteArray): Instant {
  require(response.size >= NtpPacketSize) { "Invalid NTP response length" }
  val leapIndicator = (response[0].toInt() ushr 6) and 0x03
  val mode = response[0].toInt() and 0x07
  val stratum = response[1].toInt() and 0xff
  require(leapIndicator != 3) { "NTP server is unsynchronized" }
  require(mode == 4 || mode == 5) { "Invalid NTP response mode" }
  require(stratum in 1..15) { "Invalid NTP server stratum" }

  val ntpSeconds = readUnsignedInt(response, 40)
  val fraction = readUnsignedInt(response, 44)
  val eraSeconds = if (ntpSeconds < NtpEpochOffsetSeconds) 4_294_967_296L else 0L
  val unixSeconds = ntpSeconds + eraSeconds - NtpEpochOffsetSeconds
  val nanoseconds = (fraction * 1_000_000_000L) ushr 32
  return Instant.fromEpochSeconds(unixSeconds, nanoseconds)
}

private fun readUnsignedInt(bytes: ByteArray, offset: Int): Long =
  ((bytes[offset].toLong() and 0xff) shl 24) or
    ((bytes[offset + 1].toLong() and 0xff) shl 16) or
    ((bytes[offset + 2].toLong() and 0xff) shl 8) or
    (bytes[offset + 3].toLong() and 0xff)
