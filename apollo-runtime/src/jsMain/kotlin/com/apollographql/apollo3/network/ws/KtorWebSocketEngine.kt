package com.apollographql.apollo3.network.ws

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.headers
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.ByteString

actual class DefaultWebSocketEngine(private val ktorClient: HttpClient) : WebSocketEngine {
  actual constructor() : this(ktorClient = HttpClient(Js) { install(WebSockets) })

  override suspend fun open(
      url: String,
      headers: Map<String, String>,
  ): WebSocketConnection = open(Url(url), headers)

  private suspend fun open(url: Url, headers: Map<String, String>): WebSocketConnection {
    val newUrl = url.copy(
        protocol = when (url.protocol) {
          URLProtocol.HTTPS -> URLProtocol.WSS
          URLProtocol.HTTP -> URLProtocol.WS
          URLProtocol.WS, URLProtocol.WSS -> url.protocol
          /* URLProtocol.SOCKS */else -> throw UnsupportedOperationException("SOCKS is not a supported protocol")
        })
    println("url: $newUrl")
    val socketSession = try {
      ktorClient.webSocketSession {
        url(newUrl)
        method = HttpMethod.Get
        headers {
//          headers.forEach {
//            println("key: ${it.key}, value:${it.value}")
//            append(it.key, it.value)
//          }
        }
      }
    } catch (e: Throwable) {
      println("stack: ${e.stackTraceToString()}")
      throw e
    }

    return object : WebSocketConnection {
      override suspend fun receive(): String {
        val received = socketSession.incoming.receive()
        println("frame type: ${received.frameType}")
        return received.data.decodeToString().also {
          println(it)
        }
      }

      override fun send(data: ByteString) {
        println("send ${data.utf8()}")
        socketSession.outgoing.trySend(Frame.Binary(true, data.toByteArray()))
      }

      override fun send(string: String) {
        println("send $string")
        socketSession.outgoing.trySend(Frame.Text(string))
      }

      override fun close() {
        CoroutineScope(Dispatchers.Unconfined).launch {
          socketSession.close(CloseReason(CloseReason.Codes.NORMAL, ""))
        }
      }
    }
  }
}

