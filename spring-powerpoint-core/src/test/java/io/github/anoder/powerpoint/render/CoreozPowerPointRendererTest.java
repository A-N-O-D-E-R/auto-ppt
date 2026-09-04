package io.github.anoder.powerpoint.render;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.anoder.powerpoint.InvalidSlideException;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.TemplateNotFoundException;
import io.github.anoder.powerpoint.TestTemplates;
import io.github.anoder.powerpoint.Theme;
import io.github.anoder.powerpoint.dsl.AgendaSlideBuilder;
import io.github.anoder.powerpoint.dsl.ChartSlideBuilder;
import io.github.anoder.powerpoint.dsl.ConclusionSlideBuilder;
import io.github.anoder.powerpoint.dsl.FullImageSlideBuilder;
import io.github.anoder.powerpoint.dsl.ImageTextSlideBuilder;
import io.github.anoder.powerpoint.dsl.MetricsSlideBuilder;
import io.github.anoder.powerpoint.dsl.QuoteSlideBuilder;
import io.github.anoder.powerpoint.dsl.SectionSlideBuilder;
import io.github.anoder.powerpoint.dsl.SlideBuilder;
import io.github.anoder.powerpoint.dsl.StatementSlideBuilder;
import io.github.anoder.powerpoint.dsl.TeamSlideBuilder;
import io.github.anoder.powerpoint.dsl.ThreePartsSlideBuilder;
import io.github.anoder.powerpoint.dsl.TimelineSlideBuilder;
import io.github.anoder.powerpoint.dsl.TitleSlideBuilder;
import io.github.anoder.powerpoint.dsl.TwoColumnsSlideBuilder;
import io.github.anoder.powerpoint.model.SlideModel;
import io.github.anoder.powerpoint.template.ClasspathTemplateRepository;

class CoreozPowerPointRendererTest {

	private final CoreozPowerPointRenderer renderer =
		new CoreozPowerPointRenderer(new ClasspathTemplateRepository(), new PoiPresentationAssembler());

	@BeforeAll
	static void generateTemplates() {
		TestTemplates.generate();
	}

	@Test
	void renders_several_slide_types_into_one_presentation() throws IOException {
		byte[] presentation = render(Theme.CORPORATE, List.of(
			new TitleSlideBuilder().title("Q3 Business Review").subtitle("September 2026").toModel(),
			new ThreePartsSlideBuilder()
				.title("Key achievements")
				.parts(
					part -> part.title("Revenue").text("+24%"),
					part -> part.title("Customers").text("+18%"),
					part -> part.title("Margin").text("+4 pts")
				)
				.toModel(),
			new TwoColumnsSlideBuilder()
				.title("Before vs After")
				.left(column -> column.title("Before").text("Manual process"))
				.right(column -> column.title("After").text("Automated process"))
				.toModel(),
			new ConclusionSlideBuilder().title("Thank you").toModel()
		));

		assertThat(TestTemplates.slideCount(presentation)).isEqualTo(4);
		assertThat(TestTemplates.textOf(presentation))
			.contains("Q3 Business Review", "September 2026", "Key achievements", "Revenue", "+24%",
				"Margin", "Before vs After", "Automated process", "Thank you")
			.noneMatch(text -> text.contains("$/"));
	}

	@Test
	void replaces_the_placeholder_image() throws IOException {
		byte[] presentation = render(Theme.MODERN, List.of(
			new ImageTextSlideBuilder()
				.title("Our new office")
				.text("Opened in September 2026")
				.image(png(new Color(0x00, 0x80, 0x40)))
				.toModel()
		));

		assertThat(TestTemplates.slideCount(presentation)).isEqualTo(1);
		assertThat(TestTemplates.textOf(presentation)).contains("Our new office", "Opened in September 2026");
	}

	@Test
	void renders_every_theme() throws IOException {
		for (Theme theme : Theme.values()) {
			byte[] presentation = render(theme, List.of(
				new TitleSlideBuilder().title("Hello").subtitle("World").toModel()
			));

			assertThat(TestTemplates.textOf(presentation)).as("%s", theme).contains("Hello", "World");
		}
	}

	@Test
	void renders_every_built_in_slide_type_in_every_theme() throws IOException {
		List<SlideModel> deck = List.of(
			new TitleSlideBuilder().title("Ginkgo").subtitle("Investor update").toModel(),
			new SectionSlideBuilder().title("The company").subtitle("2026").toModel(),
			new AgendaSlideBuilder().title("Agenda").items("The problem", "Our platform", "Traction").toModel(),
			new ThreePartsSlideBuilder().title("Our priorities").part(0, part -> part.title("Growth")).toModel(),
			new TwoColumnsSlideBuilder().title("Before vs After").left(column -> column.text("Manual")).toModel(),
			new ImageTextSlideBuilder().title("The foundry").text("Automated biology").image(png(Color.GRAY)).toModel(),
			new StatementSlideBuilder().statement("Biology is programmable").attribution("2008").toModel(),
			new MetricsSlideBuilder().title("Traction")
				.metrics(metric -> metric.value("$1.2B").label("Revenue")).toModel(),
			new TimelineSlideBuilder().title("Roadmap")
				.milestones(milestone -> milestone.date("Q1 2026").text("Pilot")).toModel(),
			new TeamSlideBuilder().title("Leadership")
				.people(person -> person.name("Dana Okonkwo").role("CEO").photo(png(Color.BLUE))).toModel(),
			new QuoteSlideBuilder().quote("Half the cost.").author("Dana Okonkwo").role("CEO").toModel(),
			new ChartSlideBuilder().title("Revenue").chart(png(Color.RED)).takeaway("Doubling yearly").toModel(),
			new FullImageSlideBuilder().image(png(Color.GREEN)).headline("The foundry").subheadline("2026").toModel(),
			new ConclusionSlideBuilder().title("Thank you").toModel()
		);
		assertThat(deck).as("one slide per built-in type").hasSameSizeAs(SlideType.values());

		for (Theme theme : Theme.values()) {
			byte[] presentation = render(theme, deck);

			assertThat(TestTemplates.slideCount(presentation)).as("%s", theme).isEqualTo(deck.size());
			assertThat(TestTemplates.textOf(presentation)).as("%s", theme)
				.contains("Ginkgo", "The problem", "Biology is programmable", "$1.2B", "Q1 2026",
					"Dana Okonkwo", "Half the cost.", "Doubling yearly", "The foundry", "Thank you")
				.noneMatch(text -> text.contains("$/"));
		}
	}

	@Test
	void an_unset_image_variable_removes_the_placeholder_picture() throws IOException {
		byte[] withPhotos = render(Theme.CORPORATE, List.of(
			new TeamSlideBuilder()
				.people(
					person -> person.name("Dana Okonkwo").photo(png(Color.BLUE)),
					person -> person.name("Ravi Menon").photo(png(Color.BLUE))
				)
				.toModel()
		));
		byte[] withoutPhotos = render(Theme.CORPORATE, List.of(
			new TeamSlideBuilder().people(person -> person.name("Dana Okonkwo")).toModel()
		));

		// the template has one placeholder per person; only the ones given a photo survive
		assertThat(TestTemplates.pictureCount(withPhotos)).isEqualTo(2);
		assertThat(TestTemplates.pictureCount(withoutPhotos)).isZero();
	}

	@Test
	void an_unset_template_variable_is_replaced_by_an_empty_text() throws IOException {
		byte[] presentation = render(Theme.MINIMAL, List.of(
			new ConclusionSlideBuilder().title("Thank you").toModel()
		));

		assertThat(TestTemplates.textOf(presentation))
			.contains("Thank you", "")
			.noneMatch(text -> text.contains("subtitle"));
	}

	@Test
	void a_value_without_variable_in_the_template_fails_with_the_full_context() {
		SlideModel invalid = new SlideModel(SlideType.TITLE, Map.of("headline", "Not in the template"));

		assertThatThrownBy(() -> render(Theme.CORPORATE, List.of(invalid)))
			.isInstanceOf(InvalidSlideException.class)
			.hasMessageContaining("[headline]")
			.hasMessageContaining("Theme: CORPORATE")
			.hasMessageContaining("slide type: TITLE")
			.hasMessageContaining("/powerpoint/corporate/title.pptx");
	}

	@Test
	void an_unsupported_value_type_fails() {
		SlideModel invalid = new SlideModel(SlideType.TITLE, Map.of("title", List.of("not", "a", "text")));

		assertThatThrownBy(() -> render(Theme.CORPORATE, List.of(invalid)))
			.isInstanceOf(InvalidSlideException.class)
			.hasMessageContaining("Unsupported value type");
	}

	@Test
	void a_missing_template_fails_with_the_resolved_path() {
		SlideType<UnusedBuilder> pricing = SlideType.of("PRICING", "pricing");
		SlideModel slide = new SlideModel(pricing, Map.of());

		assertThatThrownBy(() -> render(Theme.CORPORATE, List.of(slide)))
			.isInstanceOf(TemplateNotFoundException.class)
			.hasMessageContaining("/powerpoint/corporate/pricing.pptx");
	}

	@Test
	void an_empty_presentation_fails() {
		assertThatThrownBy(() -> render(Theme.CORPORATE, List.of()))
			.isInstanceOf(InvalidSlideException.class)
			.hasMessageContaining("at least one slide");
	}

	private byte[] render(Theme theme, List<SlideModel> slides) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		renderer.render(theme, slides, output);
		return output.toByteArray();
	}

	/** Unchecked, so that it can be called from the builder lambdas. */
	private static byte[] png(Color color) {
		BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
		var graphics = image.createGraphics();
		graphics.setColor(color);
		graphics.fillRect(0, 0, 200, 150);
		graphics.dispose();
		ByteArrayOutputStream png = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", png);
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot generate the test image", e);
		}
		return png.toByteArray();
	}

	/** Only used to give {@link SlideType#of} a builder type. */
	private interface UnusedBuilder extends SlideBuilder<UnusedBuilder> {
	}
}
