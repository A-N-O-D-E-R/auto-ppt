package io.github.anoder.powerpoint;

/**
 * Thrown when the {@code .pptx} template of a theme/slide type combination cannot be found.
 */
public class TemplateNotFoundException extends PowerPointException {

	private static final long serialVersionUID = 1L;

	private final Theme theme;
	private final SlideType<?> slideType;
	private final String templatePath;

	public TemplateNotFoundException(Theme theme, SlideType<?> slideType, String templatePath) {
		super("No PowerPoint template found. Theme: " + theme
			+ ", slide type: " + slideType
			+ ", template: " + templatePath);
		this.theme = theme;
		this.slideType = slideType;
		this.templatePath = templatePath;
	}

	public Theme theme() {
		return theme;
	}

	public SlideType<?> slideType() {
		return slideType;
	}

	/**
	 * @return the resolved location the template was looked up at
	 */
	public String templatePath() {
		return templatePath;
	}
}
