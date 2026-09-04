package io.github.anoder.powerpoint.dsl;

import java.util.Objects;
import java.util.function.Supplier;

import io.github.anoder.powerpoint.SlideType;

/**
 * A {@link SlideBuilderFactory} made of a slide type and a builder constructor, which is all a
 * factory usually needs:
 *
 * {@snippet :
 * new DefaultSlideBuilderFactory<>(SlideType.TITLE, TitleSlideBuilder::new);
 * }
 *
 * @param <B> the builder type created by this factory
 */
public record DefaultSlideBuilderFactory<B extends SlideBuilder<B>>(SlideType<B> type, Supplier<B> constructor)
		implements SlideBuilderFactory<B> {

	public DefaultSlideBuilderFactory {
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(constructor, "constructor");
	}

	@Override
	public B create() {
		return constructor.get();
	}
}
