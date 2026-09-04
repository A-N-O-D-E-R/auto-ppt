package io.github.anoder.demo;

import org.springframework.stereotype.Service;

import io.github.anoder.powerpoint.PowerPoint;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.Theme;

@Service
public class BusinessReviewService {

	private final PowerPoint powerpoint;

	public BusinessReviewService(PowerPoint powerpoint) {
		this.powerpoint = powerpoint;
	}

	public byte[] generate() {

		return powerpoint
			.presentation(Theme.CORPORATE)

			.add(
				SlideType.TITLE,
				slide -> slide
					.title("Q3 Business Review")
					.subtitle("September 2026")
			)

			.add(
				SlideType.SECTION,
				slide -> slide
					.title("Performance")
					.subtitle("Q3 2026")
			)

			.add(
				SlideType.THREE_PARTS,
				slide -> slide
					.title("Key achievements")
					.parts(
						part -> part
							.title("Revenue")
							.text("+24%"),

						part -> part
							.title("Customers")
							.text("+18%"),

						part -> part
							.title("Margin")
							.text("+4 pts")
					)
			)

			.add(
				SlideType.CONCLUSION,
				slide -> slide
					.title("Thank you")
			)

			.build()
			.toByteArray();
	}
}
