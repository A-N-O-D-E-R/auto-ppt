package io.github.anoder.powerpoint.dsl;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.anoder.powerpoint.SlideType;

/**
 * Slide comparing two columns.
 *
 * {@snippet :
 * presentation.add(SlideType.TWO_COLUMNS, slide -> slide
 *     .title("Before vs After")
 *     .left(column -> column.title("Before").text("Manual process"))
 *     .right(column -> column.title("After").text("Automated process")));
 * }
 */
public final class TwoColumnsSlideBuilder extends AbstractSlideBuilder<TwoColumnsSlideBuilder> {

	public TwoColumnsSlideBuilder() {
		super(SlideType.TWO_COLUMNS);
	}

	public TwoColumnsSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	public TwoColumnsSlideBuilder left(Consumer<ColumnBuilder> column) {
		return column("left", column);
	}

	public TwoColumnsSlideBuilder right(Consumer<ColumnBuilder> column) {
		return column("right", column);
	}

	private TwoColumnsSlideBuilder column(String prefix, Consumer<ColumnBuilder> column) {
		Objects.requireNonNull(column, prefix).accept(new SlideBlock(this, prefix));
		return this;
	}
}
