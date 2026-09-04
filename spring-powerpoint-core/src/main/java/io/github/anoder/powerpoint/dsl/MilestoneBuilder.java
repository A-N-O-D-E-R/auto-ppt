package io.github.anoder.powerpoint.dsl;

/**
 * One of the steps of a {@link TimelineSlideBuilder timeline} slide.
 */
public interface MilestoneBuilder {

	/** When it happens, e.g. {@code Q3 2026}; free text, so any calendar works. */
	MilestoneBuilder date(String value);

	/** What happens then. */
	MilestoneBuilder text(String value);
}
