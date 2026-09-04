package io.github.anoder.powerpoint.dsl;

import io.github.anoder.powerpoint.SlideType;

/**
 * Divider slide introducing a new section of a presentation.
 */
public final class SectionSlideBuilder extends AbstractSlideBuilder<SectionSlideBuilder> {

	public SectionSlideBuilder() {
		super(SlideType.SECTION);
	}

	public SectionSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	public SectionSlideBuilder subtitle(String value) {
		put("subtitle", value);
		return this;
	}
}
