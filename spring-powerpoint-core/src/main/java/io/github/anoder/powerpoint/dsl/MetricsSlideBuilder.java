package io.github.anoder.powerpoint.dsl;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.anoder.powerpoint.SlideType;

/**
 * Slide showing up to {@value #METRIC_COUNT} key figures in a row, the traction slide of a pitch deck.
 *
 * <p>Metrics left unset are replaced by an empty text, so three or two figures are legitimate.
 *
 * {@snippet :
 * presentation.add(SlideType.METRICS, slide -> slide
 *     .title("Traction")
 *     .metrics(
 *         metric -> metric.value("$1.2B").label("Annual recurring revenue"),
 *         metric -> metric.value("+42%").label("Year over year growth"),
 *         metric -> metric.value("120").label("Enterprise customers"),
 *         metric -> metric.value("98%").label("Net retention")));
 * }
 */
public final class MetricsSlideBuilder extends AbstractSlideBuilder<MetricsSlideBuilder> {

	/** Number of figures the template has room for. */
	public static final int METRIC_COUNT = 4;

	public MetricsSlideBuilder() {
		super(SlideType.METRICS);
	}

	public MetricsSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	/**
	 * Configures the four figures at once.
	 */
	@SafeVarargs
	public final MetricsSlideBuilder metrics(Consumer<MetricBuilder>... metrics) {
		Objects.requireNonNull(metrics, "metrics");
		for (int index = 0; index < metrics.length; index++) {
			metric(index, metrics[index]);
		}
		return this;
	}

	/**
	 * Configures a single figure, which is handy when the figures come from a loop.
	 *
	 * @param index zero based index, from {@code 0} to {@value #METRIC_COUNT} minus one
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public MetricsSlideBuilder metric(int index, Consumer<MetricBuilder> metric) {
		Objects.checkIndex(index, METRIC_COUNT);
		Objects.requireNonNull(metric, "metric").accept(new SlideBlock(this, "metric" + index));
		return this;
	}
}
