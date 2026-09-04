package io.github.anoder.powerpoint.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.TemplateNotFoundException;
import io.github.anoder.powerpoint.TestTemplates;
import io.github.anoder.powerpoint.Theme;
import io.github.anoder.powerpoint.dsl.SlideBuilder;

class ClasspathTemplateRepositoryTest {

	private final ClasspathTemplateRepository repository = new ClasspathTemplateRepository();

	@BeforeAll
	static void generateTemplates() {
		TestTemplates.generate();
	}

	@Test
	void resolves_the_template_of_a_theme_and_slide_type() {
		assertThat(repository.describe(Theme.CORPORATE, SlideType.TITLE))
			.isEqualTo("/powerpoint/corporate/title.pptx");
		assertThat(repository.describe(Theme.MODERN, SlideType.THREE_PARTS))
			.isEqualTo("/powerpoint/modern/three-parts.pptx");
		assertThat(repository.describe(Theme.MINIMAL, SlideType.IMAGE_TEXT))
			.isEqualTo("/powerpoint/minimal/image-text.pptx");
	}

	@Test
	void reads_every_built_in_template_of_every_theme() throws IOException {
		for (Theme theme : Theme.values()) {
			for (SlideType<?> slideType : SlideType.values()) {
				try (InputStream template = repository.get(theme, slideType)) {
					assertThat(template.readAllBytes()).as("%s/%s", theme, slideType).isNotEmpty();
				}
			}
		}
	}

	@Test
	void fails_with_the_resolved_path_when_the_template_is_missing() {
		SlideType<CustomSlideBuilder> pricing = SlideType.of("PRICING", "pricing");

		assertThatThrownBy(() -> repository.get(Theme.CORPORATE, pricing))
			.isInstanceOf(TemplateNotFoundException.class)
			.hasMessageContaining("Theme: CORPORATE")
			.hasMessageContaining("slide type: PRICING")
			.hasMessageContaining("/powerpoint/corporate/pricing.pptx");
	}

	@Test
	void normalizes_the_configured_location() {
		assertThat(new ClasspathTemplateRepository("classpath:/powerpoint/").location()).isEqualTo("/powerpoint");
		assertThat(new ClasspathTemplateRepository("powerpoint").location()).isEqualTo("/powerpoint");
		assertThat(new ClasspathTemplateRepository("classpath:decks/templates").describe(Theme.MODERN, SlideType.SECTION))
			.isEqualTo("/decks/templates/modern/section.pptx");
	}

	/** A slide type that has no template, to check the failure path. */
	private interface CustomSlideBuilder extends SlideBuilder<CustomSlideBuilder> {
	}
}
