package com.example.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MyController {

  private final HttpServletRequest httpServletRequest;
  private final HttpServletResponse httpServletResponse;
  private final RedissonClient redissonClient;


  public MyController(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final RedissonClient redissonClient) {
    this.httpServletRequest = httpServletRequest;
    this.httpServletResponse = httpServletResponse;
    this.redissonClient = redissonClient;
  }


  @PostMapping("/login")
  public ResponseEntity<String> login()  {
    HttpSession session = httpServletRequest.getSession(true);
    String key = httpServletRequest.getHeader("MyKey");
    if(StringUtils.isEmpty(key)){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"key is empty");
    }
    session.setAttribute("Key",key);


    return ResponseEntity.ok("");
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout()  {
    HttpSession session = httpServletRequest.getSession(false);
    if(session == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid session");
    }

    String key = (String) session.getAttribute("Key");
//    httpServletResponse.addHeader("MyKey",key);

    session.invalidate();

    HttpHeaders headers = new HttpHeaders();
    headers.add("MyKey", key);
    return new ResponseEntity(headers, HttpStatus.OK);
  }



}