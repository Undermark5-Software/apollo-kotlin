package test

import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.api.http.HttpMethod
import com.apollographql.apollo3.api.http.HttpPart
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.valueOf
import com.apollographql.apollo3.mockserver.MockServer
import com.apollographql.apollo3.mockserver.enqueueMultipart
import com.apollographql.apollo3.network.http.DefaultHttpEngine
import com.apollographql.apollo3.testing.runTest
import kotlinx.coroutines.flow.collect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ApolloExperimental::class)
class HttpEngineTest {
  private lateinit var mockServer: MockServer

  private fun setUp() {
    mockServer = MockServer()
  }

  private suspend fun tearDown() {
    mockServer.stop()
  }

  @Test
  fun receiveAndDecodeParts() = runTest(before = { setUp() }, after = { tearDown() }) {
    val part0 = """{"data":{"song":{"firstVerse":"Now I know my ABC's."}},"hasNext":true}"""
    val part1 = """{"data":{"secondVerse":"Next time won't you sing with me?"},"path":["song"],"hasNext":false}"""
    val enqueuedParts = listOf(part0, part1)
    mockServer.enqueueMultipart(enqueuedParts)

    val httpResponse = DefaultHttpEngine().execute(HttpRequest.Builder(HttpMethod.Get, mockServer.url()).build())
    assertNull(httpResponse.body)

    val receivedParts = mutableListOf<HttpPart>()
    val receivedPartBodies = mutableListOf<String>()
    httpResponse.parts!!.collect { part ->
      receivedParts.add(part)
      receivedPartBodies.add(part.body.readUtf8())
    }

    assertEquals(enqueuedParts.size, receivedParts.size)
    for (i in enqueuedParts.indices) {
      assertEquals(enqueuedParts[i].length.toString(), receivedParts[i].headers.valueOf("Content-Length"))
      assertEquals("application/json; charset=utf-8", receivedParts[i].headers.valueOf("Content-Type"))
      assertEquals(enqueuedParts[i], receivedPartBodies[i])
    }
  }
}
