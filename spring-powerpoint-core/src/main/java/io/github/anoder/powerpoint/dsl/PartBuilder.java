package io.github.anoder.powerpoint.dsl;

/**
 * One of the parts of a {@link ThreePartsSlideBuilder three parts} slide.
 */
public interface PartBuilder {

	PartBuilder title(String value);

	PartBuilder text(String value);
}
