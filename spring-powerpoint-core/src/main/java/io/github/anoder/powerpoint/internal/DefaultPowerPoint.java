package io.github.anoder.powerpoint.internal;

import java.util.Objects;

import io.github.anoder.powerpoint.PowerPoint;
import io.github.anoder.powerpoint.PresentationBuilder;
import io.github.anoder.powerpoint.Theme;
import io.github.anoder.powerpoint.dsl.SlideBuilderRegistry;
import io.github.anoder.powerpoint.render.PowerPointRenderer;

/**
 * Default {@link PowerPoint} implementation: a thread safe factory of {@link PresentationBuilder}.
 */
public final class DefaultPowerPoint implements PowerPoint {

	private final SlideBuilderRegistry builders;
	private final PowerPointRenderer renderer;
	private final Theme defaultTheme;

	public DefaultPowerPoint(SlideBuilderRegistry builders, PowerPointRenderer renderer, Theme defaultTheme) {
		this.builders = Objects.requireNonNull(builders, "builders");
		this.renderer = Objects.requireNonNull(renderer, "renderer");
		this.defaultTheme = Objects.requireNonNull(defaultTheme, "defaultTheme");
	}

	@Override
	public PresentationBuilder presentation() {
		return presentation(defaultTheme);
	}

	@Override
	public PresentationBuilder presentation(Theme theme) {
		return new DefaultPresentationBuilder(theme, builders, renderer);
	}

	/**
	 * @return the theme used by {@link #presentation()}
	 */
	public Theme defaultTheme() {
		return defaultTheme;
	}
}
