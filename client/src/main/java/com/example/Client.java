package com.example;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;

@Log4j2
public class Client {

  private static final String url = "http://localhost:8080"; //change port after gateway is started
//  private static final String url = "http://localhost:50900";
  private static Map<String, String> map = new ConcurrentHashMap<>();

  public static void main(String[] args) throws IOException, InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(10);

    for (int t = 0; t < 40; t++) {
      final int finalT = t;
      executor.submit(() -> {
        log.info("Thread: " + Thread.currentThread().getId());

        try (CloseableHttpClient client = HttpClientBuilder.create()
                                                           .setDefaultCookieStore(new BasicCookieStore())
                                                           .build()) {
          for (int i = 0; i < 1000; i++) {
            doLoginLogout(client, finalT + "-" + i);
          }
        } catch (Throwable ex) {
          log.error("", ex);
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(60, TimeUnit.SECONDS);
  }

  private static void doLoginLogout(CloseableHttpClient client, String id) throws IOException {
    Header setCookie;
    HttpPost post = new HttpPost(url + "/login");
    post.setHeader("MyKey", id);
    try(CloseableHttpResponse loginResponse = (CloseableHttpResponse) client
        .execute(post, response1 -> response1)) {

      if (loginResponse.getCode() != HttpStatus.SC_OK) {
        log.error("login failed: " + loginResponse.getCode());
      }

      setCookie = loginResponse.getFirstHeader(HttpHeaders.SET_COOKIE);
      if (setCookie == null) {
        log.error("Set-Cookie not set");
      } else {
        String value = setCookie.getValue();
        if (value == null || value.isBlank()) {
          log.error("Set-Cookie blank");
        }
        if (map.containsKey(setCookie.getValue())) {
          log.error("Set-Cookie already exists");
        }
        map.put(setCookie.getValue(), "");
      }
    }



    try(CloseableHttpResponse logoutResponse = (CloseableHttpResponse) client
        .execute(new HttpPost(url + "/logout"), response1 -> response1)) {

      if (logoutResponse.getCode() != HttpStatus.SC_OK) {
        log.error("logout failed: " + logoutResponse.getCode());
      } else {
        log.info("Id: " + id + " cookie: " + Objects.requireNonNull(setCookie).getValue());
      }

      Header reference = logoutResponse.getFirstHeader("MyKey");
      if (reference == null || !id.equals(reference.getValue())) {
        log.error("Invalid Id");
      }
    }

  }

}