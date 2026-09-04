package io.github.anoder.powerpoint.dsl;

import io.github.anoder.powerpoint.SlideType;

/**
 * Opening slide of a presentation.
 *
 * {@snippet :
 * presentation.add(SlideType.TITLE, slide -> slide
 *     .title("Q3 Business Review")
 *     .subtitle("September 2026"));
 * }
 */
public final class TitleSlideBuilder extends AbstractSlideBuilder<TitleSlideBuilder> {

	public TitleSlideBuilder() {
		super(SlideType.TITLE);
	}

	public TitleSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	public TitleSlideBuilder subtitle(String value) {
		put("subtitle", value);
		return this;
	}
}
