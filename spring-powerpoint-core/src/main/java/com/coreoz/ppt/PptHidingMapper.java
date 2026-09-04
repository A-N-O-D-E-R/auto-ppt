/*
 * Vendored from Coreoz PPT-Templates (https://github.com/Coreoz/PPT-Templates),
 * branch master @ fb8b7386a9ad7dce9e139f4a6839c3037a142803, licensed Apache-2.0.
 * Only change: Lombok annotations replaced by plain Java. See NOTICE.
 */
package com.coreoz.ppt;

import java.util.function.Predicate;

final class PptHidingMapper {
	private final Predicate<String> shouldHide;

	private PptHidingMapper(Predicate<String> shouldHide) {
		this.shouldHide = shouldHide;
	}

	public static PptHidingMapper of(Predicate<String> shouldHide) {
		return new PptHidingMapper(shouldHide);
	}

	public Predicate<String> getShouldHide() {
		return shouldHide;
	}
}
