package io.github.anoder.demo;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Minimal application: the starter is on the classpath, so {@code PowerPoint} can be injected anywhere.
 *
 * <p>Running it writes {@code target/q3-review.pptx}:
 * {@code mvn -pl example spring-boot:run}
 */
@SpringBootApplication
public class ExampleApplication {

	private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);

	public static void main(String... args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

	@Bean
	ApplicationRunner writeBusinessReview(BusinessReviewService businessReview) {
		return (ApplicationArguments arguments) -> {
			Path file = Path.of(arguments.getSourceArgs().length > 0
				? arguments.getSourceArgs()[0]
				: "target/q3-review.pptx");
			Files.createDirectories(file.toAbsolutePath().getParent());
			Files.write(file, businessReview.generate());
			logger.info("Presentation written to {}", file.toAbsolutePath());
		};
	}
}
