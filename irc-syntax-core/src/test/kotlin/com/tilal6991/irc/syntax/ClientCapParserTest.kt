package com.tilal6991.irc.syntax

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ClientCapParserTest {
  private val callback = mock(ClientCapParser.Callback::class.java)

  @Test fun testCapLs() {
    ClientCapParser.parse(listOf("*", "LS", "first second third"), callback)
    verify(callback).onCapLs("*", true, listOf("first", "second", "third"))

    ClientCapParser.parse(listOf("*", "LS", "*", "first second third"), callback)
    verify(callback).onCapLs("*", false, listOf("first", "second", "third"))
  }
}