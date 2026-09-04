/*
 * Vendored from Coreoz PPT-Templates (https://github.com/Coreoz/PPT-Templates),
 * branch master @ fb8b7386a9ad7dce9e139f4a6839c3037a142803, licensed Apache-2.0.
 * Only change: Lombok annotations replaced by plain Java. See NOTICE.
 */
package com.coreoz.ppt;

import java.util.function.BiConsumer;

import org.apache.poi.xslf.usermodel.XSLFSimpleShape;

final class PptStyleShapeMapper {
	private final BiConsumer<String, XSLFSimpleShape> applyStyle;

	private PptStyleShapeMapper(BiConsumer<String, XSLFSimpleShape> applyStyle) {
		this.applyStyle = applyStyle;
	}

	public static PptStyleShapeMapper of(BiConsumer<String, XSLFSimpleShape> applyStyle) {
		return new PptStyleShapeMapper(applyStyle);
	}

	public BiConsumer<String, XSLFSimpleShape> getApplyStyle() {
		return applyStyle;
	}
}
