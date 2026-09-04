package io.github.anoder.powerpoint;

/**
 * Thrown when no builder is registered for a slide type.
 */
public class UnsupportedSlideTypeException extends PowerPointException {

	private static final long serialVersionUID = 1L;

	private final SlideType<?> slideType;

	public UnsupportedSlideTypeException(SlideType<?> slideType, Iterable<String> registeredTypes) {
		super("No slide builder registered for slide type: " + slideType
			+ ". Registered types: " + String.join(", ", registeredTypes));
		this.slideType = slideType;
	}

	public SlideType<?> slideType() {
		return slideType;
	}
}
