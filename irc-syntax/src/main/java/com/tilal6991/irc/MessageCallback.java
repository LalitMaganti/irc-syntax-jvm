package com.tilal6991.irc;

import java.lang.Character;
import java.lang.String;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MessageCallback {
  /**
   * Callback method for ACCOUNT messages.
   */
  void onAccount(@Nullable List<String> tags, @Nullable String prefix, @Nullable String account);

  /**
   * Callback method for AUTHENTICATE messages.
   */
  void onAuthenticate(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String data);

  /**
   * Callback method for AWAY messages.
   */
  void onAway(@Nullable List<String> tags, @Nullable String prefix, @Nullable String message);

  /**
   * Callback method for BATCH messages.
   */
  void onBatch(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String modifiedReferenceTag, @Nonnull String type, @Nonnull List<String> arguments);

  /**
   * Callback method for CAP messages.
   */
  void onCap(@Nullable List<String> tags, @Nullable String prefix, @Nonnull List<String> args);

  /**
   * Callback method for CHGHOST messages.
   */
  void onChghost(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String newUser, @Nonnull String newHost);

  /**
   * Callback method for RPL_ENDOFNAMES replies.
   */
  void onEndOfNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel, @Nonnull String message);

  /**
   * Callback method for INVITE messages.
   */
  void onInvite(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel);

  /**
   * Callback method for RPL_ISUPPORT replies.
   */
  void onIsupport(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message, @Nonnull List<String> tokens);

  /**
   * Callback method for JOIN messages.
   */
  void onJoin(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull List<String> arguments);

  /**
   * Callback method for KICK messages.
   */
  void onKick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull String user, @Nullable String reason);

  /**
   * Callback method for MODE messages.
   */
  void onMode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull List<String> arguments);

  /**
   * Callback method for RPL_NAMREPLY replies.
   */
  void onNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names);

  /**
   * Callback method for NICK messages.
   */
  void onNick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String nick);

  /**
   * Callback method for NOTICE messages.
   */
  void onNotice(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);

  /**
   * Callback method for PART messages.
   */
  void onPart(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nullable String reason);

  /**
   * Callback method for PING messages.
   */
  void onPing(@Nullable List<String> tags, @Nullable String prefix, @Nullable String hostname);

  /**
   * Callback method for PRIVMSG messages.
   */
  void onPrivmsg(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);

  /**
   * Callback method for QUIT messages.
   */
  void onQuit(@Nullable List<String> tags, @Nullable String prefix, @Nullable String reason);

  /**
   * Callback method for RPL_UNKNOWNCODE replies.
   */
  void onUnknownCode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, int code, @Nonnull List<String> arguments);

  /**
   * Callback method for UNKNOWNCOMMAND messages.
   */
  void onUnknownCommand(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String command, @Nonnull List<String> arguments);

  /**
   * Callback method for RPL_WELCOME replies.
   */
  void onWelcome(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message);
}
