package io.github.anoder.powerpoint.dsl;

import io.github.anoder.powerpoint.SlideType;

/**
 * Slide quoting someone: a customer, an analyst, a partner.
 *
 * {@snippet :
 * presentation.add(SlideType.QUOTE, slide -> slide
 *     .quote("It cut our sequencing costs in half.")
 *     .author("Dana Okonkwo")
 *     .role("Head of Research, Northwind Bio"));
 * }
 */
public final class QuoteSlideBuilder extends AbstractSlideBuilder<QuoteSlideBuilder> {

	public QuoteSlideBuilder() {
		super(SlideType.QUOTE);
	}

	public QuoteSlideBuilder quote(String value) {
		put("quote", value);
		return this;
	}

	public QuoteSlideBuilder author(String value) {
		put("author", value);
		return this;
	}

	/**
	 * The role of the author, e.g. {@code Head of Research, Northwind Bio}.
	 */
	public QuoteSlideBuilder role(String value) {
		put("role", value);
		return this;
	}
}
