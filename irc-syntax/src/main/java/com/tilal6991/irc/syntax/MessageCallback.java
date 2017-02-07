package com.tilal6991.irc.syntax;

import java.lang.Character;
import java.lang.String;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MessageCallback<T> {
  /**
   * Callback method for CAP ACK.
   */
  T onCapAck(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues);

  /**
   * Callback method for CAP DEL.
   */
  T onCapDel(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues);

  /**
   * Callback method for CAP LIST.
   */
  T onCapList(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues);

  /**
   * Callback method for CAP LS.
   */
  T onCapLs(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues);

  /**
   * Callback method for CAP NAK.
   */
  T onCapNak(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues);

  /**
   * Callback method for CAP NEW.
   */
  T onCapNew(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues);

  /**
   * Callback method for RPL_ENDOFMOTD replies.
   */
  T onEndOfMotd(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);

  /**
   * Callback method for RPL_ENDOFNAMES replies.
   */
  T onEndOfNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel, @Nonnull String message);

  /**
   * Callback method for INVITE messages.
   */
  T onInvite(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel);

  /**
   * Callback method for RPL_ISUPPORT replies.
   */
  T onIsupport(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message, @Nonnull List<String> tokens);

  /**
   * Callback method for JOIN messages.
   */
  T onJoin(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull List<String> arguments);

  /**
   * Callback method for KICK messages.
   */
  T onKick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull String user, @Nullable String reason);

  /**
   * Callback method for MODE messages.
   */
  T onMode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull List<String> arguments);

  /**
   * Callback method for RPL_MOTD replies.
   */
  T onMotd(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);

  /**
   * Callback method for RPL_MOTDSTART replies.
   */
  T onMotdStart(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);

  /**
   * Callback method for RPL_NAMREPLY replies.
   */
  T onNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names);

  /**
   * Callback method for NICK messages.
   */
  T onNick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String nick);

  /**
   * Callback method for NOTICE messages.
   */
  T onNotice(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);

  /**
   * Callback method for PART messages.
   */
  T onPart(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nullable String reason);

  /**
   * Callback method for PING messages.
   */
  T onPing(@Nullable List<String> tags, @Nullable String prefix, @Nullable String hostname);

  /**
   * Callback method for PRIVMSG messages.
   */
  T onPrivmsg(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);

  /**
   * Callback method for QUIT messages.
   */
  T onQuit(@Nullable List<String> tags, @Nullable String prefix, @Nullable String reason);

  /**
   * Callback method for CAP ONUNKNOWNCAP.
   */
  T onUnknownCap(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nonnull String subcommand, @Nonnull List<String> arguments);

  /**
   * Callback method for RPL_UNKNOWNCODE replies.
   */
  T onUnknownCode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, int code, @Nonnull List<String> arguments);

  /**
   * Callback method for UNKNOWNCOMMAND messages.
   */
  T onUnknownCommand(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String command, @Nonnull List<String> arguments);

  /**
   * Callback method for RPL_WELCOME replies.
   */
  T onWelcome(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);
}
