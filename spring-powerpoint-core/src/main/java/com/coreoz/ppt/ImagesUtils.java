/*
 * Vendored from Coreoz PPT-Templates (https://github.com/Coreoz/PPT-Templates),
 * branch master @ fb8b7386a9ad7dce9e139f4a6839c3037a142803, licensed Apache-2.0.
 * Only change: Lombok annotations replaced by plain Java. See NOTICE.
 */
package com.coreoz.ppt;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;

public class ImagesUtils {

	private static final Logger logger = LoggerFactory.getLogger(ImagesUtils.class);

	// resizing

	static byte[] resizeCrop(byte[] imageData, String targetFormat, int width, int height,
			float qualityFactor, double qualityMultiplicator) {
		return resize(imageData, targetFormat, width, height, true, qualityFactor, qualityMultiplicator);
	}

	static byte[] resizeOnly(byte[] imageData, String targetFormat, int width, int height,
			float qualityFactor, double qualityMultiplicator) {
		return resize(imageData, targetFormat, width, height, false, qualityFactor, qualityMultiplicator);
	}

	private static byte[] resize(byte[] imageData, String targetFormat, int width, int height,
			boolean crop, float qualityFactor, double qualityMultiplicator) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Builder<? extends InputStream> builder = Thumbnails
			.of(new ByteArrayInputStream(imageData))
			.outputQuality(qualityFactor)
			.size(
				(int) Math.round(width * qualityMultiplicator),
				(int) Math.round(height * qualityMultiplicator)
			);

		if(crop) {
			builder.crop(Positions.CENTER);
		}

		try {
			builder
				.outputFormat(targetFormat)
				.toOutputStream(byteArrayOutputStream);
		} catch (IOException e) {
			logger.error("Cannot resize image to format {}", targetFormat, e);
			return null;
		}

		return byteArrayOutputStream.toByteArray();
	}

	// image size

	static Dimension imageDimension(byte[] pictureData, double qualityMultiplicator) {
		try {
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(pictureData));
			return new Dimension(
				(int) Math.round(bufferedImage.getWidth() / qualityMultiplicator),
				(int) Math.round(bufferedImage.getHeight() / qualityMultiplicator)
			);
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot read the image dimensions", e);
		}
	}

	// image mime type

	public static PictureType guessPictureType(byte[] pictureData) {
		for(ImageType imageType : ImageType.values()) {
			if(startsWith(pictureData, imageType.startPattern)) {
				return imageType.poiType;
			}
		}

		return null;
	}

	private enum ImageType {
		PNG(PictureType.PNG, new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A }),
		GIF(PictureType.GIF, new byte[] { (byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38 }),
		JPEG(PictureType.JPEG, new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF }),
		BMP(PictureType.BMP, new byte[] { (byte) 0x42, (byte) 0x4D }),
		;

		private final PictureType poiType;
		private final byte[] startPattern;

		ImageType(PictureType poiType, byte[] startPattern) {
			this.poiType = poiType;
			this.startPattern = startPattern;
		}
	}

	private static boolean startsWith(byte[] source, byte[] match) {
		if(match.length > source.length) {
			return false;
		}

		for(int i=0; i<match.length; i++) {
			if(source[i] != match[i]) {
				return false;
			}
		}

		return true;
	}

}
