package com.tilal6991.irc.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Parser which considers a list of RPL_NAMREPLY arguments and interprets them. */
public class NamesParser {

  // No instances of parser.
  private NamesParser() {
  }

  /**
   * Parses RPL_NAMREPLY message arguments. Checks that the correct number of arguments are present
   * for the given command and invokes the given callback synchronously with the parsed arguments if
   * parsing was successful.
   *
   * @param arguments the arguments to parse.
   * @param callback the callback to invoke with the parsed arguments.
   * @throws IllegalArgumentException if an error occurs in the parsing.
   */
  public static <T> T parse(List<String> arguments, Callback<T> callback) {
    // RFC1459 and RFC2812 differ here - account for both cases intelligently.
    if (arguments.size() < 2) {
      throw new IllegalArgumentException(createExceptionString(2, arguments.size()));
    }

    // RFC2812 specifies that there should be a one character channel descriptor here.
    String first = arguments.get(0);
    Character descriptor;
    int offset;
    if (first.length() == 1) {
      descriptor = first.charAt(0);
      offset = 1;

      if (arguments.size() != 3) {
        throw new IllegalArgumentException(createExceptionString(3, arguments.size()));
      }
    } else {
      descriptor = null;
      offset = 0;

      if (arguments.size() != 2) {
        throw new IllegalArgumentException(createExceptionString(3, arguments.size()));
      }
    }

    String channel = arguments.get(offset);
    String namesString = arguments.get(offset + 1);
    List<String> names = Utils.tokenizeOnSpace(namesString);

    return callback.onNames(descriptor, channel, names);
  }

  private static String createExceptionString(int expected, int actual) {
    return String.format(
        Locale.getDefault(),
        "Expected argument count: %s. Actual argument count: %d.",
        expected,
        actual);
  }

  /** Callback class which will be invoked when parsing is successful */
  public interface Callback<T> {

    /** Callback method for names messages. */
    T onNames(
        @Nullable Character channelDescriptor, @Nonnull String channel, @Nonnull List<String> names);
  }
}