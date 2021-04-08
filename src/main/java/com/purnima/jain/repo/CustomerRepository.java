package com.purnima.jain.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.purnima.jain.domain.Customer;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CustomerRepository {

	private static final String CUSTOMER_KEY = "CUSTOMER_STORE";

	private final RedisTemplate<String, Customer> redisTemplate;

	@Autowired
	public CustomerRepository(RedisTemplate<String, Customer> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}

	public Customer findById(String customerId) {
		log.info("Retrieving Customer Id :: {} from Redis.", customerId);
		return (Customer) redisTemplate.opsForHash().get(CUSTOMER_KEY + ":" + customerId, customerId);
	}

	public Customer save(Customer customer) {
		log.info("Saving Customer to Redis, Customer :: {}", customer);
		redisTemplate.opsForHash().putIfAbsent(CUSTOMER_KEY + ":" + customer.getCustomerId(), customer.getCustomerId(), customer);
		// redisTemplate.expire(CUSTOMER_KEY + ":" + customer.getCustomerId(), 20000, TimeUnit.MILLISECONDS);
		return customer;
	}

	public List<Customer> findAll() {
		log.info("Retrieving all Customers from Redis...........");
		List<Customer> customerList = new ArrayList<>();

		Set<String> keys = redisTemplate.keys(CUSTOMER_KEY + ":*");  // Avoid this in production, more so in clustered environment
		for (String key : keys) {
			Set<Object> hashKeySet = redisTemplate.opsForHash().keys(key);
			for (Object hashKey : hashKeySet) {
				Customer customer = (Customer) redisTemplate.opsForHash().get(key, hashKey);
				customerList.add(customer);
			}
		}

		return customerList;
	}

	public Customer update(Customer customer) {
		log.info("Updating Customer in Redis, Customer :: {}", customer);
		redisTemplate.opsForHash().put(CUSTOMER_KEY + ":" + customer.getCustomerId(), customer.getCustomerId(), customer);
		return customer;
	}

	public String deleteById(String customerId) {
		log.info("Deleting Customer Id :: {} from Redis.", customerId);
		redisTemplate.opsForHash().delete(CUSTOMER_KEY + ":" + customerId, customerId);
		return "Customer Deleted";
	}

}
