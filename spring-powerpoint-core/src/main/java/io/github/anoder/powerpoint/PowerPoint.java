package io.github.anoder.powerpoint;

/**
 * Entry point of the library: creates presentation builders.
 *
 * <p>A {@code PowerPoint} bean is provided by the Spring Boot starter and is thread safe.
 *
 * {@snippet :
 * byte[] deck = powerpoint.presentation(Theme.CORPORATE)
 *     .add(SlideType.TITLE, slide -> slide.title("Q3 Business Review").subtitle("September 2026"))
 *     .add(SlideType.CONCLUSION, slide -> slide.title("Thank you"))
 *     .build()
 *     .toByteArray();
 * }
 */
public interface PowerPoint {

	/**
	 * Starts a presentation using the configured default theme
	 * ({@code powerpoint.default-theme}).
	 */
	PresentationBuilder presentation();

	/**
	 * Starts a presentation using an explicit theme.
	 */
	PresentationBuilder presentation(Theme theme);
}
