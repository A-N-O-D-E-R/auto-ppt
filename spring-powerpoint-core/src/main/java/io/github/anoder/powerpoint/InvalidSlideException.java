package io.github.anoder.powerpoint;

/**
 * Thrown when a slide cannot be rendered because its content does not match its template, for example
 * when a value has no corresponding variable in the template.
 */
public class InvalidSlideException extends PowerPointException {

	private static final long serialVersionUID = 1L;

	public InvalidSlideException(String message) {
		super(message);
	}

	public InvalidSlideException(String message, Throwable cause) {
		super(message, cause);
	}
}
