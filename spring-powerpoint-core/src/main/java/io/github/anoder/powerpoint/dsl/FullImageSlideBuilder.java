package io.github.anoder.powerpoint.dsl;

import java.io.InputStream;
import java.nio.file.Path;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.model.SlideImage;

/**
 * Slide filled by one image, with a headline written over it: the visual break of a pitch deck.
 *
 * <p>The image is resized and centre cropped to the whole slide frame.
 *
 * {@snippet :
 * presentation.add(SlideType.FULL_IMAGE, slide -> slide
 *     .image(Path.of("foundry.jpg"))
 *     .headline("The foundry")
 *     .subheadline("Two hectares of automated biology"));
 * }
 */
public final class FullImageSlideBuilder extends AbstractSlideBuilder<FullImageSlideBuilder> {

	public FullImageSlideBuilder() {
		super(SlideType.FULL_IMAGE);
	}

	public FullImageSlideBuilder headline(String value) {
		put("headline", value);
		return this;
	}

	public FullImageSlideBuilder subheadline(String value) {
		put("subheadline", value);
		return this;
	}

	public FullImageSlideBuilder image(byte[] image) {
		put("image", new SlideImage(image));
		return this;
	}

	/**
	 * Reads the stream fully; the stream is not closed.
	 */
	public FullImageSlideBuilder image(InputStream image) {
		put("image", SlideImages.read(image, type(), "image"));
		return this;
	}

	public FullImageSlideBuilder image(Path image) {
		put("image", SlideImages.read(image, type(), "image"));
		return this;
	}
}
