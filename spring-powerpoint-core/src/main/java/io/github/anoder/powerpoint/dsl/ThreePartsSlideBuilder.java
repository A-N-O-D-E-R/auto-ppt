package io.github.anoder.powerpoint.dsl;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.anoder.powerpoint.SlideType;

/**
 * Slide presenting three parts side by side.
 *
 * {@snippet :
 * presentation.add(SlideType.THREE_PARTS, slide -> slide
 *     .title("Our priorities")
 *     .parts(
 *         part -> part.title("Growth").text("Expand into new markets"),
 *         part -> part.title("Efficiency").text("Reduce operational costs"),
 *         part -> part.title("People").text("Invest in our teams")));
 * }
 */
public final class ThreePartsSlideBuilder extends AbstractSlideBuilder<ThreePartsSlideBuilder> {

	/** Number of parts of this slide type. */
	public static final int PART_COUNT = 3;

	public ThreePartsSlideBuilder() {
		super(SlideType.THREE_PARTS);
	}

	public ThreePartsSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	/**
	 * Configures the three parts at once; this is the primary API because it makes the three part
	 * structure of the slide explicit.
	 */
	public ThreePartsSlideBuilder parts(
			Consumer<PartBuilder> part1,
			Consumer<PartBuilder> part2,
			Consumer<PartBuilder> part3) {
		return part(0, part1).part(1, part2).part(2, part3);
	}

	/**
	 * Configures a single part, which is handy when the parts come from a loop or from optional data.
	 *
	 * @param index zero based part index, from {@code 0} to {@code 2}
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public ThreePartsSlideBuilder part(int index, Consumer<PartBuilder> part) {
		Objects.checkIndex(index, PART_COUNT);
		Objects.requireNonNull(part, "part").accept(new SlideBlock(this, "part" + index));
		return this;
	}
}
