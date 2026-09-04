package io.github.anoder.powerpoint.dsl;

/**
 * One of the columns of a {@link TwoColumnsSlideBuilder two columns} slide.
 */
public interface ColumnBuilder {

	ColumnBuilder title(String value);

	ColumnBuilder text(String value);
}
