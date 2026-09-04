package io.github.anoder.powerpoint.dsl;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.model.SlideModel;

/**
 * A strongly typed, fluent configuration of a single slide.
 *
 * <p>The type parameter is the concrete builder type, so that the fluent methods of the
 * implementations can return themselves and stay chainable.
 *
 * @param <S> the concrete builder type
 */
public interface SlideBuilder<S extends SlideBuilder<S>> {

	/**
	 * @return the slide type this builder configures
	 */
	SlideType<S> type();

	/**
	 * @return the immutable model to render, with the template variable names of the slide
	 */
	SlideModel toModel();
}
