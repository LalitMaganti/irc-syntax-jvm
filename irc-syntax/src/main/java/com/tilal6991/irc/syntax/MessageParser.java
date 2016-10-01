package com.tilal6991.irc.syntax;

import java.lang.Character;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MessageParser<T> {
  private final MessageCallback<T> callback;

  private final Inner inner;

  public MessageParser(@Nonnull MessageCallback<T> callback) {
    this.callback = callback;
    this.inner = new Inner();
  }

  public T parse(@Nonnull String line) {
    return MessageTokenizer.tokenize(line, inner);
  }

  private class Inner implements MessageTokenizer.Callback<T>, ArgumentParser.Callback<T>, CapParser.Callback<T>, CodeParser.Callback<T>, NamesParser.Callback<T> {
    private List<String> tags;

    private String prefix;

    private String target;

    @Override
    public T onCap(@Nonnull List<String> arguments) {
      return CapParser.parse(arguments, this);
    }

    @Override
    public T onCapAck(@Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
      return callback.onCapAck(tags, prefix, clientId, modCapabilityAndValues);
    }

    @Override
    public T onCapDel(@Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
      return callback.onCapDel(tags, prefix, clientId, modCapabilityAndValues);
    }

    @Override
    public T onCapList(@Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues) {
      return callback.onCapList(tags, prefix, clientId, finalLine, modCapabilityAndValues);
    }

    @Override
    public T onCapLs(@Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues) {
      return callback.onCapLs(tags, prefix, clientId, finalLine, modCapabilityAndValues);
    }

    @Override
    public T onCapNak(@Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
      return callback.onCapNak(tags, prefix, clientId, modCapabilityAndValues);
    }

    @Override
    public T onCapNew(@Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
      return callback.onCapNew(tags, prefix, clientId, modCapabilityAndValues);
    }

    @Override
    public T onEndOfNames(@Nonnull String channel, @Nonnull String message) {
      return callback.onEndOfNames(tags, prefix, target, channel, message);
    }

    @Override
    public T onInvite(@Nonnull String target, @Nonnull String channel) {
      return callback.onInvite(tags, prefix, target, channel);
    }

    @Override
    public T onIsupport(@Nonnull String message, @Nonnull List<String> tokens) {
      return callback.onIsupport(tags, prefix, target, message, tokens);
    }

    @Override
    public T onJoin(@Nonnull String channel, @Nonnull List<String> arguments) {
      return callback.onJoin(tags, prefix, channel, arguments);
    }

    @Override
    public T onKick(@Nonnull String channel, @Nonnull String user, @Nullable String reason) {
      return callback.onKick(tags, prefix, channel, user, reason);
    }

    @Override
    public T onLineTokenized(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String command, @Nonnull List<String> arguments) {
      this.tags = tags;
      this.prefix = prefix;
      T temp = ArgumentParser.parse(command, arguments, this);
      this.tags = null;
      this.prefix = null;
      return temp;
    }

    @Override
    public T onMode(@Nonnull String target, @Nonnull List<String> arguments) {
      return callback.onMode(tags, prefix, target, arguments);
    }

    @Override
    public T onNamReply(@Nonnull List<String> arguments) {
      return NamesParser.parse(arguments, this);
    }

    @Override
    public T onNames(@Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names) {
      return callback.onNames(tags, prefix, target, channelDescriptor, channel, names);
    }

    @Override
    public T onNick(@Nonnull String nick) {
      return callback.onNick(tags, prefix, nick);
    }

    @Override
    public T onNotice(@Nonnull String target, @Nonnull String message) {
      return callback.onNotice(tags, prefix, target, message);
    }

    @Override
    public T onPart(@Nonnull String channel, @Nullable String reason) {
      return callback.onPart(tags, prefix, channel, reason);
    }

    @Override
    public T onPing(@Nullable String hostname) {
      return callback.onPing(tags, prefix, hostname);
    }

    @Override
    public T onPrivmsg(@Nonnull String target, @Nonnull String message) {
      return callback.onPrivmsg(tags, prefix, target, message);
    }

    @Override
    public T onQuit(@Nullable String reason) {
      return callback.onQuit(tags, prefix, reason);
    }

    @Override
    public T onReply(int code, @Nonnull String target, @Nonnull List<String> arguments) {
      this.target = target;
      T temp = CodeParser.parse(code, arguments, this);
      this.target = null;
      return temp;
    }

    @Override
    public T onUnknownCap(@Nonnull String clientId, @Nonnull String subcommand, @Nonnull List<String> arguments) {
      return callback.onUnknownCap(tags, prefix, clientId, subcommand, arguments);
    }

    @Override
    public T onUnknownCode(int code, @Nonnull List<String> arguments) {
      return callback.onUnknownCode(tags, prefix, target, code, arguments);
    }

    @Override
    public T onUnknownCommand(@Nonnull String command, @Nonnull List<String> arguments) {
      return callback.onUnknownCommand(tags, prefix, command, arguments);
    }

    @Override
    public T onWelcome(@Nonnull String message) {
      return callback.onWelcome(tags, prefix, target, message);
    }
  }
}
