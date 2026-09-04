package io.github.anoder.powerpoint.render;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

/**
 * Opens a {@code .pptx} held in memory.
 *
 * <p>Apache POI refuses to read a zip entry that expands far more than its compressed size (its zip bomb
 * protection, {@code ZipSecureFile.MIN_INFLATE_RATIO}) but only when the package is read from an
 * {@link java.io.InputStream}. A picture with large flat areas — a logo, a chart, a screenshot — regularly
 * compresses beyond that ratio, so reading back a presentation this library just produced would fail. The
 * bytes come from this library, not from an untrusted source, so they are written to a temporary file and
 * read from there: the check does not apply to file-backed packages and the protection of the host
 * application, which is a JVM-wide setting, is left untouched.
 */
final class Decks {

	private Decks() {
	}

	/**
	 * Opens a modifiable presentation; closing it discards the changes and deletes the temporary file.
	 */
	static XMLSlideShow open(byte[] content) throws IOException {
		Path file = Files.createTempFile("spring-powerpoint-", ".pptx");
		try {
			Files.write(file, content);
			return new TemporaryFileSlideShow(OPCPackage.open(file.toFile(), PackageAccess.READ_WRITE), file);
		} catch (InvalidFormatException e) {
			Files.deleteIfExists(file);
			throw new IOException("Not a valid PowerPoint presentation", e);
		} catch (IOException | RuntimeException e) {
			Files.deleteIfExists(file);
			throw e;
		}
	}

	private static final class TemporaryFileSlideShow extends XMLSlideShow {

		private final Path file;

		private TemporaryFileSlideShow(OPCPackage opcPackage, Path file) {
			super(opcPackage);
			this.file = file;
		}

		@Override
		public void close() throws IOException {
			try {
				// revert instead of close: the changes must never be saved back to the temporary file
				getPackage().revert();
			} finally {
				Files.deleteIfExists(file);
			}
		}
	}
}
