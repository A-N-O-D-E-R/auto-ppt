package io.github.anoder.powerpoint.dsl;

import java.io.InputStream;
import java.nio.file.Path;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.model.SlideImage;

/**
 * Slide showing a chart with the conclusion to draw from it.
 *
 * <p>The chart is an image — export it from the tool that produced it. A chart left unset removes the
 * placeholder picture, which makes the slide usable as a plain "takeaway" slide.
 *
 * {@snippet :
 * presentation.add(SlideType.CHART, slide -> slide
 *     .title("Revenue growth")
 *     .chart(Path.of("revenue.png"))
 *     .takeaway("Doubling every four quarters, with no increase in sales headcount."));
 * }
 */
public final class ChartSlideBuilder extends AbstractSlideBuilder<ChartSlideBuilder> {

	public ChartSlideBuilder() {
		super(SlideType.CHART);
	}

	public ChartSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	/**
	 * The conclusion to draw from the chart, shown under it.
	 */
	public ChartSlideBuilder takeaway(String value) {
		put("takeaway", value);
		return this;
	}

	public ChartSlideBuilder chart(byte[] chart) {
		put("chart", new SlideImage(chart));
		return this;
	}

	/**
	 * Reads the stream fully; the stream is not closed.
	 */
	public ChartSlideBuilder chart(InputStream chart) {
		put("chart", SlideImages.read(chart, type(), "chart"));
		return this;
	}

	public ChartSlideBuilder chart(Path chart) {
		put("chart", SlideImages.read(chart, type(), "chart"));
		return this;
	}
}
