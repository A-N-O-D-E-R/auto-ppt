package io.github.anoder.powerpoint.dsl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.UnsupportedSlideTypeException;

/**
 * Maps every known {@link SlideType} to the factory of its builder.
 *
 * <p>This is what keeps rendering logic out of {@link SlideType}: adding a slide type means adding a
 * {@link SlideBuilderFactory}, not touching the framework.
 */
public final class SlideBuilderRegistry {

	private final Map<SlideType<?>, SlideBuilderFactory<?>> factories;

	/**
	 * @param factories the known factories; when several factories share a slide type, the last one
	 *                  wins, so an application can override a built-in one
	 */
	public SlideBuilderRegistry(Collection<? extends SlideBuilderFactory<?>> factories) {
		Map<SlideType<?>, SlideBuilderFactory<?>> byType = new LinkedHashMap<>();
		for (SlideBuilderFactory<?> factory : Objects.requireNonNull(factories, "factories")) {
			byType.put(factory.type(), factory);
		}
		this.factories = Map.copyOf(byType);
	}

	/**
	 * @return the factories of the built-in slide types, those of {@link SlideType#values()}
	 */
	public static List<SlideBuilderFactory<?>> builtInFactories() {
		return List.of(
			new DefaultSlideBuilderFactory<>(SlideType.TITLE, TitleSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.SECTION, SectionSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.AGENDA, AgendaSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.THREE_PARTS, ThreePartsSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.TWO_COLUMNS, TwoColumnsSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.IMAGE_TEXT, ImageTextSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.STATEMENT, StatementSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.METRICS, MetricsSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.TIMELINE, TimelineSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.TEAM, TeamSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.QUOTE, QuoteSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.CHART, ChartSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.FULL_IMAGE, FullImageSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.CONCLUSION, ConclusionSlideBuilder::new)
		);
	}

	/**
	 * @return a registry holding only the built-in slide types
	 */
	public static SlideBuilderRegistry withBuiltIns() {
		return new SlideBuilderRegistry(builtInFactories());
	}

	/**
	 * @return a new builder for the given slide type
	 * @throws UnsupportedSlideTypeException if no factory is registered for that type
	 */
	public <B extends SlideBuilder<B>> B create(SlideType<B> type) {
		SlideBuilderFactory<?> factory = factories.get(Objects.requireNonNull(type, "type"));
		if (factory == null) {
			throw new UnsupportedSlideTypeException(type, factories.keySet().stream().map(SlideType::name).sorted().toList());
		}
		// safe: factories are indexed by the type they declare, and a factory of SlideType<B> creates a B
		@SuppressWarnings("unchecked")
		B builder = (B) factory.create();
		return builder;
	}

	/**
	 * @return the registered slide types
	 */
	public Set<SlideType<?>> types() {
		return factories.keySet();
	}
}
