package io.github.anoder.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.anoder.powerpoint.PowerPoint;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.Theme;

@SpringBootTest
class BusinessReviewServiceTest {

	static {
		// the flat colours of the starter templates compress beyond POI's zip bomb ratio when a
		// presentation is read back from memory
		ZipSecureFile.setMinInflateRatio(0);
	}

	@Autowired
	private BusinessReviewService businessReview;

	@Autowired
	private PowerPoint powerpoint;

	@Test
	void generates_the_business_review_deck() throws Exception {
		byte[] presentation = businessReview.generate();

		assertThat(texts(presentation))
			.hasSizeGreaterThanOrEqualTo(4)
			.contains("Q3 Business Review", "September 2026", "Performance", "Key achievements",
				"Revenue", "+24%", "Customers", "+18%", "Margin", "+4 pts", "Thank you")
			.noneMatch(text -> text.contains("$/"));
	}

	@Test
	void the_injected_PowerPoint_uses_the_configured_default_theme() throws Exception {
		byte[] presentation = powerpoint.presentation()
			.add(SlideType.TITLE, slide -> slide.title("Hello").subtitle("World"))
			.build()
			.toByteArray();

		assertThat(slideCount(presentation)).isEqualTo(1);
		assertThat(texts(presentation)).contains("Hello", "World");
	}

	@Test
	void every_theme_and_slide_type_of_the_example_templates_can_be_rendered() throws Exception {
		for (Theme theme : Theme.values()) {
			byte[] presentation = powerpoint.presentation(theme)
				.add(SlideType.TITLE, slide -> slide.title("Title").subtitle("Subtitle"))
				.add(SlideType.SECTION, slide -> slide.title("Section").subtitle("Subtitle"))
				.add(SlideType.THREE_PARTS, slide -> slide
					.title("Three parts")
					.parts(
						part -> part.title("One").text("First"),
						part -> part.title("Two").text("Second"),
						part -> part.title("Three").text("Third")
					))
				.add(SlideType.TWO_COLUMNS, slide -> slide
					.title("Two columns")
					.left(column -> column.title("Left").text("Before"))
					.right(column -> column.title("Right").text("After")))
				.add(SlideType.IMAGE_TEXT, slide -> slide
					.title("Image and text")
					.text("A picture replaces the placeholder")
					.image(logo()))
				.add(SlideType.AGENDA, slide -> slide
					.title("Agenda")
					.items("Agenda item", "Second item", "Third item"))
				.add(SlideType.STATEMENT, slide -> slide
					.statement("Statement")
					.attribution("Attribution"))
				.add(SlideType.METRICS, slide -> slide
					.title("Metrics")
					.metrics(
						metric -> metric.value("+24%").label("Revenue"),
						metric -> metric.value("120").label("Customers")
					))
				.add(SlideType.TIMELINE, slide -> slide
					.title("Timeline")
					.milestones(
						milestone -> milestone.date("Q1 2026").text("Milestone"),
						milestone -> milestone.date("Q3 2026").text("Launch")
					))
				.add(SlideType.TEAM, slide -> slide
					.title("Team")
					.people(
						person -> person.name("Person").role("Role").photo(logo()),
						person -> person.name("Without photo").role("Role")
					))
				.add(SlideType.QUOTE, slide -> slide
					.quote("Quote")
					.author("Author")
					.role("Role"))
				.add(SlideType.CHART, slide -> slide
					.title("Chart")
					.chart(logo())
					.takeaway("Takeaway"))
				.add(SlideType.FULL_IMAGE, slide -> slide
					.image(logo())
					.headline("Full image")
					.subheadline("Subheadline"))
				.add(SlideType.CONCLUSION, slide -> slide.title("Conclusion").subtitle("Questions?"))
				.build()
				.toByteArray();

			assertThat(slideCount(presentation)).as("%s", theme).isEqualTo(SlideType.values().size());
			assertThat(texts(presentation)).as("%s", theme)
				.contains("Title", "Section", "Three parts", "Two columns", "Image and text", "Agenda",
					"Statement", "Metrics", "Timeline", "Team", "Quote", "Chart", "Full image", "Conclusion")
				.noneMatch(text -> text.contains("$/"));
		}
	}

	private static InputStream logo() {
		return BusinessReviewServiceTest.class.getResourceAsStream("/logo.png");
	}

	private static int slideCount(byte[] presentation) throws Exception {
		try (XMLSlideShow slideShow = new XMLSlideShow(new ByteArrayInputStream(presentation))) {
			return slideShow.getSlides().size();
		}
	}

	private static List<String> texts(byte[] presentation) throws Exception {
		try (XMLSlideShow slideShow = new XMLSlideShow(new ByteArrayInputStream(presentation))) {
			List<String> texts = new ArrayList<>();
			for (XSLFSlide slide : slideShow.getSlides()) {
				for (XSLFShape shape : slide.getShapes()) {
					if (shape instanceof XSLFTextShape text) {
						texts.add(text.getText());
					}
				}
			}
			return texts;
		}
	}
}
