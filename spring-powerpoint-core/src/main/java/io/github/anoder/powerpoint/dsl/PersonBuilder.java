package io.github.anoder.powerpoint.dsl;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * One of the people of a {@link TeamSlideBuilder team} slide.
 *
 * <p>The photo is optional: when it is not set, the placeholder picture of the template is removed
 * rather than left visible.
 */
public interface PersonBuilder {

	PersonBuilder name(String value);

	/** The role, e.g. {@code Chief Executive Officer}. */
	PersonBuilder role(String value);

	PersonBuilder photo(byte[] photo);

	/** Reads the stream fully; the stream is not closed. */
	PersonBuilder photo(InputStream photo);

	PersonBuilder photo(Path photo);
}
