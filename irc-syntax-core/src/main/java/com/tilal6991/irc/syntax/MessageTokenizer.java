package com.tilal6991.irc.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Tokenizer which is able to break an IRC line into its constituent parts. */
public class MessageTokenizer {

  // No instances of tokenizer.
  private MessageTokenizer() {
  }

  /**
   * Tokenizes an IRC line. Returns the result using the callback provided. The callback will be
   * invoked synchronously.
   *
   * @param line an IRC line to tokenize.
   * @throws IllegalArgumentException if an error occurs in the parsing.
   */
  public static <T> T tokenize(@Nonnull String line, @Nonnull Callback<T> callback) {
    if (line.isEmpty()) {
      throw new IllegalArgumentException("Empty line cannot be parsed.");
    }

    String trimmed = line.trim();
    int pos = 0;
    int end;

    List<String> tags;
    if (trimmed.charAt(pos) != '@') {
      tags = null;
    } else {
      tags = new ArrayList<>();

      // Consume the @ character.
      pos += 1;

      // Find out where all the tags end.
      int allTagsEnd = trimmed.indexOf(' ', pos);
      if (allTagsEnd == -1) {
        throw new IllegalArgumentException("Unable to tokenize message without command.");
      }

      // Extract all non-final tags.
      end = trimmed.indexOf(';', pos);
      while (end != -1 && end < allTagsEnd) {
        tags.add(trimmed.substring(pos, end));
        pos = end + 1;
        end = trimmed.indexOf(';', pos);
      }

      // Add the last tag.
      end = allTagsEnd;
      tags.add(trimmed.substring(pos, end));

      // Consume the last space.
      pos = end + 1;
    }

    String prefix;
    if (trimmed.charAt(pos) != ':') {
      prefix = null;
    } else {
      // Consume the : character.
      pos += 1;
      end = trimmed.indexOf(' ', pos);

      if (end == -1) {
        throw new IllegalArgumentException("Unable to tokenize message without command.");
      }
      prefix = trimmed.substring(pos, end);
      pos = end + 1;
    }

    // Parse the command.
    end = indexOfSpaceOrLength(trimmed, pos);
    String command = trimmed.substring(pos, end);
    pos = end + 1;

    // Parse the arguments.
    List<String> arguments;
    if (pos >= trimmed.length()) {
      arguments = Collections.emptyList();
    } else if (trimmed.charAt(pos) == ':') {
      pos += 1;
      arguments = Collections.singletonList(trimmed.substring(pos, trimmed.length()));
    } else {
      arguments = new ArrayList<>();
      end = trimmed.indexOf(' ', pos);
      while (end != -1) {
        arguments.add(trimmed.substring(pos, end));
        pos = end + 1;
        // We're OK looking end + 1 because this string is trimmed.
        if (trimmed.charAt(end + 1) == ':') {
          // Consume the colon.
          pos += 1;
          break;
        }
        end = trimmed.indexOf(' ', pos);
      }
      arguments.add(trimmed.substring(pos, trimmed.length()));
    }
    return callback.onLineTokenized(tags, prefix, command, arguments);
  }

  private static int indexOfSpaceOrLength(String input, int pos) {
    int end = input.indexOf(' ', pos);
    return end == -1 ? input.length() : end;
  }

  /** Callback class which will be used when parsing is successful */
  public interface Callback<T> {

    /**
     * Callback method which will invoked when tokenization is successful.
     *
     * @param tags tag as defined by IRCv3.2 message tags spec.
     * @param command command as specified by RFC1459.
     * @param prefix prefix as specified by RFC1459.
     * @param arguments the arguments as specified by RFC1459.
     */
    T onLineTokenized(@Nullable List<String> tags,
                      @Nullable String prefix,
                      @Nonnull String command,
                      @Nonnull List<String> arguments);
  }
}