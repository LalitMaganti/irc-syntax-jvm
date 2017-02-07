package com.tilal6991.irc.syntax;

import static com.tilal6991.irc.syntax.Utils.checkCountIs;
import static com.tilal6991.irc.syntax.Utils.checkCountIsGeq;

import java.util.List;
import javax.annotation.Nonnull;

/** Parser which considers a list of IRC code arguments and interprets them. */
public class CodeParser {

  private static final int RPL_WELCOME = 1;
  private static final int RPL_ISUPPORT = 5;
  private static final int RPL_NAMREPLY = 353;
  private static final int RPL_ENDOFNAMES = 366;

  // No instances of parser.
  private CodeParser() {
  }

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
  public static <T> T parse(
      int code, @Nonnull List<String> arguments, @Nonnull Callback<T> callback) {
    switch (code) {
      case RPL_WELCOME:
        checkCountIs(code, arguments, 1);
        return callback.onWelcome(arguments.get(0));
      case RPL_ISUPPORT:
        checkCountIsGeq(code, arguments, 2);
        return callback.onIsupport(
            arguments.get(arguments.size() - 1), arguments.subList(0, arguments.size() - 1));
      case RPL_NAMREPLY:
        return callback.onNamReply(arguments);
      case RPL_ENDOFNAMES:
        checkCountIs(code, arguments, 2);
        return callback.onEndOfNames(arguments.get(0), arguments.get(1));
      default:
        return callback.onUnknownCode(code, arguments);
    }
  }

  /** Callback class which will be invoked when parsing is successful */
  public interface Callback<T> {

    /** Callback method for RPL_WELCOME code. */
    T onWelcome(@Nonnull String message);

    /** Callback method for RPL_ISUPPORT code. */
    T onIsupport(@Nonnull String message, @Nonnull List<String> tokens);

    /** Callback method for RPL_NAMREPLY code. */
    T onNamReply(@Nonnull List<String> arguments);

    /** Callback method for RPL_ENDOFNAMES code. */
    T onEndOfNames(@Nonnull String channel, @Nonnull String message);

    /** Callback method for any unknown codes */
    T onUnknownCode(int code, @Nonnull List<String> arguments);
  }
}
