package com.example.service;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
public class SessionOncePerRequestValidator extends OncePerRequestFilter {


  @Override
  protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                  @NonNull final HttpServletResponse response,
                                  @NonNull final FilterChain filterChain) throws ServletException,
                                                                                 IOException {

    try {
        filterChain.doFilter(request, response);
    } catch (IllegalStateException e) {
        log.error("Session handling failed {}: {} ",request.getServletPath(), request.getHeader("MyKey"));
    }
  }

}
