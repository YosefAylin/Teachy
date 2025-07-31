/**
 * Contains entity classes that represent the domain model of the application.
 * 
 * <p>The model package defines the core business entities that are persisted to the database.
 * These classes use JPA annotations to define their mapping to database tables and relationships
 * between entities.</p>
 * 
 * <p>Key entities in this package include:</p>
 * <ul>
 *   <li>{@code User} - Base class for all user types with common properties</li>
 *   <li>{@code Student} - Represents a student user with enrollment capabilities</li>
 *   <li>{@code Teacher} - Represents a teacher user who can create and manage courses</li>
 *   <li>{@code Admin} - Represents an administrator with system management privileges</li>
 *   <li>{@code Lesson} - Represents a learning unit within a course</li>
 *   <li>{@code Schedule} - Represents timing information for lessons</li>
 *   <li>{@code Review} - Represents student feedback for courses or teachers</li>
 *   <li>{@code Payment} - Represents financial transactions for course enrollment</li>
 * </ul>
 * 
 * <p>The user hierarchy uses single-table inheritance with a discriminator column
 * to differentiate between user types while maintaining a unified user management approach.</p>
 */
package io.jos.onlinelearningplatform.model;