package com.tilal6991.irc

import org.assertj.core.api.Assertions.fail
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class CodeParserTest {
  private val callback = mock(CodeParser.Callback::class.java)

  @Test fun testWelcome() {
    verifyTooFew(1, emptyList())

    parse(1, listOf("message"))
    verify(callback).onWelcome("message")

    verifyTooMany(1, twoItemList())
  }

  @Test fun testIsupport() {
    verifyTooFew(5, emptyList())
    verifyTooFew(5, oneItemList())

    parse(5, listOf("token", "message"))
    verify(callback).onIsupport("message", listOf("token"))

    parse(5, fourIemList() + listOf("message"))
    verify(callback).onIsupport("message", fourIemList())
  }

  private fun verifyTooMany(code: Int, arguments: List<String>) {
    verifyFail(code, arguments, "Too many items")
  }

  private fun verifyTooFew(code: Int, arguments: List<String>) {
    verifyFail(code, arguments, "Too few items")
  }

  private fun verifyFail(code: Int, arguments: List<String>, message: String) {
    try {
      parse(code, arguments)
      fail(message)
    } catch (ex: IllegalArgumentException) {
      // Don't bother checking the message.
    }
  }

  private fun parse(code: Int, arguments: List<String>) {
    CodeParser.parse(code, arguments, callback)
  }
}