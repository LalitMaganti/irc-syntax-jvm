package com.tilal6991.irc

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.reflect.Modifier

class ArgumentParserTest {
  private val callback = mock(ArgumentParser.Callback::class.java)

  @Test fun testInvalid() {
    parse("COMMAND", emptyList())
    verify(callback).onUnknownCommand("COMMAND", emptyList())

    parse("12m", emptyList())
    verify(callback).onUnknownCommand("COMMAND", emptyList())

    parse("12#", emptyList())
    verify(callback).onUnknownCommand("COMMAND", emptyList())
  }

  @Test fun testPing() {
    parse("PING", emptyList())
    verify(callback).onPing(null)

    parse("PING", listOf("a.server.com"))
    verify(callback).onPing("a.server.com")

    verifyTooMany("PING", twoItemList())
  }

  @Test fun testQuit() {
    parse("QUIT", emptyList())
    verify(callback).onQuit(null)

    parse("QUIT", listOf("reason"))
    verify(callback).onQuit("reason")

    verifyTooMany("QUIT", twoItemList())
  }

  @Test fun testNick() {
    verifyTooFew("NICK", emptyList<String>())

    parse("NICK", listOf("newnick"))
    verify(callback).onNick("newnick")

    verifyTooMany("NICK", twoItemList())
  }

  @Test fun testInvite() {
    verifyTooFew("INVITE", emptyList())
    verifyTooFew("INVITE", listOf("first"))

    parse("INVITE", listOf("target", "channel"))
    verify(callback).onInvite("target", "channel")

    verifyTooMany("INVITE", threeItemList())
  }

  @Test fun testAccount() {
    verifyTooFew("ACCOUNT", emptyList())

    parse("ACCOUNT", listOf("account"))
    verify(callback).onAccount("account")

    verifyTooMany("ACCOUNT", twoItemList())
  }

  @Test fun testAuthenticate() {
    verifyTooFew("AUTHENTICATE", emptyList())

    parse("AUTHENTICATE", listOf("data"))
    verify(callback).onAuthenticate("data")

    verifyTooMany("AUTHENTICATE", twoItemList())
  }

  @Test fun testPart() {
    verifyTooFew("PART", emptyList())

    parse("PART", listOf("channel"))
    verify(callback).onPart("channel", null)

    parse("PART", listOf("channel", "reason"))
    verify(callback).onPart("channel", "reason")

    verifyTooMany("NICK", threeItemList())
  }

  @Test fun testPrivmsg() {
    verifyTooFew("PRIVMSG", emptyList())
    verifyTooFew("PRIVMSG", listOf("target"))

    parse("PRIVMSG", listOf("target", "reason"))
    verify(callback).onPrivmsg("target", "reason")

    verifyTooMany("PRIVMSG", threeItemList())
  }

  @Test fun testNotice() {
    verifyTooFew("NOTICE", emptyList())
    verifyTooFew("NOTICE", listOf("target"))

    parse("NOTICE", listOf("target", "reason"))
    verify(callback).onNotice("target", "reason")

    verifyTooMany("NOTICE", threeItemList())
  }

  @Test fun testJoin() {
    verifyTooFew("JOIN", emptyList())

    parse("JOIN", listOf("channel"))
    verify(callback).onJoin("channel", emptyList())

    parse("JOIN", listOf("channel", "account", "realname"))
    verify(callback).onJoin("channel", listOf("account", "realname"))
  }

  @Test fun testCap() {
    verifyTooFew("CAP", emptyList())

    parse("CAP", listOf("subcommand", "first", "second"))
    verify(callback).onCap(listOf("subcommand", "first", "second"))
  }

  @Test fun testMode() {
    verifyTooFew("MODE", emptyList())
    verifyTooFew("MODE", listOf("target"))

    parse("MODE", listOf("target", "mode", "first", "second"))
    verify(callback).onMode("target", listOf("mode", "first", "second"))
  }

  @Test fun testKick() {
    verifyTooFew("KICK", emptyList())
    verifyTooFew("KICK", listOf("first"))

    parse("KICK", listOf("channel", "target"))
    verify(callback).onKick("channel", "target", null)

    parse("KICK", listOf("channel", "target", "reason"))
    verify(callback).onKick("channel", "target", "reason")

    verifyTooMany("KICK", fourIemList())
  }

  @Test fun testChghost() {
    verifyTooFew("CHGHOST", emptyList())
    verifyTooFew("CHGHOST", listOf("first"))

    parse("CHGHOST", listOf("newuser", "newhost"))
    verify(callback).onChghost("newuser", "newhost")

    verifyTooMany("CHGHOST", threeItemList())
  }

  @Test fun testAway() {
    parse("AWAY", emptyList())
    verify(callback).onAway(null)

    parse("AWAY", listOf("message"))
    verify(callback).onAway("message")

    verifyTooMany("AWAY", listOf("first", "fourth", "third", "fourth"))
  }

  @Test fun testBatch() {
    verifyTooFew("BATCH", emptyList())
    verifyTooFew("BATCH", listOf("first"))

    parse("BATCH", listOf("+subcommand", "type", "first", "second"))
    verify(callback).onBatch("+subcommand", "type", twoItemList())
  }

  @Test fun testReply() {
    verifyTooFew("001", emptyList())

    parse("001", listOf("target"))
    verify(callback).onReply(1, "target", emptyList())

    parse("001", listOf("target", "arg", "arg"))
    verify(callback).onReply(1, "target", listOf("arg", "arg"))
  }

  @Test fun testConstructorIsPrivate() {
    val constructor = ArgumentParser::class.java.getDeclaredConstructor()
    Assertions.assertThat(Modifier.isPrivate(constructor.modifiers)).isTrue()
    constructor.isAccessible = true
    constructor.newInstance()
  }

  private fun verifyTooMany(command: String, arguments: List<String>) {
    verifyFail(command, arguments, "Too many items")
  }

  private fun verifyTooFew(command: String, arguments: List<String>) {
    verifyFail(command, arguments, "Too few items")
  }

  private fun verifyFail(command: String, arguments: List<String>, message: String) {
    try {
      parse(command, arguments)
      fail(message)
    } catch (ex: IllegalArgumentException) {
      // Don't bother checking the message.
    }
  }

  private fun parse(command: String, arguments: List<String>) {
    ArgumentParser.parse(command, arguments, callback)
  }
}