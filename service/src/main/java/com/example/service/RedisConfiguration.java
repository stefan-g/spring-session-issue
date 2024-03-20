package com.example.service;

import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(redisNamespace = "spring:my_session")
public class RedisConfiguration {

  @Value("${spring.redis.port:0}")
  private int port;

  @Value("${spring.redis.host:}")
  private String host;

  @Value("${spring.redis.password:}")
  private String password;

  @Bean(name = "redisTemplate")
  @ConditionalOnMissingBean
  public RedisTemplate<String, Object> getRedisTemplate(@NonNull final RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setKeySerializer(RedisSerializer.string());
    template.setHashKeySerializer(RedisSerializer.string());
    template.setConnectionFactory(connectionFactory);
    return template;
  }

  @Bean
  @Primary
  @ConditionalOnMissingBean
  public SessionRepository<?> getRedisSessionRepository(@NonNull final RedisTemplate<String, Object> redisTemplate) {
    return new RedisIndexedSessionRepository(redisTemplate);
  }

  @Bean(destroyMethod = "shutdown")
  public RedissonClient getRedissonClient() {
    Config config = new Config();
      config.useSingleServer()
            .setPassword(StringUtils.isEmpty(password) ? null : password)
            .setAddress(String.format( "redis://%s:%s", host, port));
    return Redisson.create(config);
  }

}