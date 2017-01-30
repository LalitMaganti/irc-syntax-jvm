package com.tilal6991.irc.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

/** Contains utility methods for use by parsers and tokenizers. */
class Utils {

  // No instances of Utils.
  private Utils() {
  }

  static String getOrNull(@Nonnull List<String> list, int index) {
    if (index >= list.size()) {
      return null;
    }
    return list.get(index);
  }

  static List<String> tokenizeOnSpace(@Nonnull String str) {
    List<String> tokens = new ArrayList<>();
    int pos = 0;
    int end = str.indexOf(' ', pos);
    while (end != -1) {
      tokens.add(str.substring(pos, end));
      pos = end + 1;
      end = str.indexOf(' ', pos);
    }
    tokens.add(str.substring(pos, str.length()));
    return tokens;
  }

  static void checkCountOneOf(String command, List<String> arguments, int... counts) {
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

  static void checkCountIs(int code, List<String> arguments, int count) {
    if (arguments.size() != count) {
      throw new IllegalArgumentException(
              String.format(
                      Locale.getDefault(),
                      "Code: %d. Expected argument count: %s. Actual argument count: %d.",
                      code,
                      count,
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

  static void checkCountIsGeq(int code, List<String> arguments, int count) {
    if (arguments.size() < count) {
      throw new IllegalArgumentException(
              String.format(
                      Locale.getDefault(),
                      "Code: %d. Expected argument count geq: %s. Actual argument count: %d.",
                      code,
                      count,
                      arguments.size()));
    }
  }
}
