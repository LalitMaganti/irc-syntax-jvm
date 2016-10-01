package com.tilal6991.irc.syntax;

import java.lang.Character;
import java.lang.Override;
import java.lang.String;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DelegatingClientMessageCallback implements ClientMessageCallback<Void> {
  private final List<ClientMessageCallback<?>> callbacks;

  DelegatingClientMessageCallback() {
    callbacks = new ArrayList<>();
  }

  @Override
  public Void onAccount(@Nullable List<String> tags, @Nullable String prefix, @Nullable String account) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onAccount(tags, prefix, account);
    }
    return null;
  }

  @Override
  public Void onAuthenticate(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String data) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onAuthenticate(tags, prefix, data);
    }
    return null;
  }

  @Override
  public Void onAway(@Nullable List<String> tags, @Nullable String prefix, @Nullable String message) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onAway(tags, prefix, message);
    }
    return null;
  }

  @Override
  public Void onBatch(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String modifiedReferenceTag, @Nonnull String type, @Nonnull List<String> arguments) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onBatch(tags, prefix, modifiedReferenceTag, type, arguments);
    }
    return null;
  }

  @Override
  public Void onCapAck(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onCapAck(tags, prefix, clientId, modCapabilityAndValues);
    }
    return null;
  }

  @Override
  public Void onCapDel(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onCapDel(tags, prefix, clientId, modCapabilityAndValues);
    }
    return null;
  }

  @Override
  public Void onCapList(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onCapList(tags, prefix, clientId, finalLine, modCapabilityAndValues);
    }
    return null;
  }

  @Override
  public Void onCapLs(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onCapLs(tags, prefix, clientId, finalLine, modCapabilityAndValues);
    }
    return null;
  }

  @Override
  public Void onCapNak(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onCapNak(tags, prefix, clientId, modCapabilityAndValues);
    }
    return null;
  }

  @Override
  public Void onCapNew(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nullable List<String> modCapabilityAndValues) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onCapNew(tags, prefix, clientId, modCapabilityAndValues);
    }
    return null;
  }

  @Override
  public Void onChghost(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String newUser, @Nonnull String newHost) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onChghost(tags, prefix, newUser, newHost);
    }
    return null;
  }

  @Override
  public Void onEndOfNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel, @Nonnull String message) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onEndOfNames(tags, prefix, target, channel, message);
    }
    return null;
  }

  @Override
  public Void onInvite(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String channel) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onInvite(tags, prefix, target, channel);
    }
    return null;
  }

  @Override
  public Void onIsupport(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message, @Nonnull List<String> tokens) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onIsupport(tags, prefix, target, message, tokens);
    }
    return null;
  }

  @Override
  public Void onJoin(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull List<String> arguments) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onJoin(tags, prefix, channel, arguments);
    }
    return null;
  }

  @Override
  public Void onKick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nonnull String user, @Nullable String reason) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onKick(tags, prefix, channel, user, reason);
    }
    return null;
  }

  @Override
  public Void onMode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull List<String> arguments) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onMode(tags, prefix, target, arguments);
    }
    return null;
  }

  @Override
  public Void onNames(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onNames(tags, prefix, target, channelDescriptor, channel, names);
    }
    return null;
  }

  @Override
  public Void onNick(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String nick) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onNick(tags, prefix, nick);
    }
    return null;
  }

  @Override
  public Void onNotice(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onNotice(tags, prefix, target, message);
    }
    return null;
  }

  @Override
  public Void onPart(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String channel, @Nullable String reason) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onPart(tags, prefix, channel, reason);
    }
    return null;
  }

  @Override
  public Void onPing(@Nullable List<String> tags, @Nullable String prefix, @Nullable String hostname) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onPing(tags, prefix, hostname);
    }
    return null;
  }

  @Override
  public Void onPrivmsg(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onPrivmsg(tags, prefix, target, message);
    }
    return null;
  }

  @Override
  public Void onQuit(@Nullable List<String> tags, @Nullable String prefix, @Nullable String reason) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onQuit(tags, prefix, reason);
    }
    return null;
  }

  @Override
  public Void onUnknownCap(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String clientId, @Nonnull String subcommand, @Nonnull List<String> arguments) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onUnknownCap(tags, prefix, clientId, subcommand, arguments);
    }
    return null;
  }

  @Override
  public Void onUnknownCode(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, int code, @Nonnull List<String> arguments) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onUnknownCode(tags, prefix, target, code, arguments);
    }
    return null;
  }

  @Override
  public Void onUnknownCommand(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String command, @Nonnull List<String> arguments) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onUnknownCommand(tags, prefix, command, arguments);
    }
    return null;
  }

  @Override
  public Void onWelcome(@Nullable List<String> tags, @Nullable String prefix, @Nonnull String target, @Nonnull String message) {
    for (ClientMessageCallback<?> callback : callbacks) {
      callback.onWelcome(tags, prefix, target, message);
    }
    return null;
  }

  public <T> void addCallback(ClientMessageCallback<T> callback) {
    callbacks.add(callback);
  }

  public <T> void removeCallback(ClientMessageCallback<T> callback) {
    callbacks.remove(callback);
  }
}
