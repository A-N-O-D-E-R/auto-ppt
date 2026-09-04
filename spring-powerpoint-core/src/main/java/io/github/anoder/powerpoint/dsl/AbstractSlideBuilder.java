package io.github.anoder.powerpoint.dsl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.model.SlideModel;

/**
 * Base class of the slide builders: it holds the template variable values and turns them into an
 * immutable {@link SlideModel}.
 *
 * <p>Extend it to support a custom slide type; the template variable names stay confined to the
 * subclass, the public methods only speak of PowerPoint concepts.
 *
 * @param <S> the concrete builder type
 */
public abstract class AbstractSlideBuilder<S extends SlideBuilder<S>> implements SlideBuilder<S> {

	private final SlideType<S> type;
	private final Map<String, Object> values = new LinkedHashMap<>();

	protected AbstractSlideBuilder(SlideType<S> type) {
		this.type = Objects.requireNonNull(type, "type");
	}

	@Override
	public final SlideType<S> type() {
		return type;
	}

	/**
	 * Sets the value of a template variable, e.g. {@code part0.title}.
	 */
	protected final void put(String variable, Object value) {
		Objects.requireNonNull(variable, "variable");
		values.put(variable, Objects.requireNonNull(value, () -> "The value of '" + variable + "' must not be null"));
	}

	@Override
	public final SlideModel toModel() {
		return new SlideModel(type, values);
	}

	@Override
	public String toString() {
		return type + values.keySet().toString();
	}
}
