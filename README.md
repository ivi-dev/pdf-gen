# pdf-gen
A command‑line tool that generates PDF documents from user‑provided HTML templates and data.

# Features
- Generate PDFs from HTML templates
- Inject dynamic data into templates
- Locale‑aware date and number formatting
- CLI interface
- Extensible data population logic

# Getting statrted

## Prerequisits
- Java 17+
- Maven 3.8+

## Build
```bash
./scripts/build.sh
```

## Run
**Using the built-in font and the system's current locale. The generated PDF file will be placed in the current working direcory:**
```bash
./scripts/run.sh --template=/path/to/template.html \
                 --data=/path/to/dynamic-data.json
```

**Specifying a custom output directory:**
```bash
./scripts/run.sh --template=/path/to/template.html \
                 --data=/path/to/dynamic-data.json \
                 --output=/path/to/output-file.pdf
```

**Specifying a custom locale:**
```bash
./scripts/run.sh --template=/path/to/template.html \
                 --data=/path/to/dynamic-data.json \
                 --output=/path/to/output-file.pdf \
                 --locale=/ISO 639 alpha-2 or alpha-3 code
```

**Specifying a custom font:**
```bash
./scripts/run.sh --template=/path/to/template.html \
                 --data=/path/to/dynamic-data.json \
                 --output=/path/to/output-file.pdf \
                 --locale="bg" \
                 --font="Custom /path/to/custom/font.ttf"
```

# Testing
**Execute automated tests and renerate a code coverage report:**
```
./scripts/test.sh
```
Then open ```/target/site/jacoco/index.html``` in a browser to see the code coverage report.

# Dependencies
- [JSoup](https://github.com/jhy/jsoup) (HTML parsing)
- [Jackson](https://github.com/FasterXML/jackson) (dynamic data parsing)
- [Flying Saucer](https://github.com/flyingsaucerproject/flyingsaucer) (HTML and CSS 2.1 rendering)
- [OpenHTMLToPDF](https://github.com/danfickle/openhtmltopdf) (PDF generation)
- [JCommander](https://github.com/cbeust/jcommander) (command-line arguments parsing)
- [JUnit 5](https://github.com/junit-team/junit-framework) (testing framework)
- [Mockito](https://github.com/mockito/mockito) (mocking framework)

# License
MIT
