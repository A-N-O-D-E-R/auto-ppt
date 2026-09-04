package io.github.anoder.powerpoint.dsl;

import io.github.anoder.powerpoint.SlideType;

/**
 * Slide carrying a single strong sentence, the way a pitch deck states the problem it attacks.
 *
 * {@snippet :
 * presentation.add(SlideType.STATEMENT, slide -> slide
 *     .statement("Biology is programmable")
 *     .attribution("Our founding bet, 2008"));
 * }
 */
public final class StatementSlideBuilder extends AbstractSlideBuilder<StatementSlideBuilder> {

	public StatementSlideBuilder() {
		super(SlideType.STATEMENT);
	}

	public StatementSlideBuilder statement(String value) {
		put("statement", value);
		return this;
	}

	/**
	 * The line under the statement: a source, a date, a caveat.
	 */
	public StatementSlideBuilder attribution(String value) {
		put("attribution", value);
		return this;
	}
}
