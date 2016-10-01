package com.tilal6991.irc.syntax

import org.assertj.core.api.Assertions
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.reflect.Modifier

class CapParserTest {
  private val callback = mock(CapParser.Callback::class.java)

  @Test fun testCapBadOrUnknown() {
    expectIae { CapParser.parse(emptyList(), callback) }
    expectIae { CapParser.parse(oneItemList(), callback) }
    expectIae { CapParser.parse(fiveIemList(), callback) }

    CapParser.parse(listOf("*", "OTHER"), callback)
    verify(callback).onUnknownCap("*", "OTHER", emptyList())

    CapParser.parse(listOf("*", "OTHER") + oneItemList(), callback)
    verify(callback).onUnknownCap("*", "OTHER", oneItemList())
  }

  @Test fun testCapLs() {
    CapParser.parse(listOf("*", "LS"), callback)
    verify(callback).onCapLs("*", true, null)

    CapParser.parse(listOf("*", "LS", "first second third"), callback)
    verify(callback).onCapLs("*", true, listOf("first", "second", "third"))

    CapParser.parse(listOf("*", "LS", "*", "first"), callback)
    verify(callback).onCapLs("*", false, listOf("first"))

    CapParser.parse(listOf("*", "LS", "*", "first second third"), callback)
    verify(callback).onCapLs("*", false, listOf("first", "second", "third"))

    expectIae { CapParser.parse(listOf("*", "LS", "*"), callback) }
    expectIae { CapParser.parse(listOf("*", "LS", "f", "second"), callback) }
    expectIae { CapParser.parse(listOf("*", "LS", "first", "second"), callback) }
  }

  @Test fun testCapList() {
    CapParser.parse(listOf("*", "LIST"), callback)
    verify(callback).onCapList("*", true, null)

    CapParser.parse(listOf("*", "LIST", "first second third"), callback)
    verify(callback).onCapList("*", true, listOf("first", "second", "third"))

    CapParser.parse(listOf("*", "LIST", "*", "first"), callback)
    verify(callback).onCapList("*", false, listOf("first"))

    CapParser.parse(listOf("*", "LIST", "*", "first second third"), callback)
    verify(callback).onCapList("*", false, listOf("first", "second", "third"))

    expectIae { CapParser.parse(listOf("*", "LIST", "*"), callback) }
    expectIae { CapParser.parse(listOf("*", "LIST", "f", "second"), callback) }
    expectIae { CapParser.parse(listOf("*", "LIST", "first", "second"), callback) }
  }

  @Test fun testCapAck() {
    CapParser.parse(listOf("*", "ACK"), callback)
    verify(callback).onCapAck("*", null)

    CapParser.parse(listOf("*", "ACK", "first second third"), callback)
    verify(callback).onCapAck("*", listOf("first", "second", "third"))

    expectIae { CapParser.parse(listOf("first", "ACK", "second", "third"), callback) }
  }

  @Test fun testCapNak() {
    CapParser.parse(listOf("*", "NAK"), callback)
    verify(callback).onCapNak("*", null)

    CapParser.parse(listOf("*", "NAK", "first second third"), callback)
    verify(callback).onCapNak("*", listOf("first", "second", "third"))

    expectIae { CapParser.parse(listOf("first", "NAK", "second", "third"), callback) }
  }

  @Test fun testCapNew() {
    CapParser.parse(listOf("*", "NEW"), callback)
    verify(callback).onCapNew("*", null)

    CapParser.parse(listOf("*", "NEW", "first second third"), callback)
    verify(callback).onCapNew("*", listOf("first", "second", "third"))

    expectIae { CapParser.parse(listOf("first", "NEW", "second", "third"), callback) }
  }

  @Test fun testCapDel() {
    CapParser.parse(listOf("*", "DEL"), callback)
    verify(callback).onCapDel("*", null)

    CapParser.parse(listOf("*", "DEL", "first second third"), callback)
    verify(callback).onCapDel("*", listOf("first", "second", "third"))

    expectIae { CapParser.parse(listOf("first", "DEL", "second", "third"), callback) }
  }

  @Test fun testConstructorIsPrivate() {
    val constructor = CapParser::class.java.getDeclaredConstructor()
    Assertions.assertThat(Modifier.isPrivate(constructor.modifiers)).isTrue()
    constructor.isAccessible = true
    constructor.newInstance()
  }

  inline fun expectIae(code: () -> Unit) {
    try {
      code()
      fail("Expected IAE.")
    } catch(ex: IllegalArgumentException) {
      // Don't bother checking the message
    }
  }
}