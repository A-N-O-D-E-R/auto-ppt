package io.github.anoder.powerpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * An immutable, already rendered {@code .pptx} presentation.
 */
public final class PowerPointPresentation {

	private final byte[] content;

	/**
	 * @param content the rendered {@code .pptx} bytes, copied defensively
	 */
	public PowerPointPresentation(byte[] content) {
		this.content = Objects.requireNonNull(content, "content").clone();
	}

	/**
	 * Writes the presentation to the given stream, which is not closed.
	 */
	public void write(OutputStream output) throws IOException {
		Objects.requireNonNull(output, "output").write(content);
	}

	/**
	 * Writes the presentation to the given file, creating or truncating it.
	 */
	public void write(Path path) throws IOException {
		Files.write(Objects.requireNonNull(path, "path"), content);
	}

	/**
	 * @return a copy of the rendered {@code .pptx} bytes
	 */
	public byte[] toByteArray() {
		return content.clone();
	}

	/**
	 * @return the size of the presentation in bytes
	 */
	public int size() {
		return content.length;
	}
}
