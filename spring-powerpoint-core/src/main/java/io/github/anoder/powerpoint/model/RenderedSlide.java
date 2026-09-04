package io.github.anoder.powerpoint.model;

import java.util.Objects;

import io.github.anoder.powerpoint.SlideType;

/**
 * A single slide whose template has been filled in, as a standalone one-slide {@code .pptx}.
 *
 * @param type    the slide type it was rendered from, for error reporting
 * @param content the rendered one-slide presentation
 */
public record RenderedSlide(SlideType<?> type, byte[] content) {

	public RenderedSlide {
		Objects.requireNonNull(type, "type");
		content = Objects.requireNonNull(content, "content").clone();
	}

	@Override
	public byte[] content() {
		return content.clone();
	}

	@Override
	public String toString() {
		return "RenderedSlide[" + type + ", " + content.length + " bytes]";
	}
}
