package com.tilal6991.irc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.tilal6991.irc.Utils.getOrNull;

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
  public static void parse(
      @Nonnull String command, @Nonnull List<String> arguments, @Nonnull Callback callback) {
    switch (command) {
      case "NICK":
        checkCountOneOf(command, arguments, 1);
        callback.onNick(arguments.get(0));
        break;
      case "QUIT":
        checkCountOneOf(command, arguments, 0, 1);
        callback.onQuit(getOrNull(arguments, 0));
        break;
      case "JOIN":
        checkCountGreaterThanEq(command, arguments, 1);
        callback.onJoin(arguments.get(0), arguments.subList(1, arguments.size()));
        break;
      case "PART":
        checkCountOneOf(command, arguments, 1, 2);
        callback.onPart(arguments.get(0), getOrNull(arguments, 1));
        break;
      case "MODE":
        checkCountGreaterThanEq(command, arguments, 2);
        callback.onMode(arguments.get(0), arguments.subList(1, arguments.size()));
        break;
      case "INVITE":
        checkCountOneOf(command, arguments, 2);
        callback.onInvite(arguments.get(0), arguments.get(1));
        break;
      case "KICK":
        checkCountOneOf(command, arguments, 2, 3);
        callback.onKick(arguments.get(0), arguments.get(1), getOrNull(arguments, 2));
        break;
      case "AUTHENTICATE":
        checkCountOneOf(command, arguments, 1);
        callback.onAuthenticate(arguments.get(0));
        break;
      case "ACCOUNT":
        checkCountOneOf(command, arguments, 1);
        callback.onAccount(arguments.get(0));
        break;
      case "CHGHOST":
        checkCountOneOf(command, arguments, 2);
        callback.onChghost(arguments.get(0), arguments.get(1));
        break;
      case "PRIVMSG":
        checkCountOneOf(command, arguments, 2);
        callback.onPrivmsg(arguments.get(0), arguments.get(1));
        break;
      case "NOTICE":
        checkCountOneOf(command, arguments, 2);
        callback.onNotice(arguments.get(0), arguments.get(1));
        break;
      case "AWAY":
        checkCountOneOf(command, arguments, 0, 1);
        callback.onAway(getOrNull(arguments, 0));
        break;
      case "PING":
        checkCountOneOf(command, arguments, 0, 1);
        callback.onPing(getOrNull(arguments, 0));
        break;
      case "BATCH":
        checkCountGreaterThanEq(command, arguments, 2);
        if (arguments.get(0).length() < 2) {
          throw new IllegalArgumentException("Reference tag for batch not present");
        }
        callback.onBatch(
            arguments.get(0).charAt(0),
            arguments.get(0).substring(1),
            arguments.get(1),
            arguments.subList(2, arguments.size()));
        break;
      case "CAP":
        checkCountGreaterThanEq(command, arguments, 1);
        callback.onCap(arguments);
        break;
      default:
        int code = parseCode(command);
        if (code == -1) {
          throw new IllegalArgumentException(String.format("Unknown command %s.", command));
        }
        checkCountGreaterThanEq(command, arguments, 1);
        callback.onReply(code, arguments.get(0), arguments.subList(1, arguments.size()));
        break;
    }
  }

  private static void checkCountOneOf(String command, List<String> arguments, int... counts) {
    int index = Arrays.binarySearch(counts, arguments.size());
    if (index < 0) {
      throw new IllegalArgumentException(
          String.format(
              Locale.getDefault(),
              "Command: %s. Expected argument count: %s. Actual argument count: %d.",
              command,
              Arrays.toString(counts),
              arguments.size()));
    }
  }

  private static void checkCountGreaterThanEq(String command, List<String> arguments, int count) {
    if (arguments.size() < count) {
      throw new IllegalArgumentException(
          String.format(
              Locale.getDefault(),
              "Command: %s. Expected argument count geq: %d. Actual argument count: %d.",
              command,
              count,
              arguments.size()));
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
  interface Callback {

    /** Callback method for PING command. */
    void onPing(@Nullable String hostname);

    /** Callback method for QUIT command. */
    void onQuit(@Nullable String reason);

    /** Callback method for JOIN command. */
    void onJoin(@Nonnull String channel, @Nonnull List<String> args);

    /** Callback method for a MODE message */
    void onMode(@Nonnull String target, @Nonnull List<String> args);

    /** Callback method for KICK command. */
    void onKick(@Nonnull String channel, @Nonnull String user, @Nullable String reason);

    /** Callback method for NICK command. */
    void onNick(@Nonnull String nick);

    /** Callback method for PART command. */
    void onPart(@Nonnull String channel, @Nullable String reason);

    /** Callback method for AUTHENTICATE command. */
    void onAuthenticate(@Nonnull String data);

    /** Callback method for ACCOUNT command. */
    void onAccount(@Nullable String account);

    /** Callback method for CHGHOST command. */
    void onChghost(@Nonnull String newUser, @Nonnull String newHost);

    /** Callback method for PRIVMSG command. */
    void onPrivmsg(@Nonnull String target, @Nonnull String message);

    /** Callback method for NOTICE command. */
    void onNotice(@Nonnull String target, @Nonnull String message);

    /** Callback method for INVITE command. */
    void onInvite(@Nonnull String target, @Nonnull String channel);

    /** Callback method for AWAY command. */
    void onAway(@Nullable String message);

    /** Callback method for BATCH command. */
    void onBatch(
        char referenceModifier,
        @Nonnull String referenceTag,
        @Nonnull String type,
        @Nonnull List<String> args);

    /** Callback method for CAP command. */
    void onCap(@Nonnull List<String> args);

    /** Callback method for a reply message. */
    void onReply(int code, @Nonnull String target, @Nonnull List<String> args);
  }
}