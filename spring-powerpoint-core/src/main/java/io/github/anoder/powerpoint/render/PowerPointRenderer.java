package io.github.anoder.powerpoint.render;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.github.anoder.powerpoint.Theme;
import io.github.anoder.powerpoint.model.SlideModel;

/**
 * Turns slide models into a {@code .pptx} presentation.
 *
 * <p>This is the boundary of the template engine: no engine type appears in this interface, so the
 * engine can be swapped by publishing another bean of this type.
 */
public interface PowerPointRenderer {

	/**
	 * Renders every slide with the templates of the given theme and writes the assembled presentation.
	 *
	 * @param theme  the theme whose templates are used
	 * @param slides the slides, in presentation order; must not be empty
	 * @param output where the {@code .pptx} is written; not closed by this method
	 * @throws IOException                                            if writing fails
	 * @throws io.github.anoder.powerpoint.TemplateNotFoundException       if a template is missing
	 * @throws io.github.anoder.powerpoint.InvalidSlideException           if a slide does not match its template
	 * @throws io.github.anoder.powerpoint.PowerPointRenderingException    if the rendering itself fails
	 */
	void render(Theme theme, List<SlideModel> slides, OutputStream output) throws IOException;
}
