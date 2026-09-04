/*
 * Vendored from Coreoz PPT-Templates (https://github.com/Coreoz/PPT-Templates),
 * branch master @ fb8b7386a9ad7dce9e139f4a6839c3037a142803, licensed Apache-2.0.
 * Only change: Lombok annotations replaced by plain Java. See NOTICE.
 */
package com.coreoz.ppt;

import java.util.function.Function;

final class PptTextMapper {
	private final Object value;
	private final Function<String, Object> argumentToValue;

	private PptTextMapper(Object value, Function<String, Object> argumentToValue) {
		this.value = value;
		this.argumentToValue = argumentToValue;
	}

	public static PptTextMapper of(Object value, Function<String, Object> argumentToValue) {
		return new PptTextMapper(value, argumentToValue);
	}

	public Object getValue() {
		return value;
	}

	public Function<String, Object> getArgumentToValue() {
		return argumentToValue;
	}
}
