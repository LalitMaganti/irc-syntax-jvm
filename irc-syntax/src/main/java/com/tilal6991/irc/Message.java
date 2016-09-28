package com.tilal6991.irc;

import java.lang.Character;
import java.lang.String;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Message {
  @Nullable
  public final List<String> tags;

  @Nullable
  public final String prefix;

  public Message(@Nullable List<String> tags, @Nullable String prefix) {
    this.tags = tags;
    this.prefix = prefix;
  }

  public static class Reply extends Message {
    @Nonnull
    public final String target;

    public Reply(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target) {
      super(tags, prefix);
      this.target = target;
    }
  }

  public static final class Join extends Message {
    @Nonnull
    public final String channel;

    @Nonnull
    public final List<String> arguments;

    public Join(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull List<String> arguments) {
      super(tags, prefix);
      this.channel = channel;
      this.arguments = arguments;
    }
  }

  public static final class Mode extends Message {
    @Nonnull
    public final String target;

    @Nonnull
    public final List<String> arguments;

    public Mode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull List<String> arguments) {
      super(tags, prefix);
      this.target = target;
      this.arguments = arguments;
    }
  }

  public static final class Kick extends Message {
    @Nonnull
    public final String channel;

    @Nonnull
    public final String user;

    @Nullable
    public final String reason;

    public Kick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull String user, @Nullable String reason) {
      super(tags, prefix);
      this.channel = channel;
      this.user = user;
      this.reason = reason;
    }
  }

  public static final class Nick extends Message {
    @Nonnull
    public final String nick;

    public Nick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String nick) {
      super(tags, prefix);
      this.nick = nick;
    }
  }

  public static final class Part extends Message {
    @Nonnull
    public final String channel;

    @Nullable
    public final String reason;

    public Part(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nullable String reason) {
      super(tags, prefix);
      this.channel = channel;
      this.reason = reason;
    }
  }

  public static final class Authenticate extends Message {
    @Nonnull
    public final String data;

    public Authenticate(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String data) {
      super(tags, prefix);
      this.data = data;
    }
  }

  public static final class Account extends Message {
    @Nullable
    public final String account;

    public Account(@Nullable List<String> tags, @Nullable String prefix, @Nullable String account) {
      super(tags, prefix);
      this.account = account;
    }
  }

  public static final class Chghost extends Message {
    @Nonnull
    public final String newUser;

    @Nonnull
    public final String newHost;

    public Chghost(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String newUser, @Nonnull String newHost) {
      super(tags, prefix);
      this.newUser = newUser;
      this.newHost = newHost;
    }
  }

  public static final class Privmsg extends Message {
    @Nonnull
    public final String target;

    @Nonnull
    public final String message;

    public Privmsg(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
      super(tags, prefix);
      this.target = target;
      this.message = message;
    }
  }

  public static final class Invite extends Message {
    @Nonnull
    public final String target;

    @Nonnull
    public final String channel;

    public Invite(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel) {
      super(tags, prefix);
      this.target = target;
      this.channel = channel;
    }
  }

  public static final class UnknownCommand extends Message {
    @Nonnull
    public final String command;

    @Nonnull
    public final List<String> arguments;

    public UnknownCommand(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String command, @Nonnull List<String> arguments) {
      super(tags, prefix);
      this.command = command;
      this.arguments = arguments;
    }
  }

  public static final class Ping extends Message {
    @Nullable
    public final String hostname;

    public Ping(@Nullable List<String> tags, @Nullable String prefix, @Nullable String hostname) {
      super(tags, prefix);
      this.hostname = hostname;
    }
  }

  public static final class Away extends Message {
    @Nullable
    public final String message;

    public Away(@Nullable List<String> tags, @Nullable String prefix, @Nullable String message) {
      super(tags, prefix);
      this.message = message;
    }
  }

  public static final class Batch extends Message {
    @Nonnull
    public final String modifiedReferenceTag;

    @Nonnull
    public final String type;

    @Nonnull
    public final List<String> arguments;

    public Batch(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String modifiedReferenceTag, @Nonnull String type, @Nonnull List<String> arguments) {
      super(tags, prefix);
      this.modifiedReferenceTag = modifiedReferenceTag;
      this.type = type;
      this.arguments = arguments;
    }
  }

  public static final class Cap extends Message {
    @Nonnull
    public final List<String> args;

    public Cap(@Nullable List<String> tags, @Nullable String prefix, @Nonnull List<String> args) {
      super(tags, prefix);
      this.args = args;
    }
  }

  public static final class Notice extends Message {
    @Nonnull
    public final String target;

    @Nonnull
    public final String message;

    public Notice(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
      super(tags, prefix);
      this.target = target;
      this.message = message;
    }
  }

  public static final class Quit extends Message {
    @Nullable
    public final String reason;

    public Quit(@Nullable List<String> tags, @Nullable String prefix, @Nullable String reason) {
      super(tags, prefix);
      this.reason = reason;
    }
  }

  public static final class UnknownCode extends Reply {
    public final int code;

    @Nonnull
    public final List<String> arguments;

    public UnknownCode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, int code, @Nonnull List<String> arguments) {
      super(tags, prefix, target);
      this.code = code;
      this.arguments = arguments;
    }
  }

  public static final class Welcome extends Reply {
    @Nonnull
    public final String message;

    public Welcome(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
      super(tags, prefix, target);
      this.message = message;
    }
  }

  public static final class EndOfNames extends Reply {
    @Nonnull
    public final String channel;

    @Nonnull
    public final String message;

    public EndOfNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel, @Nonnull String message) {
      super(tags, prefix, target);
      this.channel = channel;
      this.message = message;
    }
  }

  public static final class Isupport extends Reply {
    @Nonnull
    public final String message;

    @Nonnull
    public final List<String> tokens;

    public Isupport(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message, @Nonnull List<String> tokens) {
      super(tags, prefix, target);
      this.message = message;
      this.tokens = tokens;
    }
  }

  public static final class Names extends Reply {
    @Nullable
    public final Character channelDescriptor;

    @Nonnull
    public final String channel;

    @Nonnull
    public final List<String> names;

    public Names(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names) {
      super(tags, prefix, target);
      this.channelDescriptor = channelDescriptor;
      this.channel = channel;
      this.names = names;
    }
  }
}
