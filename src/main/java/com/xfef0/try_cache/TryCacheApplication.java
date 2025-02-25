package com.xfef0.try_cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TryCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(TryCacheApplication.class, args);
	}

}
