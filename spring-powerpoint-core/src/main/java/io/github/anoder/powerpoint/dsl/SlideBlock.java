package io.github.anoder.powerpoint.dsl;

import java.io.InputStream;
import java.nio.file.Path;

import io.github.anoder.powerpoint.model.SlideImage;

/**
 * A repeated block inside a slide, stored under a variable prefix such as {@code part0}, {@code left},
 * {@code metric2} or {@code person1}: every method writes {@code prefix.field}.
 *
 * <p>One implementation serves every block interface, because they are the same shape — a few named
 * fields — and are only kept separate so that the DSL reads like the slide it configures.
 */
final class SlideBlock implements PartBuilder, ColumnBuilder, MetricBuilder, MilestoneBuilder, PersonBuilder {

	private final AbstractSlideBuilder<?> slide;
	private final String prefix;

	SlideBlock(AbstractSlideBuilder<?> slide, String prefix) {
		this.slide = slide;
		this.prefix = prefix;
	}

	@Override
	public SlideBlock title(String value) {
		return put("title", value);
	}

	@Override
	public SlideBlock text(String value) {
		return put("text", value);
	}

	@Override
	public SlideBlock value(String value) {
		return put("value", value);
	}

	@Override
	public SlideBlock label(String value) {
		return put("label", value);
	}

	@Override
	public SlideBlock date(String value) {
		return put("date", value);
	}

	@Override
	public SlideBlock name(String value) {
		return put("name", value);
	}

	@Override
	public SlideBlock role(String value) {
		return put("role", value);
	}

	@Override
	public SlideBlock photo(byte[] photo) {
		return put("photo", new SlideImage(photo));
	}

	@Override
	public SlideBlock photo(InputStream photo) {
		return put("photo", SlideImages.read(photo, slide.type(), "photo"));
	}

	@Override
	public SlideBlock photo(Path photo) {
		return put("photo", SlideImages.read(photo, slide.type(), "photo"));
	}

	private SlideBlock put(String field, Object value) {
		slide.put(prefix + "." + field, value);
		return this;
	}
}
