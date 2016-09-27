package com.tilal6991.irc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Parser which considers a list of IRC code arguments and interprets them. */
public class CodeParser {

  private static final int RPL_WELCOME = 1;
  private static final int RPL_ISUPPORT = 5;
  private static final int RPL_NAMREPLY = 353;
  private static final int RPL_ENDOFNAMES = 366;

  /**
   * Parses reply message arguments. Checks that the correct number of arguments are present for the
   * given command and invokes the given callback synchronously with the parsed arguments if parsing
   * was successful.
   *
   * @param code IRC reply code to parse the arguments in the context of.
   * @param arguments the arguments to parse.
   * @param callback the callback to invoke with the parsed arguments.
   * @throws IllegalArgumentException if an error occurs in the parsing.
   */
  public static void parse(int code, @Nonnull List<String> arguments, @Nonnull Callback callback) {
    switch (code) {
      case RPL_WELCOME:
        checkCountOneOf(code, arguments, 1);
        callback.onWelcome(arguments.get(0));
        break;
      case RPL_ISUPPORT:
        checkCountOneOf(code, arguments, 2);
        callback.onIsupport(
            arguments.subList(0, arguments.size() - 1), arguments.get(arguments.size() - 1));
        break;
      case RPL_NAMREPLY:
        callback.onNames(arguments);
        break;
      case RPL_ENDOFNAMES:
        checkCountOneOf(code, arguments, 2);
        callback.onEndOfNames(arguments.get(0), arguments.get(1));
        break;
      default:
        throw new IllegalArgumentException(
            String.format(Locale.getDefault(), "Unknown code provided: %d", code));
    }
  }

  private static void checkCountOneOf(int code, List<String> arguments, int... counts) {
    int index = Arrays.binarySearch(counts, arguments.size());
    if (index < 0) {
      throw new IllegalArgumentException(
          String.format(
              Locale.getDefault(),
              "Code: %d. Expected argument count: %s. Actual argument count: %d.",
              code,
              Arrays.toString(counts),
              arguments.size()));
    }
  }

  /** Callback class which will be invoked when parsing is successful */
  interface Callback {

    /** Callback method for RPL_WELCOME code. */
    void onWelcome(@Nonnull String message);

    /** Callback method for RPL_ISUPPORT code. */
    void onIsupport(@Nonnull List<String> tokens, @Nonnull String s);

    /** Callback method for RPL_NAMREPLY code. */
    void onNames(@Nullable List<String> arguments);

    /** Callback method for RPL_ENDOFNAMES code. */
    void onEndOfNames(@Nonnull String channel, @Nonnull String message);
  }
}
