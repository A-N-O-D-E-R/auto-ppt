package io.github.anoder.powerpoint.dsl;

import java.util.List;
import java.util.Objects;

import io.github.anoder.powerpoint.SlideType;

/**
 * Slide listing what the presentation covers, as up to {@value #ITEM_COUNT} numbered items.
 *
 * <p>Items left unset are replaced by an empty text, so a shorter agenda is legitimate.
 *
 * {@snippet :
 * presentation.add(SlideType.AGENDA, slide -> slide
 *     .title("Agenda")
 *     .items("The problem", "Our platform", "Traction", "The team"));
 * }
 */
public final class AgendaSlideBuilder extends AbstractSlideBuilder<AgendaSlideBuilder> {

	/** Number of items the template has room for. */
	public static final int ITEM_COUNT = 6;

	public AgendaSlideBuilder() {
		super(SlideType.AGENDA);
	}

	public AgendaSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	/**
	 * Sets the items in order, from the first one.
	 *
	 * @throws IndexOutOfBoundsException if more than {@value #ITEM_COUNT} items are given
	 */
	public AgendaSlideBuilder items(String... items) {
		return items(List.of(Objects.requireNonNull(items, "items")));
	}

	/**
	 * Sets the items in order, from the first one.
	 *
	 * @throws IndexOutOfBoundsException if more than {@value #ITEM_COUNT} items are given
	 */
	public AgendaSlideBuilder items(List<String> items) {
		Objects.requireNonNull(items, "items");
		for (int index = 0; index < items.size(); index++) {
			item(index, items.get(index));
		}
		return this;
	}

	/**
	 * Sets a single item, which is handy to leave holes or to fill the agenda from optional data.
	 *
	 * @param index zero based item index, from {@code 0} to {@value #ITEM_COUNT} minus one
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public AgendaSlideBuilder item(int index, String value) {
		Objects.checkIndex(index, ITEM_COUNT);
		put("item" + index, value);
		return this;
	}
}
