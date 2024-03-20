package com.example.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MyController {

  private final HttpServletRequest httpServletRequest;

  public MyController(final HttpServletRequest httpServletRequest) {
    this.httpServletRequest = httpServletRequest;
  }


  @PostMapping("/login")
  public ResponseEntity<String> login()  {
    HttpSession session = httpServletRequest.getSession(true);
    session.setAttribute("Key","Hello");

    return ResponseEntity.ok("");
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout()  {
    HttpSession session = httpServletRequest.getSession(false);
    if(session == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid session");
    }

    session.invalidate();

    return ResponseEntity.ok("");
  }



}