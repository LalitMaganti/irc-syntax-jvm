package com.tilal6991.irc.syntax;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Contains utility methods for use by parsers and tokenizers. */
class Utils {

  // No instances of Utils.
  private Utils() {
  }

  static String getOrNull(@Nullable List<String> list, int index) {
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
}
