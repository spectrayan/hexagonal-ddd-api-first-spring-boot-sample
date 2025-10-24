/**
 * Intake module of the system following Hexagonal Architecture, DDD, and API-first.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Implements Intake-specific domain model and business rules.</li>
 *   <li>Exposes application use cases via input ports; interacts with the outside world through adapters.</li>
 *   <li>Remains independent from technical frameworks in the core (domain + application).</li>
 * </ul>
 *
 * <p>This module depends on {@code com.spectrayan:common} for shared OpenAPI DTOs and contracts.</p>
 */
package com.spectrayan.intake;
