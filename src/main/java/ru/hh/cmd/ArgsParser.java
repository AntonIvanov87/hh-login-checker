package ru.hh.cmd;

import java.util.HashMap;
import java.util.Map;

public final class ArgsParser {

  public static Map<String, String> argsToMap(final String[] args) {

    final int argsLength = args.length;

    if (argsLength % 2 != 0) {
      throw new IllegalArgumentException(
              "failed to convert array of arguments to map: expected even number of arguments, but got " + argsLength);
    }

    final Map<String, String> nameToValue = new HashMap<>(argsLength / 2);
    for (int i = 0; i< argsLength; i += 2) {
      nameToValue.put(args[i], args[i+1]);
    }

    return nameToValue;
  }

  private ArgsParser() {
  }
}
