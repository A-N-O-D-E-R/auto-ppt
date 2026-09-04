package io.github.anoder.powerpoint.render;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;

import io.github.anoder.powerpoint.InvalidSlideException;
import io.github.anoder.powerpoint.PowerPointRenderingException;
import io.github.anoder.powerpoint.model.RenderedSlide;

/**
 * Merges the rendered slides with Apache POI/XSLF.
 *
 * <p>The first rendered slide becomes the base presentation, so the theme, slide masters and layouts of
 * its template are the ones of the result. The slides of the other rendered presentations are then
 * copied into it with {@link XSLFSlide#importContent(org.apache.poi.xslf.usermodel.XSLFSheet)}, which
 * copies the shapes, their formatting and their pictures. Nothing is concatenated at the file level.
 *
 * <p>Because a slide layout cannot be copied across presentations, the copied slides are attached to
 * the layout of the base presentation whose name matches, or to its first layout. Design the templates
 * of a theme with explicit text boxes rather than layout placeholders: the formatting then travels with
 * the shapes and the result looks like the templates.
 */
public final class PoiPresentationAssembler implements PresentationAssembler {

	@Override
	public void assemble(List<RenderedSlide> slides, OutputStream output) throws IOException {
		Objects.requireNonNull(slides, "slides");
		Objects.requireNonNull(output, "output");
		if (slides.isEmpty()) {
			throw new InvalidSlideException("A presentation must contain at least one slide");
		}

		try (XMLSlideShow presentation = Decks.open(slides.getFirst().content())) {
			for (RenderedSlide slide : slides.subList(1, slides.size())) {
				append(presentation, slide);
			}
			presentation.write(output);
		}
	}

	private void append(XMLSlideShow presentation, RenderedSlide slide) throws IOException {
		try (XMLSlideShow source = Decks.open(slide.content())) {
			for (XSLFSlide sourceSlide : source.getSlides()) {
				createSlide(presentation, sourceSlide).importContent(sourceSlide);
			}
		} catch (RuntimeException e) {
			throw new PowerPointRenderingException("Cannot merge a rendered " + slide.type() + " slide", e);
		}
	}

	private XSLFSlide createSlide(XMLSlideShow presentation, XSLFSlide sourceSlide) {
		XSLFSlideLayout layout = matchingLayout(presentation, sourceSlide);
		return layout == null ? presentation.createSlide() : presentation.createSlide(layout);
	}

	private XSLFSlideLayout matchingLayout(XMLSlideShow presentation, XSLFSlide sourceSlide) {
		String sourceLayoutName = layoutName(sourceSlide);
		XSLFSlideLayout fallback = null;
		for (XSLFSlideMaster master : presentation.getSlideMasters()) {
			for (XSLFSlideLayout layout : master.getSlideLayouts()) {
				if (fallback == null) {
					fallback = layout;
				}
				if (layout.getName().equals(sourceLayoutName)) {
					return layout;
				}
			}
		}
		return fallback;
	}

	private String layoutName(XSLFSlide slide) {
		try {
			XSLFSlideLayout layout = slide.getSlideLayout();
			return layout == null ? null : layout.getName();
		} catch (IllegalArgumentException e) {
			// a slide is not required to reference a layout
			return null;
		}
	}
}
