package ru.hh.loginchecker;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;

import java.util.HashMap;
import java.util.Map;

public final class LoginChecker {

  public static void main(final String[] args) {
    final Arguments arguments = Arguments.fromCmdArgs(args);

    final AsyncHttpClientConfig asyncHttpClientConfig = new AsyncHttpClientConfig.Builder().build();
    final NettyAsyncHttpProvider nettyAsyncHttpProvider = new NettyAsyncHttpProvider(asyncHttpClientConfig);
    try (final AsyncHttpClient asyncHttpClient = new AsyncHttpClient(nettyAsyncHttpProvider, asyncHttpClientConfig)) {
      checkLogin(arguments.username, arguments.password, arguments.numOfAttempts, asyncHttpClient);
    }
  }

  private static void checkLogin(final String username, final String password, final int numOfAttempts, final AsyncHttpClient asyncHttpClient) {
    final LoginPerformer loginPerformer = new LoginPerformer(asyncHttpClient);
    final Map<Integer, Integer> statusToCount = new HashMap<>();
    for (int i = 1; i <= numOfAttempts; i++) {

      final Response response = loginPerformer.login(username, password);

      System.out.println();
      System.out.println("Finished attempt #" + i);

      rememberAttemptResult(response.getStatusCode(), statusToCount);
    }
    printAttemptsResults(statusToCount);
  }

  private static void rememberAttemptResult(final int status, final Map<Integer, Integer> statusToCount) {
    Integer count = statusToCount.get(status);
    if (count == null) {
      count = 0;
    }
    count++;
    statusToCount.put(status, count);
  }

  private static void printAttemptsResults(final Map<Integer, Integer> statusToCount) {
    System.out.println();
    System.out.println("Results:");
    statusToCount.entrySet().forEach(statusAndCount ->
        System.out.println(statusAndCount.getKey() + ": " + statusAndCount.getValue())
    );
  }

  private LoginChecker() {
  }
}
