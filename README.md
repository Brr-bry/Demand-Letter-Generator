# Demand Letter Generator

A Java desktop application that automates the generation of demand letters from structured business data. The application is designed to reduce manual document preparation by parsing input data, mapping it to the required fields, and generating a formatted Microsoft Word demand letter using a predefined template.

> **Important:** This application is tailored for a specific business and is **not intended to be a generic demand-letter generator**. Its input format, data fields, and document template are based on the output produced by the business's existing system. Using the application with another business would require changes to the input parser, data mapping, and document template.

## Features

* Import business-generated data from Excel files
* Parse and validate input data
* Map customer and transaction information into the application's data models
* Generate demand letters automatically
* Populate a predefined Word document template
* Replace template placeholders with processed business data
* Provide a JavaFX desktop user interface
* Generate formatted `.docx` documents
* Handle input and generation errors through the application interface

## How It Works

The application follows a simple processing workflow:

```text
Business System
      │
      ▼
Generated Excel Input
      │
      ▼
Excel Parser
      │
      ▼
Data Validation & Mapping
      │
      ▼
Customer / Transaction Models
      │
      ▼
Demand Letter Generator
      │
      ▼
Word Template
      │
      ▼
Generated Demand Letter (.docx)
```

The application is dependent on the structure of the data produced by the business's existing system. The parser expects the required fields and format used by that system.

## Business-Specific Design

This project was developed as a **custom solution for one specific business**.

The application is intentionally coupled to the business's existing data workflow because the input is generated directly from its system. As a result:

* The expected Excel structure is based on the business's system output.
* The parser is designed around the business's specific fields and data format.
* The demand letter follows the business's required document format.
* The Word template contains placeholders corresponding to the business's required information.
* The application is not currently designed as a multi-business or configurable SaaS solution.

To adapt the application for another business, the input parser, data model, field mappings, and document template would need to be modified accordingly.

## Technology Stack

| Technology           | Purpose                                             |
| -------------------- | --------------------------------------------------- |
| **Java 21**          | Primary programming language                        |
| **JavaFX 21**        | Desktop graphical user interface                    |
| **FXML**             | JavaFX UI layout                                    |
| **Maven**            | Project and dependency management                   |
| **Apache POI 5.4.1** | Excel file processing                               |
| **docx4j 11.5.3**    | Microsoft Word document generation and manipulation |
| **Logback**          | Application logging                                 |

The project is configured to compile against **Java 21** and uses Maven for its build configuration.

## Project Structure

```text
Demand-Letter-Generator/
│
├── .github/
│   └── workflows/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── karesh/
│       │           └── demandlettergenerator/
│       │               ├── controller/
│       │               │   └── MainController.java
│       │               │
│       │               ├── generator/
│       │               │   ├── PlaceholderReplacer.java
│       │               │   └── WordGenerator.java
│       │               │
│       │               ├── model/
│       │               │   ├── Customer.java
│       │               │   └── Transaction.java
│       │               │
│       │               ├── parser/
│       │               │   ├── ExcelParser.java
│       │               │   └── ParserState.java
│       │               │
│       │               ├── service/
│       │               │   └── GenerationService.java
│       │               │
│       │               ├── ui/
│       │               │   └── ...
│       │               │
│       │               ├── util/
│       │               │   └── ...
│       │               │
│       │               └── Main.java
│       │
│       └── resources/
│           ├── fxml/
│           │   └── MainView.fxml
│           │
│           ├── templates/
│           │   ├── demandtemplate.docx
│           │   └── old_demandtemplate.docx
│           │
│           ├── icons/
│           ├── images/
│           ├── styles/
│           ├── META-INF/
│           └── logback.xml
│
├── pom.xml
└── .gitignore
```

The repository currently separates the application into parser, generator, model, service, controller, UI, and utility packages. Its resources include the JavaFX FXML view and Word document templates.

## Main Components

### ExcelParser

Responsible for reading and processing the Excel data generated by the business's existing system.

The parser converts the spreadsheet data into application objects that can be used during demand-letter generation.

```text
Excel File
    ↓
ExcelParser
    ↓
Customer / Transaction Data
```

The parser package contains `ExcelParser.java` and `ParserState.java`.

### Data Models

The application currently uses models such as:

* `Customer`
* `Transaction`

These objects represent the information required during the demand-letter generation process.

### WordGenerator

Responsible for generating the final Microsoft Word demand letter.

The generator package contains:

* `WordGenerator.java`
* `PlaceholderReplacer.java`

The generator uses the predefined Word template and replaces its placeholders with the appropriate business data.

### GenerationService

Coordinates the demand-letter generation process and connects the relevant application components.

```text
Input
  ↓
Parser
  ↓
Models
  ↓
Generation Service
  ↓
Word Generator
  ↓
Demand Letter
```

The service package currently contains `GenerationService.java`.

### JavaFX Interface

The desktop interface is built using JavaFX and FXML.

The project contains `MainView.fxml`, which serves as the primary FXML view.

## Input Requirements

The application expects an Excel file generated by the target business's existing system.

Because the parser is designed around that system's output, the input file must follow the expected structure and contain the required information.

The application should therefore be treated as part of the business's existing workflow:

```text
Business System
       ↓
Generate Required Excel Data
       ↓
Demand Letter Generator
       ↓
Generate Demand Letter
```

An arbitrary Excel spreadsheet from another business is **not guaranteed to work** without modifying the parser and data mapping.

## Output

The primary output of the application is a formatted Microsoft Word demand letter:

```text
Demand Letter
    ↓
.docx
```

The generated document is based on the predefined demand-letter template stored in:

```text
src/main/resources/templates/
```

The repository currently contains `demandtemplate.docx` and `old_demandtemplate.docx`.

## Requirements

Before running the application, install:

* Java Development Kit (JDK) 21
* Maven
* JavaFX-compatible environment
* Git

## Running the Application

### 1. Clone the repository

```bash
git clone https://github.com/Brr-bry/Demand-Letter-Generator.git
cd Demand-Letter-Generator
```

### 2. Build the project

```bash
mvn clean package
```

### 3. Run the application

The project uses Maven with JavaFX and is configured with `com.karesh.demandlettergenerator.ui.App` as the JavaFX main class.

Run the application through your IDE or the configured Maven JavaFX plugin.

## Maven Dependencies

The project uses:

* Apache POI `5.4.1`
* docx4j `11.5.3`
* JavaFX `21.0.2`
* Logback `1.5.18`

The project is configured for Java 21.

## Limitations

### Business-Specific Input

The application is tailored to a single business because its input is generated by that business's existing system. The expected Excel structure and fields are therefore specific to that workflow.

### Fixed Document Template

The generated demand letter follows a predefined Word template. Different businesses or document formats would require a different template and corresponding placeholder mapping.

### Limited Portability

The application cannot be considered a plug-and-play solution for other businesses. Supporting another business would require adapting the parser, models, field mappings, and document template.

### Desktop Application

The project is implemented as a Java desktop application using JavaFX rather than as a web or mobile application.

## Project Status

This project is a custom desktop automation solution developed for a specific business workflow. Its primary purpose is to automate the conversion of structured business-generated data into standardized demand letters while reducing repetitive manual document preparation.

## License

This project is intended for its specific business and project context. No general commercial redistribution or adaptation is implied by this repository.
