package io.github.anoder.powerpoint;

/**
 * A visual identity for a presentation.
 *
 * <p>Each theme maps to a folder of {@code .pptx} templates, one template per {@link SlideType}:
 * {@code /powerpoint/{folder}/{slide-type}.pptx}.
 */
public enum Theme {

	CORPORATE("corporate"),
	MODERN("modern"),
	MINIMAL("minimal");

	private final String folder;

	Theme(String folder) {
		this.folder = folder;
	}

	/**
	 * @return the template folder name of this theme, e.g. {@code corporate}
	 */
	public String folder() {
		return folder;
	}
}
