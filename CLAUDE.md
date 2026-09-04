# CLAUDE.md

Spring Boot 4 starter generating `.pptx` decks from templates. Java 25, Maven, Apache POI, vendored
Coreoz PPT-Templates. No Lombok, no Spring in `spring-powerpoint-core`.

## Build

```bash
export JAVA_HOME=$HOME/.sdkman/candidates/java/25-tem      # mvn itself runs on 21, the build needs 25
mvn -o -f /home/arthur/auto-ppt/pom.xml clean install       # always pass an absolute -f: the shell cwd drifts
```

Green means 47 tests, 0 failures, **0 compiler warnings** — keep it that way.

## Adding a built-in slide type

Worked example: `METRICS` (`MetricsSlideBuilder`, `MetricBuilder`, `metrics.pptx`). Six files, in order:

1. **Builder** — `spring-powerpoint-core/src/main/java/io/github/anoder/powerpoint/dsl/XxxSlideBuilder.java`,
   `final`, extends `AbstractSlideBuilder<XxxSlideBuilder>`, `super(SlideType.XXX)` in the constructor.
   Every setter is `put("variable", value); return this;` and a `{@snippet :}` in the JavaDoc.
   Template variable names live **only here** — never in application code.
   * repeated blocks (`metric0.value`, `person1.photo`): declare a `public interface XxxBuilder` with the
     field methods, let `SlideBlock` implement it, and expose
     `xxxs(Consumer<XxxBuilder>...)` + `xxx(int index, Consumer<XxxBuilder>)` with
     `Objects.checkIndex(index, XXX_COUNT)`. The varargs method needs `@SafeVarargs` and must be `final`.
   * images: `put("image", new SlideImage(bytes))` for `byte[]`, `SlideImages.read(…)` for
     `InputStream`/`Path`. Do not re-implement the reading.
2. **`SlideType`** — add the constant *and* the entry in `VALUES` (deck order), plus the import.
3. **`SlideBuilderRegistry.builtInFactories()`** — add the `DefaultSlideBuilderFactory` entry.
4. **`TemplateFixtureGenerator.write`** — add a `case "xxx"` layout on the 960×540 slide. Text goes through
   `text(...)`, a replaceable image through `picture(presentation, slide, style, "variable", x, y, w, h)`
   — the variable is carried by the picture **hyperlink**, that is how Coreoz declares an image.
   Create pictures before the text boxes that overlap them (POI z-order).
5. **Templates** — regenerate the committed ones, they are not hand-made binaries:
   ```bash
   mvn -o -f /home/arthur/auto-ppt/pom.xml -pl spring-powerpoint-core install -DskipTests
   mvn -o -f /home/arthur/auto-ppt/example/pom.xml exec:java@generate-templates
   ```
6. **Tests + README** — `SlideBuildersTest` (assert the exact variable names),
   `CoreozPowerPointRendererTest.renders_every_built_in_slide_type_in_every_theme` (one sample slide per
   type, its size is asserted against `SlideType.values()`),
   `BusinessReviewServiceTest.every_theme_and_slide_type_of_the_example_templates_can_be_rendered`,
   README §5 tree and §6 table.

An application can add a slide type without touching the library — `SlideType.of(...)` plus a
`SlideBuilderFactory` bean, see README §9. Only the built-in set needs the six steps above.

## Things that will bite

* Missing values are not silently ignored: unset **text** variables are emptied, unset **hyperlink**
  (image) variables are hidden via `mapper.hide(...)`, both logged at `WARN` by
  `CoreozPowerPointRenderer`. That split is why `TemplateVariables.declaredIn` returns
  `Declared(texts, hyperlinks)`.
* A value whose variable is absent from the template throws `InvalidSlideException`; every message carries
  theme, slide type and resolved template path.
* Read presentations back with `Decks.open(byte[])`, never `new XMLSlideShow(InputStream)`: POI applies its
  zip-bomb ratio check to stream opens and flat-colour images legitimately exceed it. Tests may instead
  call `ZipSecureFile.setMinInflateRatio(0)` — test scope only, never in library code.
* `CoreozPowerPointRenderer` is the **only** Coreoz-aware class. Nothing from `com.coreoz.ppt` may appear in
  a public signature.
* `com/coreoz/ppt/*` and `org/apache/poi/ooxml/PptPoiBridge.java` are vendored upstream sources
  (Apache-2.0). Do not reformat them; record any change in `NOTICE`.
* Templates are one-slide decks; the assembler keeps the theme, masters and layouts of the **first** slide,
  so use explicit text boxes, not layout placeholders.
