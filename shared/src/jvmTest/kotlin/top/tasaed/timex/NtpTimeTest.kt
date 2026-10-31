package top.tasaed.timex

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

class NtpTimeTest {
  @Test
  fun queryNtpTimeSendsUdpRequestAndParsesServerTimestamp() = runBlocking {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val server = aSocket(selectorManager).udp().bind("127.0.0.1", 0)
    try {
      val port = (server.localAddress as InetSocketAddress).port
      val responder = async {
        val request = server.receive()
        val requestBytes = request.packet.readByteArray()
        val response = Buffer()
        val responseBytes = ByteArray(48)
        responseBytes[0] = 0x24.toByte()
        responseBytes[1] = 1
        val ntpSeconds = 1_700_000_000L + 2_208_988_800L
        repeat(4) { index ->
          responseBytes[40 + index] = (ntpSeconds shr (24 - index * 8)).toByte()
        }
        responseBytes[44] = 0x80.toByte()
        assertEquals(500_000_000, parseNtpResponse(responseBytes).nanosecondsOfSecond)
        responseBytes.forEach(response::writeByte)
        server.send(Datagram(response, request.address))
        assertEquals(48, requestBytes.size)
        assertEquals(0x23, requestBytes[0].toInt() and 0xff)
      }
      val result = queryNtpTime("127.0.0.1", port)
      responder.await()

      assertTrue(result.epochSeconds in 1_700_000_000L..1_700_000_004L)
    } finally {
      server.close()
      selectorManager.close()
    }
  }

  @Test
  fun queryNtpTimeDifferenceUsesServerTimeMinusLocalTime() = runBlocking {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val server = aSocket(selectorManager).udp().bind("127.0.0.1", 0)
    try {
      val port = (server.localAddress as InetSocketAddress).port
      val responder = async {
        val request = server.receive()
        val response = Buffer()
        val responseBytes = ByteArray(48)
        responseBytes[0] = 0x24.toByte()
        responseBytes[1] = 1
        val ntpSeconds = kotlin.time.Clock.System.now().epochSeconds + 2_208_988_800L + 2L
        repeat(4) { index ->
          responseBytes[40 + index] = (ntpSeconds shr (24 - index * 8)).toByte()
        }
        responseBytes.forEach(response::writeByte)
        server.send(Datagram(response, request.address))
      }

      val difference = queryNtpTimeDifference("127.0.0.1", port)
      responder.await()

      assertTrue(difference.inWholeSeconds in 1L..3L)
    } finally {
      server.close()
      selectorManager.close()
    }
  }

  @Test
  fun rejectsUnsynchronizedNtpResponse() {
    val response = ByteArray(48)
    response[0] = 0xe4.toByte()
    response[1] = 1

    assertFailsWith<IllegalArgumentException> { parseNtpResponse(response) }
  }
}
