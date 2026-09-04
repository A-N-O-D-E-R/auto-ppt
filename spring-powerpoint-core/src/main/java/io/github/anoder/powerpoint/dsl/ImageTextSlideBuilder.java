package io.github.anoder.powerpoint.dsl;

import java.io.InputStream;
import java.nio.file.Path;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.model.SlideImage;

/**
 * Slide showing an image next to a block of text.
 *
 * <p>The image replaces the placeholder picture of the template: it is resized and centre cropped to
 * fit the placeholder frame. PNG, JPEG, GIF and BMP images are supported.
 *
 * {@snippet :
 * presentation.add(SlideType.IMAGE_TEXT, slide -> slide
 *     .title("Our new office")
 *     .text("Opened in September 2026")
 *     .image(Path.of("office.png")));
 * }
 */
public final class ImageTextSlideBuilder extends AbstractSlideBuilder<ImageTextSlideBuilder> {

	public ImageTextSlideBuilder() {
		super(SlideType.IMAGE_TEXT);
	}

	public ImageTextSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	public ImageTextSlideBuilder text(String value) {
		put("text", value);
		return this;
	}

	/**
	 * Sets the image of the slide.
	 */
	public ImageTextSlideBuilder image(byte[] image) {
		put("image", new SlideImage(image));
		return this;
	}

	/**
	 * Sets the image of the slide by reading the stream fully; the stream is not closed.
	 */
	public ImageTextSlideBuilder image(InputStream image) {
		put("image", SlideImages.read(image, type(), "image"));
		return this;
	}

	/**
	 * Sets the image of the slide by reading the given file.
	 */
	public ImageTextSlideBuilder image(Path image) {
		put("image", SlideImages.read(image, type(), "image"));
		return this;
	}
}
