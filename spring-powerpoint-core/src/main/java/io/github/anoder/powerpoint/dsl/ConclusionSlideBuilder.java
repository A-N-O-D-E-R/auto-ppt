package io.github.anoder.powerpoint.dsl;

import io.github.anoder.powerpoint.SlideType;

/**
 * Closing slide of a presentation.
 */
public final class ConclusionSlideBuilder extends AbstractSlideBuilder<ConclusionSlideBuilder> {

	public ConclusionSlideBuilder() {
		super(SlideType.CONCLUSION);
	}

	public ConclusionSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	public ConclusionSlideBuilder subtitle(String value) {
		put("subtitle", value);
		return this;
	}
}
