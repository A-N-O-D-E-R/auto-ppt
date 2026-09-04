package io.github.anoder.powerpoint;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.anoder.powerpoint.dsl.SlideBuilderRegistry;
import io.github.anoder.powerpoint.internal.DefaultPowerPoint;
import io.github.anoder.powerpoint.render.CoreozPowerPointRenderer;
import io.github.anoder.powerpoint.render.PoiPresentationAssembler;
import io.github.anoder.powerpoint.template.ClasspathTemplateRepository;

/**
 * Uses the library the way an application does, without Spring.
 */
class PowerPointEndToEndTest {

	private final PowerPoint powerpoint = new DefaultPowerPoint(
		SlideBuilderRegistry.withBuiltIns(),
		new CoreozPowerPointRenderer(new ClasspathTemplateRepository(), new PoiPresentationAssembler()),
		Theme.CORPORATE
	);

	@BeforeAll
	static void generateTemplates() {
		TestTemplates.generate();
	}

	@Test
	void generates_a_business_review() {
		PowerPointPresentation presentation = powerpoint
			.presentation(Theme.CORPORATE)
			.add(SlideType.TITLE, slide -> slide
				.title("Q3 Business Review")
				.subtitle("September 2026"))
			.add(SlideType.SECTION, slide -> slide
				.title("Performance")
				.subtitle("Q3 2026"))
			.add(SlideType.THREE_PARTS, slide -> slide
				.title("Key achievements")
				.parts(
					part -> part.title("Revenue").text("+24%"),
					part -> part.title("Customers").text("+18%"),
					part -> part.title("Margin").text("+4 pts")
				))
			.add(SlideType.CONCLUSION, slide -> slide
				.title("Thank you"))
			.build();

		byte[] content = presentation.toByteArray();
		assertThat(TestTemplates.slideCount(content)).isEqualTo(4);
		assertThat(TestTemplates.textOf(content))
			.contains("Q3 Business Review", "Performance", "Key achievements", "+24%", "Thank you")
			.noneMatch(text -> text.contains("$/"));
	}

	@Test
	void uses_the_default_theme_when_none_is_given() {
		PowerPointPresentation presentation = powerpoint
			.presentation()
			.add(SlideType.TITLE, slide -> slide.title("Hello").subtitle("World"))
			.build();

		assertThat(TestTemplates.textOf(presentation.toByteArray())).contains("Hello", "World");
	}

	@Test
	void writes_the_presentation_to_a_file(@TempDir Path directory) throws Exception {
		Path file = directory.resolve("presentation.pptx");

		powerpoint.presentation(Theme.MINIMAL)
			.add(SlideType.TITLE, slide -> slide.title("Hello").subtitle("World"))
			.build()
			.write(file);

		assertThat(Files.size(file)).isPositive();
		assertThat(TestTemplates.slideCount(Files.readAllBytes(file))).isEqualTo(1);
	}
}
