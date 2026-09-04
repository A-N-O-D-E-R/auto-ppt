package io.github.anoder.powerpoint.render;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.anoder.powerpoint.InvalidSlideException;
import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.TestTemplates;
import io.github.anoder.powerpoint.Theme;
import io.github.anoder.powerpoint.model.RenderedSlide;
import io.github.anoder.powerpoint.template.ClasspathTemplateRepository;
import io.github.anoder.powerpoint.template.TemplateRepository;

class PoiPresentationAssemblerTest {

	private final TemplateRepository templates = new ClasspathTemplateRepository();
	private final PoiPresentationAssembler assembler = new PoiPresentationAssembler();

	@BeforeAll
	static void generateTemplates() {
		TestTemplates.generate();
	}

	@Test
	void merges_the_slides_in_order_keeping_their_content() throws IOException {
		byte[] presentation = assemble(List.of(
			slide(SlideType.TITLE),
			slide(SlideType.THREE_PARTS),
			slide(SlideType.THREE_PARTS),
			slide(SlideType.CONCLUSION)
		));

		assertThat(TestTemplates.slideCount(presentation)).isEqualTo(4);
		assertThat(TestTemplates.textOf(presentation)).contains("$/title/", "$/part0.title/", "$/subtitle/");
	}

	@Test
	void a_single_slide_is_returned_as_is() throws IOException {
		byte[] presentation = assemble(List.of(slide(SlideType.SECTION)));

		assertThat(TestTemplates.slideCount(presentation)).isEqualTo(1);
	}

	@Test
	void an_empty_list_fails() {
		assertThatThrownBy(() -> assemble(List.of()))
			.isInstanceOf(InvalidSlideException.class)
			.hasMessageContaining("at least one slide");
	}

	private byte[] assemble(List<RenderedSlide> slides) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assembler.assemble(slides, output);
		return output.toByteArray();
	}

	/** Assembles the raw templates: the assembler does not care whether variables were replaced. */
	private RenderedSlide slide(SlideType<?> type) throws IOException {
		try (var template = templates.get(Theme.CORPORATE, type)) {
			return new RenderedSlide(type, template.readAllBytes());
		}
	}
}
