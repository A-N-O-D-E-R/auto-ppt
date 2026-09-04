package io.github.anoder.powerpoint;

import java.util.function.Consumer;

import io.github.anoder.powerpoint.dsl.SlideBuilder;

/**
 * Fluent builder collecting the slides of a presentation.
 *
 * <p>Instances are not thread safe and are meant to be used once, from a single method.
 */
public interface PresentationBuilder {

	/**
	 * Appends a slide of the given type; the configuration lambda receives the builder bound to that
	 * type, so only the methods that make sense for it are available:
	 *
	 * {@snippet :
	 * builder.add(SlideType.THREE_PARTS, slide -> slide
	 *     .title("Our priorities")
	 *     .parts(
	 *         part -> part.title("Growth").text("Expand into new markets"),
	 *         part -> part.title("Efficiency").text("Reduce operational costs"),
	 *         part -> part.title("People").text("Invest in our teams")));
	 * }
	 *
	 * @param type          the slide type, which selects both the template and the builder
	 * @param configuration fills in the slide content
	 * @throws io.github.anoder.powerpoint.UnsupportedSlideTypeException if no builder is registered for {@code type}
	 */
	<B extends SlideBuilder<B>> PresentationBuilder add(SlideType<B> type, Consumer<B> configuration);

	/**
	 * Renders every slide and assembles the final presentation.
	 *
	 * @throws io.github.anoder.powerpoint.PowerPointException if a template is missing, a slide is invalid
	 *                                                    or the rendering fails
	 */
	PowerPointPresentation build();
}
