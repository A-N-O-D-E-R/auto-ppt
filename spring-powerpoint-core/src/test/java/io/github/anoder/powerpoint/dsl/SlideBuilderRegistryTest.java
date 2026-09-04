package io.github.anoder.powerpoint.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.UnsupportedSlideTypeException;

class SlideBuilderRegistryTest {

	private final SlideBuilderRegistry registry = SlideBuilderRegistry.withBuiltIns();

	@Test
	void creates_the_builder_bound_to_each_built_in_slide_type() {
		assertThat(registry.types()).containsExactlyInAnyOrderElementsOf(SlideType.values());
		assertThat(registry.create(SlideType.TITLE)).isInstanceOf(TitleSlideBuilder.class);
		assertThat(registry.create(SlideType.THREE_PARTS)).isInstanceOf(ThreePartsSlideBuilder.class);
		assertThat(registry.create(SlideType.TWO_COLUMNS)).isInstanceOf(TwoColumnsSlideBuilder.class);
		assertThat(registry.create(SlideType.IMAGE_TEXT)).isInstanceOf(ImageTextSlideBuilder.class);
		assertThat(registry.create(SlideType.SECTION)).isInstanceOf(SectionSlideBuilder.class);
		assertThat(registry.create(SlideType.CONCLUSION)).isInstanceOf(ConclusionSlideBuilder.class);
	}

	@Test
	void creates_a_new_builder_on_every_call() {
		assertThat(registry.create(SlideType.TITLE)).isNotSameAs(registry.create(SlideType.TITLE));
	}

	@Test
	void fails_when_no_factory_is_registered_for_a_slide_type() {
		SlideBuilderRegistry titleOnly = new SlideBuilderRegistry(
			List.of(new DefaultSlideBuilderFactory<>(SlideType.TITLE, TitleSlideBuilder::new))
		);

		assertThatThrownBy(() -> titleOnly.create(SlideType.SECTION))
			.isInstanceOf(UnsupportedSlideTypeException.class)
			.hasMessageContaining("SECTION")
			.hasMessageContaining("Registered types: TITLE");
	}

	@Test
	void the_last_factory_of_a_slide_type_wins_so_that_an_application_can_override_it() {
		SlideBuilderRegistry overridden = new SlideBuilderRegistry(List.of(
			new DefaultSlideBuilderFactory<>(SlideType.TITLE, TitleSlideBuilder::new),
			new DefaultSlideBuilderFactory<>(SlideType.TITLE, () -> new TitleSlideBuilder().subtitle("default subtitle"))
		));

		assertThat(overridden.create(SlideType.TITLE).toModel().values()).containsKey("subtitle");
	}
}
