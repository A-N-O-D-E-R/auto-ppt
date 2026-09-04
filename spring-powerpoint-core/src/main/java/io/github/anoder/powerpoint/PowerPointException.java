package io.github.anoder.powerpoint;

/**
 * Base class of every exception thrown by this library.
 */
public class PowerPointException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PowerPointException(String message) {
		super(message);
	}

	public PowerPointException(String message, Throwable cause) {
		super(message, cause);
	}
}
