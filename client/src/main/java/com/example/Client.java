package com.example;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpStatus;

public class Client {

  private static final String url = "http://localhost:8080"; //change port after gateway is started

  public static void main(String[] args) throws IOException, InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(10);

    for (int t = 0; t < 20; t++) {
      executor.submit(() -> {
        System.out.println("Thread: " + Thread.currentThread().getId());

        final BasicCookieStore cookieStore = new BasicCookieStore();
        try (CloseableHttpClient client = HttpClientBuilder.create()
                                                           .setDefaultCookieStore(cookieStore)
                                                           .build()) {
          for (int i = 0; i < 1000; i++) {
            doLoginLogout(client);
          }
        } catch (IOException ex) {
          System.err.println(ex);
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(60, TimeUnit.SECONDS);
  }

  private static void doLoginLogout(CloseableHttpClient client) throws IOException {
    CloseableHttpResponse response = (CloseableHttpResponse) client
        .execute(new HttpPost(url + "/login"), response1 -> response1);

    if (response.getCode() != HttpStatus.SC_OK) {
      System.err.println("login failed");
    }


    response = (CloseableHttpResponse) client
        .execute(new HttpPost(url + "/logout"), response1 -> response1);

    if (response.getCode() != HttpStatus.SC_OK) {
      System.err.println("logout failed");
    }
  }

}