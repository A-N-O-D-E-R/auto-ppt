package io.github.anoder.powerpoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import io.github.anoder.powerpoint.template.TemplateFixtureGenerator;

/**
 * Generates the starter templates into the test classpath, so that the tests exercise the real
 * classpath resolution and the real rendering without shipping binary fixtures.
 */
public final class TestTemplates {

	static {
		// the tests read presentations from memory, where POI applies its zip bomb protection: the
		// placeholder pictures are flat colours and compress far beyond the default ratio
		ZipSecureFile.setMinInflateRatio(0);
	}

	private static boolean generated;

	private TestTemplates() {
	}

	public static synchronized void generate() {
		if (generated) {
			return;
		}
		try {
			TemplateFixtureGenerator.writeAll(testClasspathRoot().resolve("powerpoint"));
			generated = true;
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot generate the test templates", e);
		}
	}

	public static Path testClasspathRoot() {
		try {
			return Path.of(TestTemplates.class.getResource("/").toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Cannot locate the test classpath root", e);
		}
	}

	/**
	 * @return every text of every shape of every slide of a rendered presentation
	 */
	public static List<String> textOf(byte[] presentation) {
		try (XMLSlideShow slideShow = new XMLSlideShow(new ByteArrayInputStream(presentation))) {
			List<String> texts = new ArrayList<>();
			for (XSLFSlide slide : slideShow.getSlides()) {
				for (XSLFShape shape : slide.getShapes()) {
					if (shape instanceof XSLFTextShape text) {
						texts.add(text.getText());
					}
				}
			}
			return texts;
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot read the generated presentation", e);
		}
	}

	/**
	 * @return how many picture shapes the slides of a rendered presentation hold
	 */
	public static int pictureCount(byte[] presentation) {
		try (XMLSlideShow slideShow = new XMLSlideShow(new ByteArrayInputStream(presentation))) {
			int pictures = 0;
			for (XSLFSlide slide : slideShow.getSlides()) {
				for (XSLFShape shape : slide.getShapes()) {
					if (shape instanceof XSLFPictureShape) {
						pictures++;
					}
				}
			}
			return pictures;
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot read the generated presentation", e);
		}
	}

	public static int slideCount(byte[] presentation) {
		try (XMLSlideShow slideShow = new XMLSlideShow(new ByteArrayInputStream(presentation))) {
			return slideShow.getSlides().size();
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot read the generated presentation", e);
		}
	}
}
