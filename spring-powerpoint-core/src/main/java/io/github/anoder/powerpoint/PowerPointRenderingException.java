package io.github.anoder.powerpoint;

/**
 * Thrown when the template engine or Apache POI fails while rendering or assembling a presentation.
 */
public class PowerPointRenderingException extends PowerPointException {

	private static final long serialVersionUID = 1L;

	public PowerPointRenderingException(String message) {
		super(message);
	}

	public PowerPointRenderingException(String message, Throwable cause) {
		super(message, cause);
	}
}
