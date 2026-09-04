package io.github.anoder.powerpoint.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import io.github.anoder.powerpoint.PowerPointPresentation;
import io.github.anoder.powerpoint.PowerPointRenderingException;
import io.github.anoder.powerpoint.PresentationBuilder;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.Theme;
import io.github.anoder.powerpoint.dsl.SlideBuilder;
import io.github.anoder.powerpoint.dsl.SlideBuilderRegistry;
import io.github.anoder.powerpoint.model.SlideModel;
import io.github.anoder.powerpoint.render.PowerPointRenderer;

/**
 * Default {@link PresentationBuilder}: it collects immutable slide models and renders them on
 * {@link #build()}.
 */
public final class DefaultPresentationBuilder implements PresentationBuilder {

	private final Theme theme;
	private final SlideBuilderRegistry builders;
	private final PowerPointRenderer renderer;
	private final List<SlideModel> slides = new ArrayList<>();

	public DefaultPresentationBuilder(Theme theme, SlideBuilderRegistry builders, PowerPointRenderer renderer) {
		this.theme = Objects.requireNonNull(theme, "theme");
		this.builders = Objects.requireNonNull(builders, "builders");
		this.renderer = Objects.requireNonNull(renderer, "renderer");
	}

	@Override
	public <B extends SlideBuilder<B>> PresentationBuilder add(SlideType<B> type, Consumer<B> configuration) {
		Objects.requireNonNull(configuration, "configuration");
		B builder = builders.create(type);
		configuration.accept(builder);
		slides.add(builder.toModel());
		return this;
	}

	@Override
	public PowerPointPresentation build() {
		ByteArrayOutputStream presentation = new ByteArrayOutputStream();
		try {
			renderer.render(theme, List.copyOf(slides), presentation);
		} catch (IOException e) {
			throw new PowerPointRenderingException("Cannot generate the presentation. Theme: " + theme, e);
		}
		return new PowerPointPresentation(presentation.toByteArray());
	}

	/**
	 * @return the slides configured so far, for tests and diagnostics
	 */
	public List<SlideModel> slides() {
		return List.copyOf(slides);
	}
}
