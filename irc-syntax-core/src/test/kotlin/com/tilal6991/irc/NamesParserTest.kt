package com.tilal6991.irc

import org.assertj.core.api.Assertions
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.reflect.Modifier

class NamesParserTest {

  private val callback = mock(NamesParser.Callback::class.java)

  @Test fun testInvalid() {
    verifyTooFew(emptyList())
    verifyTooFew(oneItemList())
    verifyTooMany(fourIemList())

    try {
      parse(listOf("#", "first +second +third"))
      fail("Expected 2812 with 2 args to throw IllegalArgumentException.")
    } catch (ex: IllegalArgumentException) {
      // Don't bother checking exact message.
    }
  }


  @Test fun test1459() {
    parse(listOf("channel", "first"))
    verify(callback).onNames(null, "channel", listOf("first"))

    parse(listOf("channel", "first +second +third"))
    verify(callback).onNames(null, "channel", listOf("first", "+second", "+third"))
  }

  @Test fun test2812() {
    parse(listOf("#", "channel", "first"))
    verify(callback).onNames('#', "channel", listOf("first"))

    parse(listOf("$", "channel", "first +second +third"))
    verify(callback).onNames('$', "channel", listOf("first", "+second", "+third"))
  }

  @Test fun testConstructorIsPrivate() {
    val constructor = NamesParser::class.java.getDeclaredConstructor()
    Assertions.assertThat(Modifier.isPrivate(constructor.modifiers)).isTrue()
    constructor.isAccessible = true
    constructor.newInstance()
  }

  private fun verifyTooMany(arguments: List<String>) {
    verifyFail(arguments, "Too many items")
  }

  private fun verifyTooFew(arguments: List<String>) {
    verifyFail(arguments, "Too few items")
  }

  private fun verifyFail(arguments: List<String>, message: String) {
    try {
      parse(arguments)
      fail(message)
    } catch (ex: IllegalArgumentException) {
      // Don't bother checking the message.
    }
  }

  private fun parse(arguments: List<String>) {
    NamesParser.parse(arguments, callback)
  }
}