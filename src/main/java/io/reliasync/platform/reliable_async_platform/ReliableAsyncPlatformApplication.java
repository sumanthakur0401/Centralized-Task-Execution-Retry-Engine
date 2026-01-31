package io.reliasync.platform.reliable_async_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ReliableAsyncPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReliableAsyncPlatformApplication.class, args);
	}

}
