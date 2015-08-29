package ru.hh.http;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.lang.System.currentTimeMillis;
import static ru.hh.http.CookiesConverter.toSimpleCookies;

public class SimpleBrowser {

  private final SimpleCookiesHolder cookiesHolder;
  private final AsyncHttpClient asyncHttpClient;

  public SimpleBrowser(final SimpleCookiesHolder cookiesHolder, final AsyncHttpClient asyncHttpClient) {
    this.cookiesHolder = cookiesHolder;
    this.asyncHttpClient = asyncHttpClient;
  }

  public Response request(Request request) {
    while(true) {

      final Request enrichedRequest = enrichRequest(request);
      final Response response = execute(enrichedRequest);
      setCookies(enrichedRequest.getUri().getHost(), response.getCookies());

      if (response.getStatusCode() != 302) {
        if (response.getStatusCode() >= 500) {
          printBody(response);
        }
        return response;
      }

      request = new RequestBuilder("GET").setUrl(response.getHeader("Location")).build();
    }
  }

  private Request enrichRequest(final Request request) {
    final RequestBuilder requestBuilder = new RequestBuilder(request);

    requestBuilder.addHeader(
            "User-Agent",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36"
    );

    final String host = request.getUri().getHost();
    final Map<String, String> cookies = cookiesHolder.getCookies(host);
    cookies.entrySet().forEach(cookie ->
                    requestBuilder.addOrReplaceCookie(
                            new Cookie(cookie.getKey(), cookie.getValue(), false, null, null, Long.MAX_VALUE, false, false)
                    )
    );

    return requestBuilder.build();
  }

  private Response execute(final Request request) {
    System.out.println();
    printRequest(request);
    final long start = currentTimeMillis();

    final ListenableFuture<Response> responseFuture = asyncHttpClient.executeRequest(request);

    final Response response;
    final long durationMs;
    try {
      response = responseFuture.get();
      durationMs = currentTimeMillis() - start;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("failed to execute request " + request + ": got " + e, e);
    } catch (ExecutionException e) {
      throw new RuntimeException("failed to execute request " + request + ": got " + e, e);
    }

    System.out.println("Got response after: " + durationMs + " ms");
    printResponse(response);
    return response;
  }

  private static void printRequest(final Request request) {
    System.out.println(request.getMethod() + ' ' + request.getUrl());
    printHeaders(request.getHeaders());
    printCookies(request.getCookies());
  }

  private static void printCookies(final Collection<Cookie> cookies) {
    System.out.println("Cookies:");
    cookies.forEach(cookie -> System.out.println(cookie.getName() + ": " + cookie.getValue()));
  }

  private static void printResponse(final Response response) {
    System.out.println("Response:");
    System.out.println("Status: " + response.getStatusCode());
    printHeaders(response.getHeaders());
  }

  private static void printHeaders(final Map<String, List<String>> headers) {
    System.out.println("Headers:");
    headers.entrySet().forEach(header ->
                    header.getValue().forEach(value ->
                                    System.out.println(header.getKey() + ": " + value)
                    )
    );
  }

  private static void printBody(final Response response) {
    System.out.println("Body:");
    final String body;
    try {
      body = response.getResponseBody();
    } catch (IOException e) {
      throw new RuntimeException("failed to get body from response", e);
    }
    System.out.println(body);
  }

  private void setCookies(final String domain, final Collection<Cookie> cookies) {
    final Map<String, String> simpleCookies = toSimpleCookies(cookies);
    cookiesHolder.setCookies(domain, simpleCookies);
  }
}
