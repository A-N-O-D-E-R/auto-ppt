package io.github.anoder.powerpoint.template;

import java.io.InputStream;
import java.util.Objects;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.TemplateNotFoundException;
import io.github.anoder.powerpoint.Theme;

/**
 * Loads templates from the classpath, at {@code {location}/{theme}/{slide-type}.pptx}, for example
 * {@code /powerpoint/corporate/three-parts.pptx}.
 */
public final class ClasspathTemplateRepository implements TemplateRepository {

	/** Default template location, matching {@code powerpoint.templates.location}. */
	public static final String DEFAULT_LOCATION = "classpath:/powerpoint";

	private static final String CLASSPATH_PREFIX = "classpath:";

	private final String location;
	private final ClassLoader classLoader;

	public ClasspathTemplateRepository() {
		this(DEFAULT_LOCATION);
	}

	/**
	 * @param location the root folder of the templates, with an optional {@code classpath:} prefix
	 */
	public ClasspathTemplateRepository(String location) {
		this(location, ClasspathTemplateRepository.class.getClassLoader());
	}

	public ClasspathTemplateRepository(String location, ClassLoader classLoader) {
		this.location = normalize(Objects.requireNonNull(location, "location"));
		this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
	}

	private static String normalize(String location) {
		String path = location.startsWith(CLASSPATH_PREFIX) ? location.substring(CLASSPATH_PREFIX.length()) : location;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
	}

	@Override
	public InputStream get(Theme theme, SlideType<?> slideType) {
		String path = describe(theme, slideType);
		// ClassLoader#getResourceAsStream does not accept a leading slash
		InputStream template = classLoader.getResourceAsStream(path.substring(1));
		if (template == null) {
			throw new TemplateNotFoundException(theme, slideType, path);
		}
		return template;
	}

	@Override
	public String describe(Theme theme, SlideType<?> slideType) {
		Objects.requireNonNull(theme, "theme");
		Objects.requireNonNull(slideType, "slideType");
		return location + "/" + theme.folder() + "/" + slideType.templateName() + ".pptx";
	}

	/**
	 * @return the normalized root folder of the templates, e.g. {@code /powerpoint}
	 */
	public String location() {
		return location;
	}
}
