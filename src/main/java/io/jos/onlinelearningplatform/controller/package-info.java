/**
 * Contains controller classes that handle HTTP requests and responses.
 * 
 * <p>The controller layer is responsible for processing incoming HTTP requests,
 * invoking the appropriate service methods, and returning responses to the client.
 * Controllers are organized by domain/feature to maintain separation of concerns.</p>
 * 
 * <p>Key subpackages include:</p>
 * <ul>
 *   <li>{@code auth} - Authentication and registration controllers</li>
 *   <li>{@code home} - Home page and navigation controllers</li>
 *   <li>{@code admin} - Administrative functionality controllers</li>
 *   <li>{@code teacher} - Teacher-specific controllers</li>
 *   <li>{@code student} - Student-specific controllers</li>
 * </ul>
 * 
 * <p>Most controllers use Spring's {@code @RestController} annotation and return
 * {@code ModelAndView} objects to render Thymeleaf templates.</p>
 */
package io.jos.onlinelearningplatform.controller;