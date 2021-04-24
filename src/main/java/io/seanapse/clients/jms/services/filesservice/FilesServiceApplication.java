package io.seanapse.clients.jms.services.filesservice;

import io.seanapse.clients.jms.services.filesservice.infrastructure.properties.S3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(S3Properties.class)
public class FilesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilesServiceApplication.class, args);
	}

}
