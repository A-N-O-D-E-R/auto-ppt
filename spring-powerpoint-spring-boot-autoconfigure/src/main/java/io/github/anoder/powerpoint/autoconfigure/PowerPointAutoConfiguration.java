package io.github.anoder.powerpoint.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.github.anoder.powerpoint.PowerPoint;
import io.github.anoder.powerpoint.dsl.SlideBuilderFactory;
import io.github.anoder.powerpoint.dsl.SlideBuilderRegistry;
import io.github.anoder.powerpoint.internal.DefaultPowerPoint;
import io.github.anoder.powerpoint.render.CoreozPowerPointRenderer;
import io.github.anoder.powerpoint.render.PoiPresentationAssembler;
import io.github.anoder.powerpoint.render.PowerPointRenderer;
import io.github.anoder.powerpoint.render.PresentationAssembler;
import io.github.anoder.powerpoint.template.ClasspathTemplateRepository;
import io.github.anoder.powerpoint.template.TemplateRepository;

/**
 * Auto-configures the PowerPoint generation.
 *
 * <p>Every bean is conditional on the absence of an application-provided one, so any part of the pipeline
 * can be replaced: declare your own {@link TemplateRepository} to read the templates from a database or
 * from the file system, your own {@link PowerPointRenderer} to use another template engine, or your own
 * {@link PresentationAssembler} to merge the slides differently.
 *
 * <p>{@link SlideBuilderFactory} beans are added to the six built-in ones; a factory bean declared for a
 * built-in slide type replaces it.
 */
@AutoConfiguration
@EnableConfigurationProperties(PowerPointProperties.class)
public class PowerPointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public TemplateRepository powerPointTemplateRepository(PowerPointProperties properties) {
		return new ClasspathTemplateRepository(properties.getTemplates().getLocation());
	}

	@Bean
	@ConditionalOnMissingBean
	public PresentationAssembler powerPointPresentationAssembler() {
		return new PoiPresentationAssembler();
	}

	@Bean
	@ConditionalOnMissingBean
	public PowerPointRenderer powerPointRenderer(TemplateRepository templates, PresentationAssembler assembler) {
		return new CoreozPowerPointRenderer(templates, assembler);
	}

	@Bean
	@ConditionalOnMissingBean
	public SlideBuilderRegistry slideBuilderRegistry(ObjectProvider<SlideBuilderFactory<?>> factories) {
		List<SlideBuilderFactory<?>> all = new ArrayList<>(SlideBuilderRegistry.builtInFactories());
		factories.orderedStream().forEach(all::add);
		return new SlideBuilderRegistry(all);
	}

	@Bean
	@ConditionalOnMissingBean
	public PowerPoint powerPoint(SlideBuilderRegistry builders, PowerPointRenderer renderer,
			PowerPointProperties properties) {
		return new DefaultPowerPoint(builders, renderer, properties.getDefaultTheme());
	}
}
