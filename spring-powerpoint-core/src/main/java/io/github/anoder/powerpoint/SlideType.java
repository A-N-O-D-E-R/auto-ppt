package io.github.anoder.powerpoint;

import java.util.List;
import java.util.Objects;

import io.github.anoder.powerpoint.dsl.AgendaSlideBuilder;
import io.github.anoder.powerpoint.dsl.ChartSlideBuilder;
import io.github.anoder.powerpoint.dsl.ConclusionSlideBuilder;
import io.github.anoder.powerpoint.dsl.FullImageSlideBuilder;
import io.github.anoder.powerpoint.dsl.ImageTextSlideBuilder;
import io.github.anoder.powerpoint.dsl.MetricsSlideBuilder;
import io.github.anoder.powerpoint.dsl.QuoteSlideBuilder;
import io.github.anoder.powerpoint.dsl.SectionSlideBuilder;
import io.github.anoder.powerpoint.dsl.SlideBuilder;
import io.github.anoder.powerpoint.dsl.StatementSlideBuilder;
import io.github.anoder.powerpoint.dsl.TeamSlideBuilder;
import io.github.anoder.powerpoint.dsl.ThreePartsSlideBuilder;
import io.github.anoder.powerpoint.dsl.TimelineSlideBuilder;
import io.github.anoder.powerpoint.dsl.TitleSlideBuilder;
import io.github.anoder.powerpoint.dsl.TwoColumnsSlideBuilder;

/**
 * Identifies both the {@code .pptx} template of a slide and the builder type that configures it.
 *
 * <p>This is a typed constant holder rather than an {@code enum}, because a Java enum cannot carry a
 * type parameter: the builder type must be part of the constant so that
 * {@link PresentationBuilder#add(SlideType, java.util.function.Consumer)} can hand a strongly typed
 * builder to the lambda. Usage is unchanged from an enum:
 *
 * {@snippet :
 * presentation.add(SlideType.TITLE, slide -> slide.title("Q3 Business Review"));
 * }
 *
 * <p>This class carries no rendering logic: templates are resolved by
 * {@link io.github.anoder.powerpoint.template.TemplateRepository} and builders are created by
 * {@link io.github.anoder.powerpoint.dsl.SlideBuilderFactory}.
 *
 * @param <B> the builder type used to configure slides of this type
 */
public final class SlideType<B extends SlideBuilder<B>> {

	public static final SlideType<TitleSlideBuilder> TITLE = new SlideType<>("TITLE", "title");
	public static final SlideType<ThreePartsSlideBuilder> THREE_PARTS = new SlideType<>("THREE_PARTS", "three-parts");
	public static final SlideType<TwoColumnsSlideBuilder> TWO_COLUMNS = new SlideType<>("TWO_COLUMNS", "two-columns");
	public static final SlideType<ImageTextSlideBuilder> IMAGE_TEXT = new SlideType<>("IMAGE_TEXT", "image-text");
	public static final SlideType<SectionSlideBuilder> SECTION = new SlideType<>("SECTION", "section");
	public static final SlideType<ConclusionSlideBuilder> CONCLUSION = new SlideType<>("CONCLUSION", "conclusion");
	public static final SlideType<AgendaSlideBuilder> AGENDA = new SlideType<>("AGENDA", "agenda");
	public static final SlideType<StatementSlideBuilder> STATEMENT = new SlideType<>("STATEMENT", "statement");
	public static final SlideType<MetricsSlideBuilder> METRICS = new SlideType<>("METRICS", "metrics");
	public static final SlideType<TimelineSlideBuilder> TIMELINE = new SlideType<>("TIMELINE", "timeline");
	public static final SlideType<TeamSlideBuilder> TEAM = new SlideType<>("TEAM", "team");
	public static final SlideType<QuoteSlideBuilder> QUOTE = new SlideType<>("QUOTE", "quote");
	public static final SlideType<ChartSlideBuilder> CHART = new SlideType<>("CHART", "chart");
	public static final SlideType<FullImageSlideBuilder> FULL_IMAGE = new SlideType<>("FULL_IMAGE", "full-image");

	private static final List<SlideType<?>> VALUES = List.of(
		TITLE, SECTION, AGENDA, THREE_PARTS, TWO_COLUMNS, IMAGE_TEXT, STATEMENT, METRICS, TIMELINE, TEAM,
		QUOTE, CHART, FULL_IMAGE, CONCLUSION
	);

	private final String name;
	private final String templateName;

	private SlideType(String name, String templateName) {
		this.name = Objects.requireNonNull(name, "name");
		this.templateName = Objects.requireNonNull(templateName, "templateName");
	}

	/**
	 * Declares a custom slide type; the built-in ones are the constants of this class.
	 *
	 * @param name         the constant-style name, e.g. {@code AGENDA}
	 * @param templateName the template file name without extension, e.g. {@code agenda}
	 */
	public static <B extends SlideBuilder<B>> SlideType<B> of(String name, String templateName) {
		return new SlideType<>(name, templateName);
	}

	/**
	 * @return the built-in slide types
	 */
	public static List<SlideType<?>> values() {
		return VALUES;
	}

	/**
	 * @param name a constant name ({@code THREE_PARTS}) or a template name ({@code three-parts})
	 * @throws IllegalArgumentException if no built-in slide type matches
	 */
	public static SlideType<?> valueOf(String name) {
		return VALUES.stream()
			.filter(type -> type.name.equalsIgnoreCase(name) || type.templateName.equalsIgnoreCase(name))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown slide type '" + name + "', expected one of " + VALUES));
	}

	/**
	 * @return the constant-style name, e.g. {@code THREE_PARTS}
	 */
	public String name() {
		return name;
	}

	/**
	 * @return the template file name without extension, e.g. {@code three-parts}
	 */
	public String templateName() {
		return templateName;
	}

	@Override
	public String toString() {
		return name;
	}
}
