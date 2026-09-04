package io.github.anoder.powerpoint.dsl;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.anoder.powerpoint.SlideType;

/**
 * Slide introducing up to {@value #PERSON_COUNT} people, each with a name, a role and an optional photo.
 *
 * <p>A person left unset produces empty texts, and a photo left unset removes the placeholder picture
 * instead of showing it.
 *
 * {@snippet :
 * presentation.add(SlideType.TEAM, slide -> slide
 *     .title("Leadership")
 *     .people(
 *         person -> person.name("Dana Okonkwo").role("Chief Executive Officer").photo(Path.of("dana.png")),
 *         person -> person.name("Ravi Menon").role("Chief Technology Officer")));
 * }
 */
public final class TeamSlideBuilder extends AbstractSlideBuilder<TeamSlideBuilder> {

	/** Number of people the template has room for. */
	public static final int PERSON_COUNT = 4;

	public TeamSlideBuilder() {
		super(SlideType.TEAM);
	}

	public TeamSlideBuilder title(String value) {
		put("title", value);
		return this;
	}

	/**
	 * Configures the people in order, from the first one.
	 */
	@SafeVarargs
	public final TeamSlideBuilder people(Consumer<PersonBuilder>... people) {
		Objects.requireNonNull(people, "people");
		for (int index = 0; index < people.length; index++) {
			person(index, people[index]);
		}
		return this;
	}

	/**
	 * Configures a single person.
	 *
	 * @param index zero based index, from {@code 0} to {@value #PERSON_COUNT} minus one
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public TeamSlideBuilder person(int index, Consumer<PersonBuilder> person) {
		Objects.checkIndex(index, PERSON_COUNT);
		Objects.requireNonNull(person, "person").accept(new SlideBlock(this, "person" + index));
		return this;
	}
}
