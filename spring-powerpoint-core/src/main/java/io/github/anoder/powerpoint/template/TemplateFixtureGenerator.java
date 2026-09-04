package io.github.anoder.powerpoint.template;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import io.github.anoder.powerpoint.SlideType;
import io.github.anoder.powerpoint.Theme;

/**
 * Generates starter {@code .pptx} templates for every theme and slide type.
 *
 * <p>Templates are normally designed in PowerPoint; this generator exists so that a project can start
 * with a working, self-documenting set of templates, and so that the tests of this library do not
 * depend on binary fixtures. Regenerate them with:
 *
 * <pre>{@code
 * mvn -pl spring-powerpoint-core exec:java \
 *     -Dexec.mainClass=io.github.anoder.powerpoint.template.TemplateFixtureGenerator \
 *     -Dexec.args=src/main/resources/powerpoint
 * }</pre>
 *
 * <p>Each template declares its variables the way Coreoz reads them: {@code $/name/} inside text boxes,
 * and {@code $/image/} as the hyperlink of the placeholder picture.
 */
public final class TemplateFixtureGenerator {

	/** Slide size in points, 16:9. */
	private static final Dimension SLIDE_SIZE = new Dimension(960, 540);

	private TemplateFixtureGenerator() {
	}

	/**
	 * Writes {@code {outputDirectory}/{theme}/{slide-type}.pptx} for every theme and slide type.
	 *
	 * @param args optionally the output directory, {@code target/powerpoint} by default
	 */
	public static void main(String... args) throws IOException {
		Path outputDirectory = Path.of(args.length > 0 ? args[0] : "target/powerpoint");
		writeAll(outputDirectory);
		System.out.println("Templates generated in " + outputDirectory.toAbsolutePath());
	}

	/**
	 * Writes the templates of every theme and slide type under the given directory.
	 */
	public static void writeAll(Path outputDirectory) throws IOException {
		for (Theme theme : Theme.values()) {
			Path themeDirectory = outputDirectory.resolve(theme.folder());
			Files.createDirectories(themeDirectory);
			for (SlideType<?> slideType : SlideType.values()) {
				try (OutputStream output = Files.newOutputStream(themeDirectory.resolve(slideType.templateName() + ".pptx"))) {
					write(theme, slideType, output);
				}
			}
		}
	}

	/**
	 * Writes a single template.
	 */
	public static void write(Theme theme, SlideType<?> slideType, OutputStream output) throws IOException {
		Style style = Style.of(theme);
		try (XMLSlideShow presentation = new XMLSlideShow()) {
			presentation.setPageSize(SLIDE_SIZE);
			XSLFSlide slide = presentation.createSlide();
			switch (slideType.templateName()) {
				case "title" -> {
					text(slide, style, "$/title/", 60, 170, 840, 100, 40, true, style.title(), TextAlign.LEFT);
					text(slide, style, "$/subtitle/", 60, 280, 840, 60, 20, false, style.body(), TextAlign.LEFT);
				}
				case "section" -> {
					text(slide, style, "$/title/", 60, 200, 840, 90, 34, true, style.accent(), TextAlign.LEFT);
					text(slide, style, "$/subtitle/", 60, 295, 840, 50, 18, false, style.body(), TextAlign.LEFT);
				}
				case "conclusion" -> {
					text(slide, style, "$/title/", 60, 190, 840, 100, 40, true, style.title(), TextAlign.CENTER);
					text(slide, style, "$/subtitle/", 60, 300, 840, 60, 20, false, style.body(), TextAlign.CENTER);
				}
				case "three-parts" -> {
					text(slide, style, "$/title/", 60, 50, 840, 70, 30, true, style.title(), TextAlign.LEFT);
					int[] columns = { 60, 350, 640 };
					for (int part = 0; part < columns.length; part++) {
						text(slide, style, "$/part" + part + ".title/", columns[part], 170, 260, 45, 20, true, style.accent(), TextAlign.LEFT);
						text(slide, style, "$/part" + part + ".text/", columns[part], 220, 260, 160, 14, false, style.body(), TextAlign.LEFT);
					}
				}
				case "two-columns" -> {
					text(slide, style, "$/title/", 60, 50, 840, 70, 30, true, style.title(), TextAlign.LEFT);
					text(slide, style, "$/left.title/", 60, 170, 380, 45, 22, true, style.accent(), TextAlign.LEFT);
					text(slide, style, "$/left.text/", 60, 220, 380, 180, 15, false, style.body(), TextAlign.LEFT);
					text(slide, style, "$/right.title/", 520, 170, 380, 45, 22, true, style.accent(), TextAlign.LEFT);
					text(slide, style, "$/right.text/", 520, 220, 380, 180, 15, false, style.body(), TextAlign.LEFT);
				}
				case "image-text" -> {
					text(slide, style, "$/title/", 60, 50, 840, 70, 30, true, style.title(), TextAlign.LEFT);
					picture(presentation, slide, style, "image", 60, 150, 400, 300);
					text(slide, style, "$/text/", 510, 150, 390, 300, 16, false, style.body(), TextAlign.LEFT);
				}
				case "agenda" -> {
					text(slide, style, "$/title/", 60, 50, 840, 70, 30, true, style.title(), TextAlign.LEFT);
					for (int item = 0; item < 6; item++) {
						// two columns of three items
						double x = item < 3 ? 60 : 500;
						double y = 160 + (item % 3) * 90;
						text(slide, style, String.valueOf(item + 1), x, y, 40, 45, 22, true, style.accent(), TextAlign.LEFT);
						text(slide, style, "$/item" + item + "/", x + 45, y, 355, 80, 18, false, style.body(), TextAlign.LEFT);
					}
				}
				case "statement" -> {
					text(slide, style, "$/statement/", 80, 150, 800, 220, 40, true, style.title(), TextAlign.LEFT);
					text(slide, style, "$/attribution/", 80, 390, 800, 50, 16, false, style.body(), TextAlign.LEFT);
				}
				case "metrics" -> {
					text(slide, style, "$/title/", 60, 50, 840, 70, 30, true, style.title(), TextAlign.LEFT);
					for (int metric = 0; metric < 4; metric++) {
						double x = 60 + metric * 220;
						text(slide, style, "$/metric" + metric + ".value/", x, 190, 200, 80, 44, true, style.accent(), TextAlign.LEFT);
						text(slide, style, "$/metric" + metric + ".label/", x, 280, 200, 120, 14, false, style.body(), TextAlign.LEFT);
					}
				}
				case "timeline" -> {
					text(slide, style, "$/title/", 60, 50, 840, 70, 30, true, style.title(), TextAlign.LEFT);
					for (int milestone = 0; milestone < 5; milestone++) {
						double x = 60 + milestone * 176;
						text(slide, style, "$/milestone" + milestone + ".date/", x, 210, 160, 40, 18, true, style.accent(), TextAlign.LEFT);
						text(slide, style, "$/milestone" + milestone + ".text/", x, 255, 160, 150, 13, false, style.body(), TextAlign.LEFT);
					}
				}
				case "team" -> {
					text(slide, style, "$/title/", 60, 50, 840, 70, 30, true, style.title(), TextAlign.LEFT);
					for (int person = 0; person < 4; person++) {
						double x = 60 + person * 220;
						picture(presentation, slide, style, "person" + person + ".photo", x, 160, 180, 180);
						text(slide, style, "$/person" + person + ".name/", x, 355, 180, 40, 18, true, style.title(), TextAlign.LEFT);
						text(slide, style, "$/person" + person + ".role/", x, 395, 180, 70, 13, false, style.body(), TextAlign.LEFT);
					}
				}
				case "quote" -> {
					text(slide, style, "$/quote/", 100, 140, 760, 200, 32, false, style.title(), TextAlign.LEFT);
					text(slide, style, "$/author/", 100, 370, 760, 40, 18, true, style.accent(), TextAlign.LEFT);
					text(slide, style, "$/role/", 100, 410, 760, 40, 14, false, style.body(), TextAlign.LEFT);
				}
				case "chart" -> {
					text(slide, style, "$/title/", 60, 50, 840, 70, 30, true, style.title(), TextAlign.LEFT);
					picture(presentation, slide, style, "chart", 60, 140, 840, 290);
					text(slide, style, "$/takeaway/", 60, 445, 840, 60, 16, false, style.body(), TextAlign.LEFT);
				}
				case "full-image" -> {
					picture(presentation, slide, style, "image", 0, 0, 960, 540);
					text(slide, style, "$/headline/", 60, 340, 840, 90, 40, true, style.title(), TextAlign.LEFT);
					text(slide, style, "$/subheadline/", 60, 435, 840, 50, 18, false, style.body(), TextAlign.LEFT);
				}
				default -> throw new IllegalArgumentException("No starter template known for slide type " + slideType
					+ "; design it in PowerPoint instead");
			}
			presentation.write(output);
		}
	}

	private static void text(XSLFSlide slide, Style style, String content,
			double x, double y, double width, double height,
			double fontSize, boolean bold, Color color, TextAlign align) {
		XSLFTextBox box = slide.createTextBox();
		box.setAnchor(new Rectangle2D.Double(x, y, width, height));
		box.setVerticalAlignment(VerticalAlignment.TOP);
		XSLFTextParagraph paragraph = box.addNewTextParagraph();
		paragraph.setTextAlign(align);
		XSLFTextRun run = paragraph.addNewTextRun();
		run.setText(content);
		run.setFontFamily(style.font());
		run.setFontSize(fontSize);
		run.setBold(bold);
		run.setFontColor(color);
	}

	private static void picture(XMLSlideShow presentation, XSLFSlide slide, Style style, String variable,
			double x, double y, double width, double height) throws IOException {
		XSLFPictureShape picture = slide.createPicture(
			presentation.addPicture(placeholderImage(style, (int) width, (int) height), PictureType.PNG)
		);
		picture.setAnchor(new Rectangle2D.Double(x, y, width, height));
		// this is how Coreoz declares a replaceable image: the variable is carried by the shape hyperlink
		picture.createHyperlink().setAddress("$/" + variable + "/");
	}

	private static byte[] placeholderImage(Style style, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		try {
			graphics.setColor(style.placeholder());
			graphics.fillRect(0, 0, width, height);
		} finally {
			graphics.dispose();
		}
		ByteArrayOutputStream png = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", png);
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot generate the placeholder image", e);
		}
		return png.toByteArray();
	}

	/**
	 * The look of a generated theme.
	 */
	private record Style(String font, Color title, Color body, Color accent, Color placeholder) {

		static Style of(Theme theme) {
			return switch (theme) {
				case CORPORATE -> new Style("Calibri",
					new Color(0x1F, 0x38, 0x64), new Color(0x40, 0x40, 0x40), new Color(0x2E, 0x74, 0xB5),
					new Color(0xD9, 0xE2, 0xF3));
				case MODERN -> new Style("Verdana",
					new Color(0x22, 0x22, 0x22), new Color(0x55, 0x55, 0x55), new Color(0xE0, 0x38, 0x6B),
					new Color(0xF2, 0xE1, 0xE9));
				case MINIMAL -> new Style("Arial",
					Color.BLACK, new Color(0x66, 0x66, 0x66), new Color(0x99, 0x99, 0x99),
					new Color(0xEE, 0xEE, 0xEE));
			};
		}
	}
}
