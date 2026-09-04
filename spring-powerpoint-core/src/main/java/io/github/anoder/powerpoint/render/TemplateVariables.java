package io.github.anoder.powerpoint.render;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

/**
 * Finds the Coreoz variables declared by a template: {@code $/name/} or {@code $/name:argument/} in
 * the text of the shapes, and on the hyperlink of the picture shapes, which is how Coreoz declares an
 * image to replace.
 */
final class TemplateVariables {

	private static final Pattern VARIABLE = Pattern.compile("\\$/([^/:]+)(?::[^/]*)?/");

	private TemplateVariables() {
	}

	/**
	 * The variables a template declares, split by how they are declared, because that decides what can be
	 * done with them: a text variable is replaced by a text, a hyperlink variable by an image — or the
	 * shape carrying it is hidden.
	 *
	 * @param texts      variables written inside the text of a shape or of a table cell
	 * @param hyperlinks variables carried by the hyperlink of a shape, pictures in particular
	 */
	record Declared(Set<String> texts, Set<String> hyperlinks) {

		Set<String> all() {
			Set<String> all = new LinkedHashSet<>(texts);
			all.addAll(hyperlinks);
			return all;
		}
	}

	static Declared declaredIn(XMLSlideShow presentation) {
		Declared declared = new Declared(new LinkedHashSet<>(), new LinkedHashSet<>());
		for (XSLFSlide slide : presentation.getSlides()) {
			collect(slide.getShapes(), declared);
		}
		return declared;
	}

	/**
	 * @return the variables left in the text of a rendered presentation, which must be none
	 */
	static Set<String> textVariablesIn(XMLSlideShow presentation) {
		return declaredIn(presentation).texts();
	}

	private static void collect(List<XSLFShape> shapes, Declared declared) {
		for (XSLFShape shape : shapes) {
			switch (shape) {
				case XSLFGroupShape group -> collect(group.getShapes(), declared);
				case XSLFTable table -> {
					for (XSLFTableRow row : table.getRows()) {
						for (XSLFTableCell cell : row.getCells()) {
							addAll(cell.getText(), declared.texts());
						}
					}
				}
				case XSLFTextShape text -> {
					addAll(text.getText(), declared.texts());
					addHyperlink(text, declared.hyperlinks());
				}
				case XSLFPictureShape picture -> addHyperlink(picture, declared.hyperlinks());
				default -> {
					// other shapes cannot carry a variable
				}
			}
		}
	}

	private static void addHyperlink(XSLFSimpleShape shape, Set<String> variables) {
		var hyperlink = shape.getHyperlink();
		if (hyperlink != null) {
			addAll(hyperlink.getAddress(), variables);
		}
	}

	private static void addAll(String text, Set<String> variables) {
		if (text == null || text.isEmpty()) {
			return;
		}
		Matcher matcher = VARIABLE.matcher(text);
		while (matcher.find()) {
			variables.add(matcher.group(1));
		}
	}
}
