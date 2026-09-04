package io.github.anoder.powerpoint.dsl;

/**
 * One of the figures of a {@link MetricsSlideBuilder metrics} slide: a big value and what it measures.
 */
public interface MetricBuilder {

	/** The figure itself, e.g. {@code 24%} or {@code $1.2B}. */
	MetricBuilder value(String value);

	/** What the figure measures, e.g. {@code Annual recurring revenue}. */
	MetricBuilder label(String value);
}
