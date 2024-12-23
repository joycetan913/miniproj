package miniproj.vttp_batchb_miniproj;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AppConfig {

  @Value("${movie.db.host}")
  private String dbHost;

  @Value("${movie.db.port}")
  private Integer dbPort;

  @Value("${movie.db.database}")
  private Integer dbNumber;

  @Value("${movie.db.username}")
  private String dbUsername;

  @Value("${movie.db.password}")
  private String dbPassword;

  @Bean("redis-obj")
  public RedisTemplate<String, Object> createRedisTemplate() {
   

    final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(dbHost, dbPort);
    config.setDatabase(dbNumber);
    if (!dbUsername.trim().equals("")) {
      config.setUsername(dbUsername);
      config.setPassword(dbPassword);
    }
    final JedisClientConfiguration jedisClient = JedisClientConfiguration
        .builder().build();

    final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
    jedisFac.afterPropertiesSet();

    final RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisFac);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());

    return template;
  }
}