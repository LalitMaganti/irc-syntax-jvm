package com.tilal6991.irc.syntax;

import java.lang.Character;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AbstractMessageCallback<T> implements MessageCallback<T> {
  @Override
  public T onCapAck(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
    return null;
  }

  @Override
  public T onCapDel(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
    return null;
  }

  @Override
  public T onCapList(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues) {
    return null;
  }

  @Override
  public T onCapLs(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues) {
    return null;
  }

  @Override
  public T onCapNak(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
    return null;
  }

  @Override
  public T onCapNew(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
    return null;
  }

  @Override
  public T onEndOfMotd(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return null;
  }

  @Override
  public T onEndOfNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel, @Nonnull String message) {
    return null;
  }

  @Override
  public T onInvite(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel) {
    return null;
  }

  @Override
  public T onIsupport(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message, @Nonnull List<String> tokens) {
    return null;
  }

  @Override
  public T onJoin(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull List<String> arguments) {
    return null;
  }

  @Override
  public T onKick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull String user, @Nullable String reason) {
    return null;
  }

  @Override
  public T onMode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull List<String> arguments) {
    return null;
  }

  @Override
  public T onMotd(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return null;
  }

  @Override
  public T onMotdStart(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return null;
  }

  @Override
  public T onNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names) {
    return null;
  }

  @Override
  public T onNick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String nick) {
    return null;
  }

  @Override
  public T onNotice(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return null;
  }

  @Override
  public T onPart(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nullable String reason) {
    return null;
  }

  @Override
  public T onPing(@Nullable List<String> tags, @Nullable String prefix, @Nullable String hostname) {
    return null;
  }

  @Override
  public T onPrivmsg(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return null;
  }

  @Override
  public T onQuit(@Nullable List<String> tags, @Nullable String prefix, @Nullable String reason) {
    return null;
  }

  @Override
  public T onUnknownCap(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nonnull String subcommand, @Nonnull List<String> arguments) {
    return null;
  }

  @Override
  public T onUnknownCode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, int code, @Nonnull List<String> arguments) {
    return null;
  }

  @Override
  public T onUnknownCommand(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String command, @Nonnull List<String> arguments) {
    return null;
  }

  @Override
  public T onWelcome(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    return null;
  }
}
