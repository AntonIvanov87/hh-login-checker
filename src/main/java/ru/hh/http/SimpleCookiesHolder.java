package ru.hh.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public final class SimpleCookiesHolder {

  private final Map<String, Map<String, String>> domainToCookies = new HashMap<>();

  public Map<String, String> getCookies(final String domain) {
    final String normalizedDomain = normalizeDomain(domain);
    return domainToCookies.getOrDefault(normalizedDomain, emptyMap());
  }

  public void setCookies(final String domain, final Map<String, String> cookiesToSet) {
    final String normalizedDomain = normalizeDomain(domain);
    final Map<String, String> existingCookies = domainToCookies.getOrDefault(normalizedDomain, emptyMap());
    final Map<String, String> mergedCookies = merge(cookiesToSet, existingCookies);
    domainToCookies.put(normalizedDomain, mergedCookies);
  }

  private static Map<String, String> merge(final Map<String, String> cookiesToSet, final Map<String, String> existingCookies) {

    final Set<String> cookiesToRemove = cookiesToSet.entrySet().stream()
            .filter(cookie -> cookie.getValue() == null || cookie.getValue().trim().isEmpty())
            .map(Map.Entry::getKey)
            .collect(toSet());

    final Map<String, String> existingRemoved = existingCookies.entrySet().stream()
            .filter(cookie -> !cookiesToRemove.contains(cookie.getKey()))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    final Map<String, String> cookiesToAdd = cookiesToSet.entrySet().stream()
            .filter(cookie -> !cookiesToRemove.contains(cookie.getKey()))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    final Map<String, String> merged = new HashMap<>(existingRemoved);
    merged.putAll(cookiesToAdd);
    return merged;
  }

  private static String normalizeDomain(final String domain) {
    final String[] parts = domain.split("\\.");
    return (parts[parts.length - 2] + '.' + parts[parts.length - 1]).toLowerCase();
  }
}
