package com.tilal6991.irc.syntax;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.tilal6991.irc.syntax.Utils.getOrNull;

/** Parser which considers a list of CAP message arguments from a IRC client and interprets them. */
public class ClientCapParser {

  // No instances of parser.
  private ClientCapParser() {
  }

  /**
   * Parses IRC CAP arguments on the client side. Checks that the correct number of arguments are
   * present for the given command and invokes the given callback synchronously with the parsed
   * arguments if parsing was successful.
   *
   * @param arguments the arguments to parse.
   * @param callback the callback to invoke with the parsed arguments.
   * @throws IllegalArgumentException if an error occurs in the parsing.
   */
  public static <T> T parse(@Nonnull List<String> arguments, @Nonnull Callback<T> callback) {
    if (arguments.size() != 2 && arguments.size() != 3 && arguments.size() != 4) {
      throw new IllegalArgumentException(
          String.format("Client CAP on has wrong number of arguments: %d", arguments.size()));
    }

    String clientId = arguments.get(0);
    String subcommand = arguments.get(1);
    String thirdArg = getOrNull(arguments, 2);
    if (subcommand.equals("LS") || subcommand.equals("LIST")) {
      boolean finalLine = thirdArg == null || thirdArg.length() != 1 || thirdArg.charAt(0) != '*';
      List<String> modCapsAndValues;
      if (finalLine) {
        modCapsAndValues = thirdArg == null ? null : Utils.tokenizeOnSpace(thirdArg);
      } else {
        String fourthArg = getOrNull(arguments, 3);
        if (fourthArg == null) {
          throw new IllegalArgumentException(
              String.format("Client CAP on has wrong number of arguments: %d", arguments.size()));
        }
        modCapsAndValues = Utils.tokenizeOnSpace(fourthArg);
      }

      if (subcommand.equals("LIST")) {
        callback.onCapList(clientId, finalLine, modCapsAndValues);
      } else {
        callback.onCapLs(clientId, finalLine, modCapsAndValues);
      }
    } else {
      if (arguments.size() == 4) {
        throw new IllegalArgumentException(
            String.format("Client CAP on has wrong number of arguments: %d", 4));
      }

      List<String> modCapsAndValues = thirdArg == null ? null : Utils.tokenizeOnSpace(thirdArg);
      switch (subcommand) {
        case "ACK":
          return callback.onCapAck(clientId, modCapsAndValues);
        case "NAK":
          return callback.onCapNak(clientId, modCapsAndValues);
        case "NEW":
          return callback.onCapNew(clientId, modCapsAndValues);
        case "DEL":
          return callback.onCapDel(clientId, modCapsAndValues);
      }
    }
    return callback.onUnknownCap(subcommand, arguments.subList(1, arguments.size()));
  }

  /** Callback class which will be invoked when parsing is successful */
  public interface Callback<T> {

    /** Callback method for LS subcommand. */
    T onCapLs(
        @Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues);

    /** Callback method for LIST subcommand. */
    T onCapList(
        @Nonnull String clientId, boolean finalLine, @Nullable List<String> modCapabilityAndValues);

    /** Callback method for ACK subcommand. */
    T onCapAck(@Nonnull String clientId, @Nullable List<String> modCapabilityAndValues);

    /** Callback method for NAK subcommand. */
    T onCapNak(@Nonnull String clientId, @Nullable List<String> modCapabilityAndValues);

    /** Callback method for NEW subcommand. */
    T onCapNew(@Nonnull String clientId, @Nullable List<String> modCapabilityAndValues);

    /** Callback method for DEL subcommand. */
    T onCapDel(@Nonnull String clientId, @Nullable List<String> modCapabilityAndValues);

    /** Callback method for unknown subcommand. */
    T onUnknownCap(@Nonnull String subcommand, @Nonnull List<String> arguments);
  }
}