package ru.hh.loginchecker;

import ru.hh.cmd.ArgsParser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

final class Arguments {

  final String username;
  final String password;
  final int numOfAttempts;

  static Arguments fromCmdArgs(final String[] args) {

    final Map<String, String> argNameToValue;
    try {
      argNameToValue = ArgsParser.argsToMap(args);
    } catch (IllegalArgumentException e) {
      throw createUsageException();
    }

    final Set<String> missingArgs = expectedArgs().stream()
        .filter(expectedArg -> !argNameToValue.containsKey(expectedArg))
        .collect(toSet());

    if (!missingArgs.isEmpty()) {
      throw createUsageException();
    }

    final int numOfAttempts = Integer.parseInt(argNameToValue.get("-n"));

    return new Arguments(argNameToValue.get("-u"), argNameToValue.get("-p"), numOfAttempts);
  }

  private static IllegalArgumentException createUsageException() {
    return new IllegalArgumentException("username, password and number of attempts must be provided, " +
        "for example: -u someUsername -p somePassword -n 100"
    );
  }

  private static Set<String> expectedArgs() {
    final Set<String> expectedArgs = new HashSet<>(2);
    expectedArgs.add("-u");
    expectedArgs.add("-p");
    expectedArgs.add("-n");
    return expectedArgs;
  }

  private Arguments(final String username, final String password, final int numOfAttempts) {
    this.username = username;
    this.password = password;
    this.numOfAttempts = numOfAttempts;
  }
}
