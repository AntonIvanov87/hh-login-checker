package ru.hh.loginchecker;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import ru.hh.http.SimpleBrowser;
import ru.hh.http.SimpleCookiesHolder;

final class LoginPerformer {

  private final AsyncHttpClient asyncHttpClient;

  LoginPerformer(final AsyncHttpClient asyncHttpClient) {
    this.asyncHttpClient = asyncHttpClient;
  }

  Response login(final String username, final String password) {

    final SimpleCookiesHolder simpleCookiesHolder = new SimpleCookiesHolder();
    final SimpleBrowser simpleBrowser = new SimpleBrowser(simpleCookiesHolder, asyncHttpClient);

    final Request rootRequest = rootRequest();
    final Response rootResponse = simpleBrowser.request(rootRequest);
    if (rootResponse.getStatusCode() != 200) {
      return rootResponse;
    }

    final String xsrf = simpleCookiesHolder.getCookies("hh.ru").get("_xsrf");
    final Request loginRequest = loginRequest(username, password, xsrf);
    return simpleBrowser.request(loginRequest);
  }

  private static Request rootRequest() {
    return new RequestBuilder("GET").setUrl("http://hh.ru/").build();
  }

  private static Request loginRequest(final String username, final String password, final String xsrf) {
    return new RequestBuilder("POST")
            .setUrl("https://hh.ru/account/login")
            .addFormParam("username", username)
            .addFormParam("password", password)
            .addFormParam("backUrl", "http://hh.ru/")
            .addFormParam("action", "Войти")
            .addFormParam("_xsrf", xsrf)
            .build();
  }


}
