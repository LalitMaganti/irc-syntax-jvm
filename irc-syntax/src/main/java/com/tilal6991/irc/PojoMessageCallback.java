package com.tilal6991.irc;

import java.lang.Character;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PojoMessageCallback implements MessageCallback<Message> {
  @Override
  public Message onAccount(@Nullable List<String> tags, @Nullable String prefix, @Nullable String account) {
    return new Message.Account(tags, prefix, account);
  }

  @Override
  public Message onAuthenticate(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String data) {
    return new Message.Authenticate(tags, prefix, data);
  }

  @Override
  public Message onAway(@Nullable List<String> tags, @Nullable String prefix, @Nullable String message) {
    return new Message.Away(tags, prefix, message);
  }

  @Override
  public Message onBatch(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String modifiedReferenceTag, @Nonnull String type, @Nonnull List<String> arguments) {
    return new Message.Batch(tags, prefix, modifiedReferenceTag, type, arguments);
  }

  @Override
  public Message onCap(@Nullable List<String> tags, @Nullable String prefix, @Nonnull List<String> args) {
    return new Message.Cap(tags, prefix, args);
  }

  @Override
  public Message onChghost(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String newUser, @Nonnull String newHost) {
    return new Message.Chghost(tags, prefix, newUser, newHost);
  }

  @Override
  public Message onEndOfNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel, @Nonnull String message) {
    return new Message.EndOfNames(tags, prefix, target, channel, message);
  }

  @Override
  public Message onInvite(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel) {
    return new Message.Invite(tags, prefix, target, channel);
  }

  @Override
  public Message onIsupport(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message, @Nonnull List<String> tokens) {
    return new Message.Isupport(tags, prefix, target, message, tokens);
  }

  @Override
  public Message onJoin(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull List<String> arguments) {
    return new Message.Join(tags, prefix, channel, arguments);
  }

  @Override
  public Message onKick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull String user, @Nullable String reason) {
    return new Message.Kick(tags, prefix, channel, user, reason);
  }

  @Override
  public Message onMode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull List<String> arguments) {
    return new Message.Mode(tags, prefix, target, arguments);
  }

  @Override
  public Message onNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names) {
    return new Message.Names(tags, prefix, target, channelDescriptor, channel, names);
  }

  @Override
  public Message onNick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String nick) {
    return new Message.Nick(tags, prefix, nick);
  }

  @Override
  public Message onNotice(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return new Message.Notice(tags, prefix, target, message);
  }

  @Override
  public Message onPart(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nullable String reason) {
    return new Message.Part(tags, prefix, channel, reason);
  }

  @Override
  public Message onPing(@Nullable List<String> tags, @Nullable String prefix, @Nullable String hostname) {
    return new Message.Ping(tags, prefix, hostname);
  }

  @Override
  public Message onPrivmsg(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return new Message.Privmsg(tags, prefix, target, message);
  }

  @Override
  public Message onQuit(@Nullable List<String> tags, @Nullable String prefix, @Nullable String reason) {
    return new Message.Quit(tags, prefix, reason);
  }

  @Override
  public Message onUnknownCode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, int code, @Nonnull List<String> arguments) {
    return new Message.UnknownCode(tags, prefix, target, code, arguments);
  }

  @Override
  public Message onUnknownCommand(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String command, @Nonnull List<String> arguments) {
    return new Message.UnknownCommand(tags, prefix, command, arguments);
  }

  @Override
  public Message onWelcome(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return new Message.Welcome(tags, prefix, target, message);
  }
}
