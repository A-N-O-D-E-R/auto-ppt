package io.github.anoder.powerpoint.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.anoder.powerpoint.InvalidSlideException;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.model.SlideImage;
import io.github.anoder.powerpoint.model.SlideModel;

class SlideBuildersTest {

	@Test
	void title_slide_maps_title_and_subtitle() {
		SlideModel model = new TitleSlideBuilder()
			.title("Q3 Business Review")
			.subtitle("September 2026")
			.toModel();

		assertThat(model.type()).isSameAs(SlideType.TITLE);
		assertThat(model.values()).containsExactly(
			entry("title", "Q3 Business Review"),
			entry("subtitle", "September 2026")
		);
	}

	@Test
	void three_parts_slide_maps_the_parts_to_indexed_variables() {
		SlideModel model = new ThreePartsSlideBuilder()
			.title("Our priorities")
			.parts(
				part -> part.title("Growth").text("Expand into new markets"),
				part -> part.title("Efficiency").text("Reduce operational costs"),
				part -> part.title("People").text("Invest in our teams")
			)
			.toModel();

		assertThat(model.values()).containsExactly(
			entry("title", "Our priorities"),
			entry("part0.title", "Growth"),
			entry("part0.text", "Expand into new markets"),
			entry("part1.title", "Efficiency"),
			entry("part1.text", "Reduce operational costs"),
			entry("part2.title", "People"),
			entry("part2.text", "Invest in our teams")
		);
	}

	@Test
	void three_parts_slide_can_be_filled_part_by_part() {
		SlideModel model = new ThreePartsSlideBuilder()
			.part(2, part -> part.title("People"))
			.toModel();

		assertThat(model.values()).containsExactly(entry("part2.title", "People"));
		assertThatThrownBy(() -> new ThreePartsSlideBuilder().part(3, part -> part.title("Too many")))
			.isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	void two_columns_slide_maps_left_and_right() {
		SlideModel model = new TwoColumnsSlideBuilder()
			.title("Before vs After")
			.left(column -> column.title("Before").text("Manual process"))
			.right(column -> column.title("After").text("Automated process"))
			.toModel();

		assertThat(model.values()).containsExactly(
			entry("title", "Before vs After"),
			entry("left.title", "Before"),
			entry("left.text", "Manual process"),
			entry("right.title", "After"),
			entry("right.text", "Automated process")
		);
	}

	@Test
	void image_text_slide_reads_the_image_from_bytes_a_stream_or_a_file(@TempDir Path directory) throws Exception {
		byte[] image = "not-a-real-png".getBytes(StandardCharsets.UTF_8);
		Path file = Files.write(directory.resolve("image.png"), image);

		assertThat(new ImageTextSlideBuilder().image(image).toModel().values())
			.containsExactly(entry("image", new SlideImage(image)));
		assertThat(new ImageTextSlideBuilder().image(new ByteArrayInputStream(image)).toModel().values().get("image"))
			.isInstanceOf(SlideImage.class)
			.extracting(value -> ((SlideImage) value).bytes())
			.isEqualTo(image);
		assertThat(new ImageTextSlideBuilder().image(file).toModel().values().get("image"))
			.isEqualTo(new SlideImage(image));
	}

	@Test
	void section_and_conclusion_slides_map_title_and_subtitle() {
		assertThat(new SectionSlideBuilder().title("Performance").subtitle("Q3 2026").toModel().values())
			.containsExactly(entry("title", "Performance"), entry("subtitle", "Q3 2026"));
		assertThat(new ConclusionSlideBuilder().title("Thank you").toModel().values())
			.containsExactly(entry("title", "Thank you"));
	}

	@Test
	void agenda_slide_maps_the_items_to_indexed_variables() {
		SlideModel model = new AgendaSlideBuilder()
			.title("Agenda")
			.items("The problem", "Our platform", "Traction")
			.toModel();

		assertThat(model.type()).isSameAs(SlideType.AGENDA);
		assertThat(model.values()).containsExactly(
			entry("title", "Agenda"),
			entry("item0", "The problem"),
			entry("item1", "Our platform"),
			entry("item2", "Traction")
		);
		assertThatThrownBy(() -> new AgendaSlideBuilder().item(AgendaSlideBuilder.ITEM_COUNT, "Too many"))
			.isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	void statement_and_quote_slides_map_their_own_variables() {
		assertThat(new StatementSlideBuilder()
			.statement("Biology is programmable")
			.attribution("Our founding bet, 2008")
			.toModel().values())
			.containsExactly(
				entry("statement", "Biology is programmable"),
				entry("attribution", "Our founding bet, 2008")
			);
		assertThat(new QuoteSlideBuilder()
			.quote("It cut our sequencing costs in half.")
			.author("Dana Okonkwo")
			.role("Head of Research, Northwind Bio")
			.toModel().values())
			.containsExactly(
				entry("quote", "It cut our sequencing costs in half."),
				entry("author", "Dana Okonkwo"),
				entry("role", "Head of Research, Northwind Bio")
			);
	}

	@Test
	void metrics_slide_maps_the_figures_to_indexed_variables() {
		SlideModel model = new MetricsSlideBuilder()
			.title("Traction")
			.metrics(
				metric -> metric.value("$1.2B").label("Annual recurring revenue"),
				metric -> metric.value("+42%").label("Year over year growth")
			)
			.toModel();

		assertThat(model.values()).containsExactly(
			entry("title", "Traction"),
			entry("metric0.value", "$1.2B"),
			entry("metric0.label", "Annual recurring revenue"),
			entry("metric1.value", "+42%"),
			entry("metric1.label", "Year over year growth")
		);
	}

	@Test
	void timeline_slide_maps_the_milestones_to_indexed_variables() {
		SlideModel model = new TimelineSlideBuilder()
			.title("Roadmap")
			.milestones(
				milestone -> milestone.date("Q1 2026").text("Pilot with three customers"),
				milestone -> milestone.date("Q3 2026").text("General availability")
			)
			.toModel();

		assertThat(model.values()).containsExactly(
			entry("title", "Roadmap"),
			entry("milestone0.date", "Q1 2026"),
			entry("milestone0.text", "Pilot with three customers"),
			entry("milestone1.date", "Q3 2026"),
			entry("milestone1.text", "General availability")
		);
	}

	@Test
	void team_slide_maps_the_people_and_reads_their_photo() {
		byte[] photo = "not-a-real-png".getBytes(StandardCharsets.UTF_8);

		SlideModel model = new TeamSlideBuilder()
			.title("Leadership")
			.people(
				person -> person.name("Dana Okonkwo").role("Chief Executive Officer").photo(photo),
				person -> person.name("Ravi Menon").role("Chief Technology Officer")
			)
			.toModel();

		assertThat(model.values()).containsExactly(
			entry("title", "Leadership"),
			entry("person0.name", "Dana Okonkwo"),
			entry("person0.role", "Chief Executive Officer"),
			entry("person0.photo", new SlideImage(photo)),
			entry("person1.name", "Ravi Menon"),
			entry("person1.role", "Chief Technology Officer")
		);
	}

	@Test
	void chart_and_full_image_slides_read_their_image_from_bytes_a_stream_or_a_file(@TempDir Path directory)
			throws Exception {
		byte[] image = "not-a-real-png".getBytes(StandardCharsets.UTF_8);
		Path file = Files.write(directory.resolve("chart.png"), image);

		assertThat(new ChartSlideBuilder().title("Revenue growth").takeaway("Doubling every year")
			.chart(new ByteArrayInputStream(image)).toModel().values())
			.containsExactly(
				entry("title", "Revenue growth"),
				entry("takeaway", "Doubling every year"),
				entry("chart", new SlideImage(image))
			);
		assertThat(new FullImageSlideBuilder().headline("The foundry").image(file).toModel().values())
			.containsExactly(entry("headline", "The foundry"), entry("image", new SlideImage(image)));
		assertThat(new FullImageSlideBuilder().image(image).toModel().values())
			.containsExactly(entry("image", new SlideImage(image)));
	}

	@Test
	void an_unreadable_image_file_names_the_slide_and_the_file(@TempDir Path directory) {
		Path missing = directory.resolve("missing.png");

		assertThatThrownBy(() -> new ChartSlideBuilder().chart(missing))
			.isInstanceOf(InvalidSlideException.class)
			.hasMessageContaining("chart")
			.hasMessageContaining(missing.toString())
			.hasMessageContaining("CHART");
	}

	@Test
	void the_model_is_immutable_and_detached_from_the_builder() {
		TitleSlideBuilder builder = new TitleSlideBuilder().title("First");
		SlideModel model = builder.toModel();
		builder.title("Second");

		assertThat(model.values()).containsExactly(entry("title", "First"));
		assertThatThrownBy(() -> model.values().put("title", "Third"))
			.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void a_null_value_is_rejected_with_the_variable_name() {
		assertThatThrownBy(() -> new TitleSlideBuilder().title(null))
			.isInstanceOf(NullPointerException.class)
			.hasMessageContaining("title");
	}
}
