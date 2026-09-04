/*
 * Vendored from Coreoz PPT-Templates (https://github.com/Coreoz/PPT-Templates),
 * branch master @ fb8b7386a9ad7dce9e139f4a6839c3037a142803, licensed Apache-2.0.
 * Only change: Lombok annotations replaced by plain Java. See NOTICE.
 */
package com.coreoz.ppt;


/**
 * Define how a new image should be resized to replace an existing image in a PPT.
 * Resized images will always be placed in the top left corner
 * of the original image placeholder frame.
 */
public enum PptImageReplacementMode {
	/**
	 * The new image will be resized the best fit the existing image frame,
	 * then the new image will be cropped from its center to fit exactly the original image frame.
	 */
	RESIZE_CROP(ImagesUtils::resizeCrop),
	/**
	 * The new image will be resized the best fit the existing image frame,
	 * but no cropping will be applied: that means that the new image
	 * will very likely overstep the original image frame.
	 */
	RESIZE_ONLY(ImagesUtils::resizeOnly),
	;

	private final ResizeFunction resizeFunction;

	PptImageReplacementMode(ResizeFunction resizeFunction) {
		this.resizeFunction = resizeFunction;
	}

	byte[] resize(byte[] imageData, String targetFormat, int width, int height,
			float qualityFactor, double qualityMultiplicator) {
		return resizeFunction.resizeImage(imageData, targetFormat, width, height, qualityFactor, qualityMultiplicator);
	}

	@FunctionalInterface
	private interface ResizeFunction {
		byte[] resizeImage(
			byte[] imageData,
			String targetFormat,
			int width,
			int height,
			float qualityFactor,
			double qualityMultiplicator
		);
	}

}
