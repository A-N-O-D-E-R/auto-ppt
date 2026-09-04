package io.github.anoder.powerpoint.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.anoder.powerpoint.PowerPoint;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.Theme;
import io.github.anoder.powerpoint.dsl.DefaultSlideBuilderFactory;
import io.github.anoder.powerpoint.dsl.SlideBuilderFactory;
import io.github.anoder.powerpoint.dsl.SlideBuilderRegistry;
import io.github.anoder.powerpoint.dsl.TitleSlideBuilder;
import io.github.anoder.powerpoint.internal.DefaultPowerPoint;
import io.github.anoder.powerpoint.model.SlideModel;
import io.github.anoder.powerpoint.render.CoreozPowerPointRenderer;
import io.github.anoder.powerpoint.render.PoiPresentationAssembler;
import io.github.anoder.powerpoint.render.PowerPointRenderer;
import io.github.anoder.powerpoint.render.PresentationAssembler;
import io.github.anoder.powerpoint.template.ClasspathTemplateRepository;
import io.github.anoder.powerpoint.template.TemplateRepository;

class PowerPointAutoConfigurationTest {

	private final ApplicationContextRunner runner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(PowerPointAutoConfiguration.class));

	@Test
	void configures_the_whole_pipeline_with_its_defaults() {
		runner.run(context -> {
			assertThat(context).hasSingleBean(PowerPoint.class)
				.hasSingleBean(PowerPointRenderer.class)
				.hasSingleBean(PresentationAssembler.class)
				.hasSingleBean(TemplateRepository.class)
				.hasSingleBean(SlideBuilderRegistry.class);
			assertThat(context.getBean(PowerPointRenderer.class)).isInstanceOf(CoreozPowerPointRenderer.class);
			assertThat(context.getBean(PresentationAssembler.class)).isInstanceOf(PoiPresentationAssembler.class);
			assertThat(context.getBean(ClasspathTemplateRepository.class).location()).isEqualTo("/powerpoint");
			assertThat(context.getBean(DefaultPowerPoint.class).defaultTheme()).isEqualTo(Theme.CORPORATE);
			assertThat(context.getBean(SlideBuilderRegistry.class).types())
				.containsExactlyInAnyOrderElementsOf(SlideType.values());
		});
	}

	@Test
	void binds_the_configuration_properties() {
		runner
			.withPropertyValues(
				"powerpoint.default-theme=modern",
				"powerpoint.templates.location=classpath:/decks"
			)
			.run(context -> {
				assertThat(context.getBean(DefaultPowerPoint.class).defaultTheme()).isEqualTo(Theme.MODERN);
				assertThat(context.getBean(ClasspathTemplateRepository.class).location()).isEqualTo("/decks");
			});
	}

	@Test
	void an_application_bean_replaces_the_default_one() {
		runner.withUserConfiguration(CustomPipeline.class).run(context -> {
			assertThat(context).hasSingleBean(TemplateRepository.class).hasSingleBean(PowerPointRenderer.class);
			assertThat(context.getBean(TemplateRepository.class)).isSameAs(CustomPipeline.TEMPLATES);
			assertThat(context.getBean(PowerPointRenderer.class)).isSameAs(CustomPipeline.RENDERER);
		});
	}

	@Test
	void the_facade_hands_the_slide_models_of_the_dsl_to_the_renderer() {
		runner.withUserConfiguration(CustomPipeline.class).run(context -> {
			reset(CustomPipeline.RENDERER);

			context.getBean(PowerPoint.class)
				.presentation(Theme.MODERN)
				.add(SlideType.THREE_PARTS, slide -> slide
					.title("Key achievements")
					.parts(
						part -> part.title("Revenue").text("+24%"),
						part -> part.title("Customers").text("+18%"),
						part -> part.title("Margin").text("+4 pts")
					))
				.build();

			@SuppressWarnings("unchecked")
			ArgumentCaptor<List<SlideModel>> models = ArgumentCaptor.forClass(List.class);
			verify(CustomPipeline.RENDERER).render(eq(Theme.MODERN), models.capture(), any());
			assertThat(models.getValue()).singleElement().satisfies(slide -> {
				assertThat(slide.type()).isEqualTo(SlideType.THREE_PARTS);
				assertThat(slide.values()).containsExactly(
					entry("title", "Key achievements"),
					entry("part0.title", "Revenue"),
					entry("part0.text", "+24%"),
					entry("part1.title", "Customers"),
					entry("part1.text", "+18%"),
					entry("part2.title", "Margin"),
					entry("part2.text", "+4 pts")
				);
			});
		});
	}

	@Test
	void an_application_factory_overrides_the_built_in_slide_builder() {
		runner.withUserConfiguration(CustomTitleSlide.class).run(context -> {
			SlideBuilderRegistry registry = context.getBean(SlideBuilderRegistry.class);

			assertThat(registry.types()).containsExactlyInAnyOrderElementsOf(SlideType.values());
			assertThat(registry.create(SlideType.TITLE).toModel().values())
				.containsEntry("subtitle", "ACME corporation");
		});
	}

	@Configuration(proxyBeanMethods = false)
	static class CustomPipeline {

		static final TemplateRepository TEMPLATES = mock(TemplateRepository.class);
		static final PowerPointRenderer RENDERER = mock(PowerPointRenderer.class);

		@Bean
		TemplateRepository templateRepository() {
			return TEMPLATES;
		}

		@Bean
		PowerPointRenderer powerPointRenderer() {
			return RENDERER;
		}
	}

	@Configuration(proxyBeanMethods = false)
	static class CustomTitleSlide {

		@Bean
		SlideBuilderFactory<TitleSlideBuilder> titleSlideBuilderFactory() {
			return new DefaultSlideBuilderFactory<>(
				SlideType.TITLE,
				() -> new TitleSlideBuilder().subtitle("ACME corporation")
			);
		}
	}
}
