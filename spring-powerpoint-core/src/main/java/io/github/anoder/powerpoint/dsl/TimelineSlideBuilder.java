package io.github.anoder.powerpoint.dsl;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.anoder.powerpoint.SlideType;

/**
 * Slide laying out up to {@value #MILESTONE_COUNT} milestones on a horizontal timeline: the roadmap or
 * the history of the company.
 *
 * <p>Milestones left unset are replaced by an empty text.
 *
 * {@snippet :
 * presentation.add(SlideType.TIMELINE, slide -> slide
 *     .title("Roadmap")
 *     .milestones(
 *         milestone -> milestone.date("Q1 2026").text("Pilot with three customers"),
 *         milestone -> milestone.date("Q3 2026").text("General availability"),
 *         milestone -> milestone.date("Q1 2027").text("European launch")));
 * }
 */
public final class TimelineSlideBuilder extends AbstractSlideBuilder<TimelineSlideBuilder> {

	/** Number of milestones the template has room for. */
	public static final int MILESTONE_COUNT = 5;

	public TimelineSlideBuilder() {
		super(SlideType.TIMELINE);
	}

	public TimelineSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	/**
	 * Configures the milestones in order, from the first one.
	 */
	@SafeVarargs
	public final TimelineSlideBuilder milestones(Consumer<MilestoneBuilder>... milestones) {
		Objects.requireNonNull(milestones, "milestones");
		for (int index = 0; index < milestones.length; index++) {
			milestone(index, milestones[index]);
		}
		return this;
	}

	/**
	 * Configures a single milestone.
	 *
	 * @param index zero based index, from {@code 0} to {@value #MILESTONE_COUNT} minus one
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public TimelineSlideBuilder milestone(int index, Consumer<MilestoneBuilder> milestone) {
		Objects.checkIndex(index, MILESTONE_COUNT);
		Objects.requireNonNull(milestone, "milestone").accept(new SlideBlock(this, "milestone" + index));
		return this;
	}
}
