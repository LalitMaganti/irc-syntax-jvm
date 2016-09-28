package com.tilal6991.irc;

import java.util.List;

/** Contains utility methods for use by parsers and tokenizers. */
class Utils {

  // No instances of Utils.
  private Utils() {
  }

  static String getOrNull(List<String> list, int index) {
    if (index >= list.size()) {
      return null;
    }
    return list.get(index);
  }
}
