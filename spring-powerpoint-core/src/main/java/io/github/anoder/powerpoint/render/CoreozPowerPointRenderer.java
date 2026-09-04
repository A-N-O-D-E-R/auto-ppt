package io.github.anoder.powerpoint.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.ppt.PptMapper;
import com.coreoz.ppt.PptTemplates;
import io.github.anoder.powerpoint.InvalidSlideException;
import io.github.anoder.powerpoint.PowerPointException;
import io.github.anoder.powerpoint.PowerPointRenderingException;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.Theme;
import io.github.anoder.powerpoint.model.RenderedSlide;
import io.github.anoder.powerpoint.model.SlideImage;
import io.github.anoder.powerpoint.model.SlideModel;
import io.github.anoder.powerpoint.template.TemplateRepository;

/**
 * Renders slides with <a href="https://github.com/Coreoz/PPT-Templates">Coreoz PPT-Templates</a>.
 *
 * <p>This is the only class aware of the template engine: it translates the variable values of a
 * {@link SlideModel} into a Coreoz {@code PptMapper}, fills in one template per slide, then hands the
 * results to a {@link PresentationAssembler}. Nothing from Coreoz appears in the public API.
 *
 * <p>Fail-fast rules, in both directions:
 * <ul>
 *   <li>a value whose variable does not exist in the template is a mistake in the slide builder and
 *       raises an {@link InvalidSlideException};</li>
 *   <li>a template variable with no value is logged and neutralised, so a template variable is never
 *       left visible in the generated presentation: a text variable is replaced by an empty string, and
 *       a shape declaring an image variable — an unset photo, an unset chart — is removed;</li>
 *   <li>a variable that survives the rendering raises a {@link PowerPointRenderingException}.</li>
 * </ul>
 */
public final class CoreozPowerPointRenderer implements PowerPointRenderer {

	private static final Logger logger = LoggerFactory.getLogger(CoreozPowerPointRenderer.class);

	private final TemplateRepository templates;
	private final PresentationAssembler assembler;

	public CoreozPowerPointRenderer(TemplateRepository templates, PresentationAssembler assembler) {
		this.templates = Objects.requireNonNull(templates, "templates");
		this.assembler = Objects.requireNonNull(assembler, "assembler");
	}

	@Override
	public void render(Theme theme, List<SlideModel> slides, OutputStream output) throws IOException {
		Objects.requireNonNull(theme, "theme");
		Objects.requireNonNull(slides, "slides");
		Objects.requireNonNull(output, "output");
		if (slides.isEmpty()) {
			throw new InvalidSlideException("A presentation must contain at least one slide");
		}

		List<RenderedSlide> renderedSlides = new ArrayList<>(slides.size());
		for (SlideModel slide : slides) {
			renderedSlides.add(renderSlide(theme, slide));
		}

		ByteArrayOutputStream assembled = new ByteArrayOutputStream();
		assembler.assemble(renderedSlides, assembled);
		byte[] presentation = assembled.toByteArray();
		verifyEveryVariableWasReplaced(theme, presentation);
		output.write(presentation);
	}

	private RenderedSlide renderSlide(Theme theme, SlideModel slide) {
		String context = context(theme, slide.type());
		byte[] template = readTemplate(theme, slide.type());

		try (XMLSlideShow templatePresentation = Decks.open(template)) {
			PptMapper mapper = mapper(slide, TemplateVariables.declaredIn(templatePresentation), context);

			// the template presentation is filled in place, then written out: it is a throw-away copy
			ByteArrayOutputStream out = new ByteArrayOutputStream(template.length);
			PptTemplates.processPpt(templatePresentation, mapper).write(out);
			return new RenderedSlide(slide.type(), out.toByteArray());
		} catch (PowerPointException e) {
			throw e;
		} catch (IOException | RuntimeException e) {
			throw new PowerPointRenderingException("Cannot render slide. " + context, e);
		}
	}

	private byte[] readTemplate(Theme theme, SlideType<?> slideType) {
		try (InputStream template = templates.get(theme, slideType)) {
			return template.readAllBytes();
		} catch (IOException e) {
			throw new PowerPointRenderingException("Cannot read template. " + context(theme, slideType), e);
		}
	}

	private PptMapper mapper(SlideModel slide, TemplateVariables.Declared declared, String context) {
		Set<String> declaredVariables = declared.all();
		Set<String> unknownValues = new LinkedHashSet<>(slide.values().keySet());
		unknownValues.removeAll(declaredVariables);
		if (!unknownValues.isEmpty()) {
			throw new InvalidSlideException("The template declares no variable for " + unknownValues
				+ ". Declared variables: " + declaredVariables + ". " + context);
		}

		PptMapper mapper = new PptMapper();
		for (Map.Entry<String, Object> value : slide.values().entrySet()) {
			String variable = value.getKey();
			switch (value.getValue()) {
				case SlideImage image -> mapper.image(variable, image.bytes());
				case String text -> mapper.text(variable, text);
				case Number number -> mapper.text(variable, number);
				case Object unsupported -> throw new InvalidSlideException("Unsupported value type "
					+ unsupported.getClass().getName() + " for variable '" + variable + "'. " + context);
			}
		}

		Set<String> withoutValue = new LinkedHashSet<>(declaredVariables);
		withoutValue.removeAll(slide.values().keySet());
		if (!withoutValue.isEmpty()) {
			logger.warn("No value provided for the template variables {}: the texts are emptied and the shapes"
				+ " carrying an image variable are removed. {}", withoutValue, context);
			for (String variable : withoutValue) {
				if (declared.hyperlinks().contains(variable)) {
					// emptying the text of a picture would leave the placeholder image visible
					mapper.hide(variable);
				} else {
					mapper.text(variable, "");
				}
			}
		}

		return mapper;
	}

	private void verifyEveryVariableWasReplaced(Theme theme, byte[] presentation) {
		try (XMLSlideShow rendered = Decks.open(presentation)) {
			Set<String> remaining = TemplateVariables.textVariablesIn(rendered);
			if (!remaining.isEmpty()) {
				throw new PowerPointRenderingException("The variables " + remaining
					+ " are still present in the generated presentation. Theme: " + theme);
			}
		} catch (IOException e) {
			throw new PowerPointRenderingException("Cannot read back the generated presentation. Theme: " + theme, e);
		}
	}

	private String context(Theme theme, SlideType<?> slideType) {
		return "Theme: " + theme
			+ ", slide type: " + slideType
			+ ", template: " + templates.describe(theme, slideType);
	}
}
