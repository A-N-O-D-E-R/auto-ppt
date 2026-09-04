package io.github.anoder.powerpoint.template;

import java.io.InputStream;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.TemplateNotFoundException;
import io.github.anoder.powerpoint.Theme;

/**
 * Resolves the {@code .pptx} template of a theme/slide type combination.
 *
 * <p>Replace the {@link ClasspathTemplateRepository default implementation} with a bean of this type
 * to load templates from anywhere else, for instance from a database or an object store.
 */
public interface TemplateRepository {

	/**
	 * @return a stream on the template, which the caller must close
	 * @throws TemplateNotFoundException if the template does not exist
	 */
	InputStream get(Theme theme, SlideType<?> slideType);

	/**
	 * @return where the template of this theme/slide type is looked up, for error messages
	 */
	String describe(Theme theme, SlideType<?> slideType);
}
