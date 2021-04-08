package com.purnima.jain.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.purnima.jain.domain.Customer;

@Configuration
@EnableRedisRepositories
@PropertySource("classpath:redis.properties")
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String hostName;

	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.lettuce.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.redis.lettuce.pool.min-idle}")
	private int minIdle;

	@Value("${spring.redis.lettuce.pool.max-wait}")
	private int maxWaitMillis;

	@Value("${spring.redis.lettucepool.max-total}")
	private int maxTotal;

	@Bean
	public LettucePoolingClientConfiguration lettucePoolClientConfig() {

		@SuppressWarnings("rawtypes")
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		poolConfig.setMaxTotal(maxTotal);

		return LettucePoolingClientConfiguration.builder()
												.commandTimeout(Duration.ofSeconds(10))
												.shutdownTimeout(Duration.ZERO)
												.poolConfig(poolConfig)
												.build();
	}

	@Bean
	public RedisStandaloneConfiguration redisStandaloneConfig() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(hostName);
		redisStandaloneConfiguration.setPort(port);

		return redisStandaloneConfiguration;
	}

	@Bean
	public LettuceConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfig, LettucePoolingClientConfiguration lettucePoolClientConfig) {
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfig, lettucePoolClientConfig);
		lettuceConnectionFactory.setShareNativeConnection(false);

		return lettuceConnectionFactory;
	}
	
	@SuppressWarnings("deprecation")
	@Bean
	public RedisTemplate<String, Customer> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		
		RedisTemplate<String, Customer> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		// Initializing KeySerializer
		RedisSerializer<String> redisKeySerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
		
		// Initializing ValueSerializer
		Jackson2JsonRedisSerializer<Customer> redisValueSerializer = new Jackson2JsonRedisSerializer<>(Customer.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		objectMapper.registerModule(new JavaTimeModule())
					.registerModule(new Jdk8Module())
					.registerModule(new ParameterNamesModule());
		objectMapper.findAndRegisterModules();
		redisValueSerializer.setObjectMapper(objectMapper);
		
		// Setting Key & Value Serializers
		redisTemplate.setKeySerializer(redisKeySerializer);
		redisTemplate.setValueSerializer(redisValueSerializer);
		redisTemplate.setHashKeySerializer(redisKeySerializer);
		redisTemplate.setHashValueSerializer(redisValueSerializer);
		
		redisTemplate.setEnableTransactionSupport(true);
		
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
//	@Bean
//    public RedisTemplate<String, Customer> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Customer> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());	
//        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
//        redisTemplate.setEnableTransactionSupport(true);
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }

	
//	@Bean // StringRedisTemplate is short-hand for RedisTemplate<String, String>
//    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        StringRedisTemplate redisTemplate = new StringRedisTemplate();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }
}
