package io.github.anoder.powerpoint.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * An image to inject into a slide, already fully read into memory.
 *
 * @param bytes the image content; PNG, JPEG, GIF and BMP are supported
 */
public record SlideImage(byte[] bytes) {

	public SlideImage {
		bytes = Objects.requireNonNull(bytes, "bytes").clone();
		if (bytes.length == 0) {
			throw new IllegalArgumentException("The image content is empty");
		}
	}

	@Override
	public byte[] bytes() {
		return bytes.clone();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof SlideImage image && Arrays.equals(bytes, image.bytes);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(bytes);
	}

	@Override
	public String toString() {
		return "SlideImage[" + bytes.length + " bytes]";
	}
}
