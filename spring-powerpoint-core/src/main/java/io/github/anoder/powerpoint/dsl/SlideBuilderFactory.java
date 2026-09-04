package io.github.anoder.powerpoint.dsl;

import io.github.anoder.powerpoint.SlideType;

/**
 * Creates the builder of one slide type. Register a factory as a Spring bean to add a custom slide
 * type, or to replace a built-in one.
 *
 * @param <B> the builder type created by this factory
 */
public interface SlideBuilderFactory<B extends SlideBuilder<B>> {

	/**
	 * @return the slide type this factory builds
	 */
	SlideType<B> type();

	/**
	 * @return a new, empty builder
	 */
	B create();
}
