package com.tilal6991.irc

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.reflect.Modifier

class MessageTokenizerTest {

  private val callback = mock(MessageTokenizer.Callback::class.java)

  @Test fun testEmptyLine() {
    try {
      tokenize("")
      fail("Expected empty line to throw IllegalArgumentException.")
    } catch (ex: IllegalArgumentException) {
      // Don't bother checking exact message.
    }
  }

  @Test fun testOnlyTags() {
    try {
      tokenize("@a=b;connection=d")
      fail("Expected only tags to throw IllegalArgumentException.")
    } catch (ex: IllegalArgumentException) {
      // Don't bother checking exact message.
    }
  }

  @Test fun testOnlyPrefix() {
    try {
      tokenize(":test")
      fail("Expected only prefix to throw IllegalArgumentException.")
    } catch (ex: IllegalArgumentException) {
      // Don't bother checking exact message.
    }
  }

  @Test fun testTaggedCommand() {
    tokenize("@a=b;connection=d COMMAND arg")
    verify(callback).onLineTokenized(listOf("a=b", "connection=d"), null, "COMMAND", listOf("arg"))
  }

  @Test fun testPrefixedCommand() {
    tokenize(":test COMMAND arg")
    verify(callback).onLineTokenized(null, "test", "COMMAND", listOf("arg"))
  }

  @Test fun testPrefixedCode() {
    tokenize(":test 042 #test arg")
    verify(callback).onLineTokenized(null, "test", "042", listOf("#test", "arg"))
  }

  @Test fun testFullCommand() {
    tokenize("@a=b;connection=d :test COMMAND arg")
    verify(callback).onLineTokenized(
        listOf("a=b", "connection=d"), "test", "COMMAND", listOf("arg"))
  }

  @Test fun testFullCode() {
    tokenize("@a=b;connection=d :test 042 #test arg")
    verify(callback).onLineTokenized(
        listOf("a=b", "connection=d"), "test", "042", listOf("#test", "arg"))
  }

  @Test fun testNoArgumentCommand() {
    tokenize("COMMAND")
    verify(callback).onLineTokenized(null, null, "COMMAND", emptyList())
  }

  @Test fun testNoArgumentCode() {
    tokenize(":test 042 #test")
    verify(callback).onLineTokenized(null, "test", "042", listOf("#test"))
  }

  @Test fun testNoPrefixCode() {
    tokenize("042 #test")
    verify(callback).onLineTokenized(null, null, "042", listOf("#test"))
  }

  @Test fun testNoTargetCode() {
    tokenize(":test 042")
    verify(callback).onLineTokenized(null, "test", "042", emptyList())
  }

  @Test fun testArgumentWithColon() {
    tokenize("@a=b;connection=d :test COMMAND :arg arg2")
    verify(callback).onLineTokenized(
        listOf("a=b", "connection=d"), "test", "COMMAND", listOf("arg arg2"))
  }

  @Test fun testMultipleArgumentsWithColon() {
    tokenize("COMMAND test test2 :arg arg2")
    verify(callback).onLineTokenized(null, null, "COMMAND", listOf("test", "test2", "arg arg2"))
  }

  @Test fun testConstructorIsPrivate() {
    val constructor = MessageTokenizer::class.java.getDeclaredConstructor()
    assertThat(Modifier.isPrivate(constructor.modifiers)).isTrue()
    constructor.isAccessible = true
    constructor.newInstance()
  }

  private fun tokenize(line: String) {
    MessageTokenizer.tokenize(line, callback)
  }
}