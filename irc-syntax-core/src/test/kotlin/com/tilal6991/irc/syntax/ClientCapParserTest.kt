package com.tilal6991.irc.syntax

import org.assertj.core.api.Assertions
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.reflect.Modifier

class ClientCapParserTest {
  private val callback = mock(ClientCapParser.Callback::class.java)

  @Test fun testCapBadOrUnknown() {
    expectIae { ClientCapParser.parse(emptyList(), callback) }
    expectIae { ClientCapParser.parse(oneItemList(), callback) }
    expectIae { ClientCapParser.parse(fiveIemList(), callback) }

    ClientCapParser.parse(listOf("*", "OTHER"), callback)
    verify(callback).onUnknownCap("*", "OTHER", emptyList())

    ClientCapParser.parse(listOf("*", "OTHER") + oneItemList(), callback)
    verify(callback).onUnknownCap("*", "OTHER", oneItemList())
  }

  @Test fun testCapLs() {
    ClientCapParser.parse(listOf("*", "LS"), callback)
    verify(callback).onCapLs("*", true, null)

    ClientCapParser.parse(listOf("*", "LS", "first second third"), callback)
    verify(callback).onCapLs("*", true, listOf("first", "second", "third"))

    ClientCapParser.parse(listOf("*", "LS", "*", "first"), callback)
    verify(callback).onCapLs("*", false, listOf("first"))

    ClientCapParser.parse(listOf("*", "LS", "*", "first second third"), callback)
    verify(callback).onCapLs("*", false, listOf("first", "second", "third"))

    expectIae { ClientCapParser.parse(listOf("*", "LS", "*"), callback) }
    expectIae { ClientCapParser.parse(listOf("*", "LS", "d"), callback) }
    expectIae { ClientCapParser.parse(listOf("*", "LS", "first", "second"), callback) }
  }

  @Test fun testCapList() {
    ClientCapParser.parse(listOf("*", "LIST"), callback)
    verify(callback).onCapList("*", true, null)

    ClientCapParser.parse(listOf("*", "LIST", "first second third"), callback)
    verify(callback).onCapList("*", true, listOf("first", "second", "third"))

    ClientCapParser.parse(listOf("*", "LIST", "*", "first"), callback)
    verify(callback).onCapList("*", false, listOf("first"))

    ClientCapParser.parse(listOf("*", "LIST", "*", "first second third"), callback)
    verify(callback).onCapList("*", false, listOf("first", "second", "third"))

    expectIae { ClientCapParser.parse(listOf("*", "LIST", "*"), callback) }
    expectIae { ClientCapParser.parse(listOf("*", "LIST", "d"), callback) }
    expectIae { ClientCapParser.parse(listOf("*", "LIST", "first", "second"), callback) }
  }

  @Test fun testCapAck() {
    ClientCapParser.parse(listOf("*", "ACK"), callback)
    verify(callback).onCapAck("*", null)

    ClientCapParser.parse(listOf("*", "ACK", "first second third"), callback)
    verify(callback).onCapAck("*", listOf("first", "second", "third"))

    expectIae { ClientCapParser.parse(listOf("first", "ACK", "second", "third"), callback) }
  }

  @Test fun testCapNak() {
    ClientCapParser.parse(listOf("*", "NAK"), callback)
    verify(callback).onCapNak("*", null)

    ClientCapParser.parse(listOf("*", "NAK", "first second third"), callback)
    verify(callback).onCapNak("*", listOf("first", "second", "third"))

    expectIae { ClientCapParser.parse(listOf("first", "NAK", "second", "third"), callback) }
  }

  @Test fun testCapNew() {
    ClientCapParser.parse(listOf("*", "NEW"), callback)
    verify(callback).onCapNew("*", null)

    ClientCapParser.parse(listOf("*", "NEW", "first second third"), callback)
    verify(callback).onCapNew("*", listOf("first", "second", "third"))

    expectIae { ClientCapParser.parse(listOf("first", "NEW", "second", "third"), callback) }
  }

  @Test fun testCapDel() {
    ClientCapParser.parse(listOf("*", "DEL"), callback)
    verify(callback).onCapDel("*", null)

    ClientCapParser.parse(listOf("*", "DEL", "first second third"), callback)
    verify(callback).onCapDel("*", listOf("first", "second", "third"))

    expectIae { ClientCapParser.parse(listOf("first", "DEL", "second", "third"), callback) }
  }

  @Test fun testConstructorIsPrivate() {
    val constructor = ClientCapParser::class.java.getDeclaredConstructor()
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