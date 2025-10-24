/**
 * Common module for shared API-first artifacts and cross-cutting types.
 *
 * <p>This module hosts:</p>
 * <ul>
 *   <li>OpenAPI schemas under {@code src/main/resources/openapi} that define shared models.</li>
 *   <li>Generated DTOs and interfaces produced by the OpenAPI Generator (WebFlux/Spring, Boot 3).</li>
 *   <li>Any cross-module constants, exceptions, and utility contracts needed by feature modules.</li>
 * </ul>
 *
 * <h2>Architecture</h2>
 * <p>Following API-first principles, schemas drive the DTOs used across bounded contexts. Generation is
 * wired via Gradle's {@code openApiGenerate} task. The generated sources are added to the main source set.</p>
 *
 * <h2>Hexagonal/DDD Role</h2>
 * <p>While hexagonal architecture and DDD focus primarily on the domain and application layers of each
 * bounded context, the {@code common} module remains infrastructure-agnostic and domain-neutral. It
 * avoids business rules and stores only artifacts that are truly shared and stable.</p>
 */
package com.spectrayan.common;
