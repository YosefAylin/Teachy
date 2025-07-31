# Documentation Guidelines

This document outlines the standards and best practices for maintaining documentation in the Teachy Online Learning Platform project.

## General Principles

1. **Documentation is a first-class citizen**: Documentation should be treated with the same importance as code.
2. **Keep documentation close to code**: Documentation should be as close as possible to what it documents.
3. **Update documentation with code changes**: Documentation should be updated in the same pull request as code changes.
4. **Write for the audience**: Consider who will read the documentation and tailor it accordingly.
5. **Be concise but complete**: Provide enough detail without unnecessary verbosity.

## Documentation Types

### Code Documentation

#### Javadoc Standards

- **Classes**: Every class should have a Javadoc comment explaining its purpose and responsibilities.
  ```java
  /**
   * Represents a user in the system.
   * This class serves as the base for all user types and contains common user properties.
   */
  public class User {
      // ...
  }
  ```

- **Methods**: Public and protected methods should have Javadoc comments describing:
  - What the method does
  - Parameters (using `@param`)
  - Return values (using `@return`)
  - Exceptions thrown (using `@throws`)
  ```java
  /**
   * Authenticates a user with the provided credentials.
   *
   * @param username The username of the user to authenticate
   * @param password The plain text password to verify
   * @return true if authentication is successful, false otherwise
   * @throws IllegalArgumentException if the username or password is null or empty
   */
  public boolean authenticate(String username, String password) {
      // ...
  }
  ```

- **Fields**: Important fields, especially public ones, should have Javadoc comments.
  ```java
  /**
   * The unique identifier for the user.
   */
  private Long id;
  ```

#### Package Documentation

- Each package should have a `package-info.java` file describing:
  - The purpose of the package
  - Key classes/interfaces in the package
  - Relationships with other packages

### Project Documentation

#### README.md

- Should provide a high-level overview of the project
- Include setup instructions
- List key features
- Provide basic usage examples
- Link to more detailed documentation

#### API Documentation

- Document all public APIs
- Include request/response formats
- Provide example usage
- Note any authentication requirements
- Document error responses

#### Architecture Documentation

- Describe the overall system architecture
- Include component diagrams
- Explain key design decisions
- Document data models and relationships

#### User Guide

- Provide step-by-step instructions for using the application
- Include screenshots for complex workflows
- Organize by user role or feature
- Include troubleshooting information

## Documentation Process

### When to Update Documentation

Documentation should be updated when:
- Adding new features
- Modifying existing features
- Fixing bugs that change behavior
- Refactoring code structure
- Deprecating features

### How to Update Documentation

1. Use the `DOCUMENTATION-UPDATE-TEMPLATE.md` to identify required documentation changes
2. Update code-level documentation (Javadoc, comments) alongside code changes
3. Update relevant project documentation files
4. Include screenshots or diagrams if they help explain complex features
5. Update "Last updated" dates in all modified documentation files

### Review Process

Documentation should be reviewed as part of the code review process:
- Is the documentation clear and accurate?
- Does it match the actual code behavior?
- Are all public APIs documented?
- Are examples provided for complex features?
- Is the formatting consistent?

## Formatting Standards

### Markdown

- Use proper Markdown syntax for headings, lists, code blocks, etc.
- Use heading levels appropriately (H1 for document title, H2 for major sections, etc.)
- Use code blocks with language specification for code examples
- Use tables for structured data
- Use links to reference other documentation

### Javadoc

- Use HTML tags sparingly in Javadoc comments
- Prefer simple formatting (paragraphs, lists) for readability
- Start sentences with a capital letter and end with a period
- Use `{@code}` for inline code references
- Use `{@link}` to reference other classes or methods

## Tools and Resources

- **IDE Plugins**: Use IDE plugins that help with documentation generation and validation
- **Markdown Linters**: Use linters to ensure consistent Markdown formatting
- **Documentation Generators**: Use tools like Javadoc and Swagger for API documentation
- **Diagram Tools**: Use tools like PlantUML or Mermaid for creating diagrams

---

*Last updated: July 31, 2025*