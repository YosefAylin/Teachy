/**
 * Contains service interfaces and implementations that encapsulate the business logic of the application.
 * 
 * <p>The service layer acts as an intermediary between controllers and repositories,
 * implementing business rules, validations, and coordinating operations that may involve
 * multiple repositories or external systems.</p>
 * 
 * <p>Key interfaces in this package include:</p>
 * <ul>
 *   <li>{@code UserService} - User management operations</li>
 *   <li>{@code StudentService} - Student-specific operations</li>
 *   <li>{@code TeacherService} - Teacher-specific operations</li>
 *   <li>{@code AdminService} - Administrative operations</li>
 *   <li>{@code LessonService} - Lesson management operations</li>
 *   <li>{@code ReviewService} - Review management operations</li>
 * </ul>
 * 
 * <p>Implementations of these interfaces are located in the {@code impl} subpackage
 * and typically use the {@code @Service} and {@code @Transactional} annotations to
 * ensure proper transaction management.</p>
 */
package io.jos.onlinelearningplatform.service;