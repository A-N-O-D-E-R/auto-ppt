package io.github.anoder.powerpoint.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.github.anoder.powerpoint.SlideType;

/**
 * The immutable outcome of a slide builder: a slide type plus the template variable values.
 *
 * <p>The keys are Coreoz template variable names such as {@code part0.title}. They are an
 * implementation detail produced by the builders and consumed by the renderer; consumers of the
 * library never see them.
 *
 * @param type   the slide type, which selects the template
 * @param values template variable name to value, either a {@link String}, a {@link Number} or a
 *               {@link SlideImage}
 */
public record SlideModel(SlideType<?> type, Map<String, Object> values) {

	public SlideModel {
		Objects.requireNonNull(type, "type");
		values = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(values, "values")));
	}
}
