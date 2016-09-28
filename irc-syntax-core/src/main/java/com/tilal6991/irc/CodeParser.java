package com.tilal6991.irc;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

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
        checkCountIs(code, arguments, 1);
        callback.onWelcome(arguments.get(0));
        break;
      case RPL_ISUPPORT:
        checkCountIs(code, arguments, 2);
        callback.onIsupport(
            arguments.get(arguments.size() - 1), arguments.subList(0, arguments.size() - 1));
        break;
      case RPL_NAMREPLY:
        callback.onNamReply(arguments);
        break;
      case RPL_ENDOFNAMES:
        checkCountIs(code, arguments, 2);
        callback.onEndOfNames(arguments.get(0), arguments.get(1));
        break;
      default:
        callback.onUnknownCode(code, arguments);
        break;
    }
  }

  private static void checkCountIs(int code, List<String> arguments, int counts) {
    if (arguments.size() != counts) {
      throw new IllegalArgumentException(
          String.format(
              Locale.getDefault(),
              "Code: %d. Expected argument count: %s. Actual argument count: %d.",
              code,
              counts,
              arguments.size()));
    }
  }

  /** Callback class which will be invoked when parsing is successful */
  public interface Callback {

    /** Callback method for RPL_WELCOME code. */
    void onWelcome(@Nonnull String message);

    /** Callback method for RPL_ISUPPORT code. */
    void onIsupport(@Nonnull String message, @Nonnull List<String> tokens);

    /** Callback method for RPL_NAMREPLY code. */
    void onNamReply(@Nonnull List<String> arguments);

    /** Callback method for RPL_ENDOFNAMES code. */
    void onEndOfNames(@Nonnull String channel, @Nonnull String message);

    /** Callback method for any unknown codes */
    void onUnknownCode(int code, @Nonnull List<String> arguments);
  }
}
