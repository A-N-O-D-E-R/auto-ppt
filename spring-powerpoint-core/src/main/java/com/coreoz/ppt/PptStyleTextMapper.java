/*
 * Vendored from Coreoz PPT-Templates (https://github.com/Coreoz/PPT-Templates),
 * branch master @ fb8b7386a9ad7dce9e139f4a6839c3037a142803, licensed Apache-2.0.
 * Only change: Lombok annotations replaced by plain Java. See NOTICE.
 */
package com.coreoz.ppt;

import java.util.function.BiConsumer;

import org.apache.poi.sl.usermodel.TextRun;

final class PptStyleTextMapper {
	private final BiConsumer<String, TextRun> applyStyle;

	private PptStyleTextMapper(BiConsumer<String, TextRun> applyStyle) {
		this.applyStyle = applyStyle;
	}

	public static PptStyleTextMapper of(BiConsumer<String, TextRun> applyStyle) {
		return new PptStyleTextMapper(applyStyle);
	}

	public BiConsumer<String, TextRun> getApplyStyle() {
		return applyStyle;
	}
}
