/*
 * Vendored from Coreoz PPT-Templates (https://github.com/Coreoz/PPT-Templates),
 * branch master @ fb8b7386a9ad7dce9e139f4a6839c3037a142803, licensed Apache-2.0.
 * Only change: Lombok annotations replaced by plain Java. See NOTICE.
 */
package com.coreoz.ppt;

import org.apache.poi.sl.usermodel.PictureData.PictureType;

final class PptImageMapper {
	public static final float DEFAULT_QUALITY_FACTOR = 1F;
	public static final double DEFAULT_QUALITY_MULTIPLICATOR = 2.0;

	private final PictureType targetFormat;
	private final PptImageReplacementMode replacementMode;
	private final byte[] value;
	private final float qualityFactory;
	private final double qualityMultiplicator;

	private PptImageMapper(PictureType targetFormat, PptImageReplacementMode replacementMode, byte[] value,
			float qualityFactory, double qualityMultiplicator) {
		this.targetFormat = targetFormat;
		this.replacementMode = replacementMode;
		this.value = value;
		this.qualityFactory = qualityFactory;
		this.qualityMultiplicator = qualityMultiplicator;
	}

	public static PptImageMapper of(PictureType targetFormat, PptImageReplacementMode replacementMode, byte[] value,
			float qualityFactory, double qualityMultiplicator) {
		return new PptImageMapper(targetFormat, replacementMode, value, qualityFactory, qualityMultiplicator);
	}

	public PictureType getTargetFormat() {
		return targetFormat;
	}

	public PptImageReplacementMode getReplacementMode() {
		return replacementMode;
	}

	public byte[] getValue() {
		return value;
	}

	public float getQualityFactory() {
		return qualityFactory;
	}

	public double getQualityMultiplicator() {
		return qualityMultiplicator;
	}
}
