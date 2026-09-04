package io.github.anoder.powerpoint.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.anoder.powerpoint.Theme;

/**
 * Configuration of the PowerPoint generation.
 *
 * <pre>{@code
 * powerpoint:
 *   default-theme: corporate
 *   templates:
 *     location: classpath:/powerpoint
 * }</pre>
 */
@ConfigurationProperties(prefix = "powerpoint")
public class PowerPointProperties {

	/**
	 * Theme used by {@code PowerPoint.presentation()}, when no theme is given explicitly.
	 */
	private Theme defaultTheme = Theme.CORPORATE;

	private final Templates templates = new Templates();

	public Theme getDefaultTheme() {
		return defaultTheme;
	}

	public void setDefaultTheme(Theme defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

	public Templates getTemplates() {
		return templates;
	}

	/**
	 * Where the {@code .pptx} templates are read from.
	 */
	public static class Templates {

		/**
		 * Root location of the templates; a template is read from
		 * {@code {location}/{theme}/{slide-type}.pptx}.
		 */
		// the literal, and not ClasspathTemplateRepository.DEFAULT_LOCATION, so that the configuration
		// metadata generated for the IDE carries the default value
		private String location = "classpath:/powerpoint";

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}
	}
}
