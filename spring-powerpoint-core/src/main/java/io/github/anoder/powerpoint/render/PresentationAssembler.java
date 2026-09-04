package io.github.anoder.powerpoint.render;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.github.anoder.powerpoint.model.RenderedSlide;

/**
 * Merges the individually rendered slides into a single presentation.
 */
public interface PresentationAssembler {

	/**
	 * @param slides the rendered slides, in presentation order; must not be empty
	 * @param output where the {@code .pptx} is written; not closed by this method
	 */
	void assemble(List<RenderedSlide> slides, OutputStream output) throws IOException;
}
