package com.tilal6991.irc;

import java.lang.Character;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MessageParser {
  private final MessageCallback callback;

  private final Inner inner;

  public MessageParser(@Nonnull MessageCallback callback) {
    this.callback = callback;
    this.inner = new Inner();
  }

  public void parse(@Nonnull String line) {
    MessageTokenizer.tokenize(line, inner);
  }

  private class Inner implements MessageTokenizer.Callback, ArgumentParser.Callback, CodeParser.Callback, NamesParser.Callback {
    private List<String> tags;

    private String prefix;

    private String target;

    @Override
    public void onAccount(@Nullable String account) {
      callback.onAccount(tags, prefix, account);
    }

    @Override
    public void onAuthenticate(@Nonnull String data) {
      callback.onAuthenticate(tags, prefix, data);
    }

    @Override
    public void onAway(@Nullable String message) {
      callback.onAway(tags, prefix, message);
    }

    @Override
    public void onBatch(@Nonnull String modifiedReferenceTag, @Nonnull String type, @Nonnull List<String> arguments) {
      callback.onBatch(tags, prefix, modifiedReferenceTag, type, arguments);
    }

    @Override
    public void onCap(@Nonnull List<String> args) {
      callback.onCap(tags, prefix, args);
    }

    @Override
    public void onChghost(@Nonnull String newUser, @Nonnull String newHost) {
      callback.onChghost(tags, prefix, newUser, newHost);
    }

    @Override
    public void onEndOfNames(@Nonnull String channel, @Nonnull String message) {
      callback.onEndOfNames(tags, prefix, target, channel, message);
    }

    @Override
    public void onInvite(@Nonnull String target, @Nonnull String channel) {
      callback.onInvite(tags, prefix, target, channel);
    }

    @Override
    public void onIsupport(@Nonnull String message, @Nonnull List<String> tokens) {
      callback.onIsupport(tags, prefix, target, message, tokens);
    }

    @Override
    public void onJoin(@Nonnull String channel, @Nonnull List<String> arguments) {
      callback.onJoin(tags, prefix, channel, arguments);
    }

    @Override
    public void onKick(@Nonnull String channel, @Nonnull String user, @Nullable String reason) {
      callback.onKick(tags, prefix, channel, user, reason);
    }

    @Override
    public void onLineTokenized(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String command, @Nonnull List<String> arguments) {
      this.tags = tags;
      this.prefix = prefix;
      ArgumentParser.parse(command, arguments, this);
      this.tags = null;
      this.prefix = null;
    }

    @Override
    public void onMode(@Nonnull String target, @Nonnull List<String> arguments) {
      callback.onMode(tags, prefix, target, arguments);
    }

    @Override
    public void onNamReply(@Nonnull List<String> arguments) {
      NamesParser.parse(arguments, this);
    }

    @Override
    public void onNames(@Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names) {
      callback.onNames(tags, prefix, target, channelDescriptor, channel, names);
    }

    @Override
    public void onNick(@Nonnull String nick) {
      callback.onNick(tags, prefix, nick);
    }

    @Override
    public void onNotice(@Nonnull String target, @Nonnull String message) {
      callback.onNotice(tags, prefix, target, message);
    }

    @Override
    public void onPart(@Nonnull String channel, @Nullable String reason) {
      callback.onPart(tags, prefix, channel, reason);
    }

    @Override
    public void onPing(@Nullable String hostname) {
      callback.onPing(tags, prefix, hostname);
    }

    @Override
    public void onPrivmsg(@Nonnull String target, @Nonnull String message) {
      callback.onPrivmsg(tags, prefix, target, message);
    }

    @Override
    public void onQuit(@Nullable String reason) {
      callback.onQuit(tags, prefix, reason);
    }

    @Override
    public void onReply(int code, @Nonnull String target, @Nonnull List<String> arguments) {
      this.target = target;
      CodeParser.parse(code, arguments, this);
      this.target = null;
    }

    @Override
    public void onUnknownCode(int code, @Nonnull List<String> arguments) {
      callback.onUnknownCode(tags, prefix, target, code, arguments);
    }

    @Override
    public void onUnknownCommand(@Nonnull String command, @Nonnull List<String> arguments) {
      callback.onUnknownCommand(tags, prefix, command, arguments);
    }

    @Override
    public void onWelcome(@Nonnull String message) {
      callback.onWelcome(tags, prefix, target, message);
    }
  }
}
