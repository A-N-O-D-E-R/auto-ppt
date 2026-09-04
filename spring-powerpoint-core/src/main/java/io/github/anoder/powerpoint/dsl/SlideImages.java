package io.github.anoder.powerpoint.dsl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import io.github.anoder.powerpoint.InvalidSlideException;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.model.SlideImage;

/**
 * Reads the images handed to the builders, eagerly, so that a slide never holds an open stream and a
 * missing file fails where the application can see it.
 */
final class SlideImages {

	private SlideImages() {
	}

	/**
	 * Reads the stream fully; the stream is not closed.
	 */
	static SlideImage read(InputStream image, SlideType<?> type, String what) {
		Objects.requireNonNull(image, what);
		try {
			return new SlideImage(image.readAllBytes());
		} catch (IOException e) {
			throw new InvalidSlideException("Cannot read the " + what + " of a " + type + " slide", e);
		}
	}

	static SlideImage read(Path image, SlideType<?> type, String what) {
		Objects.requireNonNull(image, what);
		try {
			return new SlideImage(Files.readAllBytes(image));
		} catch (IOException e) {
			throw new InvalidSlideException(
				"Cannot read the " + what + " file " + image + " of a " + type + " slide", e);
		}
	}
}
