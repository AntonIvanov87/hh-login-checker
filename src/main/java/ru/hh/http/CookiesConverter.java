package ru.hh.http;

import com.ning.http.client.cookie.Cookie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class CookiesConverter {

  static Map<String, String> toSimpleCookies(final Collection<Cookie> cookies) {
    final HashMap<String, String> simpleCookies = new HashMap<>();
    cookies.forEach(cookie -> simpleCookies.put(cookie.getName(), cookie.getValue()));
    return simpleCookies;
  }

  private CookiesConverter() {
  }
}
