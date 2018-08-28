package com.prem.springcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringProxyApplication.class, args);
	}
}
