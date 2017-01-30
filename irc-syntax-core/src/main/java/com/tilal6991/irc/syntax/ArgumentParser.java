package com.tilal6991.irc.syntax;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.tilal6991.irc.syntax.Utils.checkCountIsGeq;
import static com.tilal6991.irc.syntax.Utils.checkCountOneOf;

/** Parser which considers a list of IRC message arguments and interprets them. */
public class ArgumentParser {

  // No instances of parser.
  private ArgumentParser() {
  }

  /**
   * Parses IRC message arguments. Checks that the correct number of arguments are present for the
   * given command and invokes the given callback synchronously with the parsed arguments if parsing
   * was successful.
   *
   * @param command IRC command to parse the arguments in the context of.
   * @param arguments the arguments to parse.
   * @param callback the callback to invoke with the parsed arguments.
   * @throws IllegalArgumentException if an error occurs in the parsing.
   */
  public static <T> T parse(
      @Nonnull String command, @Nonnull List<String> arguments, @Nonnull Callback<T> callback) {
    switch (command) {
      case "NICK":
        checkCountOneOf(command, arguments, 1);
        return callback.onNick(arguments.get(0));
      case "QUIT":
        checkCountOneOf(command, arguments, 0, 1);
        return callback.onQuit(Utils.getOrNull(arguments, 0));
      case "JOIN":
        checkCountIsGeq(command, arguments, 1);
        return callback.onJoin(arguments.get(0), arguments.subList(1, arguments.size()));
      case "PART":
        checkCountOneOf(command, arguments, 1, 2);
        return callback.onPart(arguments.get(0), Utils.getOrNull(arguments, 1));
      case "MODE":
        checkCountIsGeq(command, arguments, 2);
        return callback.onMode(arguments.get(0), arguments.subList(1, arguments.size()));
      case "INVITE":
        checkCountOneOf(command, arguments, 2);
        return callback.onInvite(arguments.get(0), arguments.get(1));
      case "KICK":
        checkCountOneOf(command, arguments, 2, 3);
        return callback.onKick(arguments.get(0), arguments.get(1), Utils.getOrNull(arguments, 2));
      case "PRIVMSG":
        checkCountOneOf(command, arguments, 2);
        return callback.onPrivmsg(arguments.get(0), arguments.get(1));
      case "NOTICE":
        checkCountOneOf(command, arguments, 2);
        return callback.onNotice(arguments.get(0), arguments.get(1));
      case "PING":
        checkCountOneOf(command, arguments, 0, 1);
        return callback.onPing(Utils.getOrNull(arguments, 0));
      case "CAP":
        checkCountIsGeq(command, arguments, 1);
        return callback.onCap(arguments);
      default:
        int code = parseCode(command);
        if (code == -1) {
          return callback.onUnknownCommand(command, arguments);
        } else {
          checkCountIsGeq(command, arguments, 1);
          return callback.onReply(code, arguments.get(0), arguments.subList(1, arguments.size()));
        }
    }
  }

  private static int parseCode(String input) {
    int length = input.length();
    if (length != 3) {
      return -1;
    }

    int result = 0;
    int base = 100;
    for (int i = 0; i < length; i++) {
      char c = input.charAt(i);
      if (c < '0' || c > '9') {
        return -1;
      }
      result += (c - '0') * base;
      base /= 10;
    }
    return result;
  }

  /** Callback class which will be invoked when parsing is successful */
  public interface Callback<T> {

    /** Callback method for PING command. */
    T onPing(@Nullable String hostname);

    /** Callback method for QUIT command. */
    T onQuit(@Nullable String reason);

    /** Callback method for JOIN command. */
    T onJoin(@Nonnull String channel, @Nonnull List<String> arguments);

    /** Callback method for a MODE message */
    T onMode(@Nonnull String target, @Nonnull List<String> arguments);

    /** Callback method for KICK command. */
    T onKick(@Nonnull String channel, @Nonnull String user, @Nullable String reason);

    /** Callback method for NICK command. */
    T onNick(@Nonnull String nick);

    /** Callback method for PART command. */
    T onPart(@Nonnull String channel, @Nullable String reason);

    /** Callback method for PRIVMSG command. */
    T onPrivmsg(@Nonnull String target, @Nonnull String message);

    /** Callback method for NOTICE command. */
    T onNotice(@Nonnull String target, @Nonnull String message);

    /** Callback method for INVITE command. */
    T onInvite(@Nonnull String target, @Nonnull String channel);

    /** Callback method for CAP command. */
    T onCap(@Nonnull List<String> arguments);

    /** Callback method for a reply message. */
    T onReply(int code, @Nonnull String target, @Nonnull List<String> arguments);

    /** Callback method for unknown command */
    T onUnknownCommand(@Nonnull String command, @Nonnull List<String> arguments);
  }
}