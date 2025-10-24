# Hexagonal + DDD + API‑First Spring Boot (Multi‑Module)

A Gradle multi‑module scaffold that demonstrates how to organize a Spring Boot project using:
- Hexagonal (Ports & Adapters) architecture
- Domain‑Driven Design (DDD)
- API‑First development with OpenAPI Generator

This repository currently focuses on structure and documentation to serve as a template for real implementations.


## Modules
- common
  - Holds OpenAPI schemas and is configured to generate DTOs and interfaces using OpenAPI Generator (WebFlux/Spring Boot 3).
  - Intentionally domain‑neutral and infrastructure‑agnostic.
- intake
  - A feature/bounded‑context module organized per Hexagonal + DDD.
  - Contains application, domain, and infrastructure packages (no business implementation yet).


## Project structure
A high‑level view of the repository layout:

```
hexagonal-ddd-api-first-spring-boot-sample/
├─ settings.gradle
├─ build.gradle
├─ gradlew, gradlew.bat
├─ gradle/
│  └─ wrapper/
├─ LICENSE
├─ README.md
├─ common/
│  ├─ build.gradle
│  └─ src/
│     └─ main/
│        ├─ java/
│        │  └─ com/spectrayan/common/
│        │     └─ package-info.java
│        └─ resources/
│           └─ openapi/
│              └─ openapi.yaml
└─ intake/
   ├─ build.gradle
   └─ src/
      └─ main/
         └─ java/
            └─ com/spectrayan/intake/
               ├─ package-info.java
               ├─ application/
               │  ├─ package-info.java
               │  ├─ port/
               │  │  ├─ in/
               │  │  │  └─ package-info.java
               │  │  └─ out/
               │  │     └─ package-info.java
               │  └─ usecase/
               │     └─ package-info.java
               ├─ domain/
               │  ├─ package-info.java
               │  ├─ model/
               │  │  └─ package-info.java
               │  ├─ repository/
               │  │  └─ package-info.java
               │  └─ service/
               │     └─ package-info.java
               └─ infrastructure/
                  ├─ package-info.java
                  ├─ adapter/
                  │  ├─ in/
                  │  │  └─ web/
                  │  │     └─ package-info.java
                  │  └─ out/
                  │     └─ persistence/
                  │        └─ package-info.java
                  ├─ config/
                  │  └─ package-info.java
                  └─ persistence/
                     ├─ entity/
                     │  └─ package-info.java
                     ├─ mapper/
                     │  └─ package-info.java
                     └─ repository/
                        └─ package-info.java
```


## Hexagonal package structure (by responsibility)
- application
  - port.in: input (driving) ports invoked by inbound adapters (e.g., REST controllers, messaging consumers)
  - port.out: output (driven) ports used by application/use cases to reach external systems (e.g., DB, HTTP, messaging)
  - usecase: orchestration of business behavior; coordinates domain operations and transactions
- domain
  - model: aggregates, entities, value objects, domain events
  - service: domain services when logic doesn’t fit naturally in an aggregate
  - repository: domain‑level repository abstractions (ubiquitous language, no tech details)
- infrastructure
  - adapter.in.web: inbound web adapter (controllers) translating transport → input ports
  - adapter.out.persistence: outbound adapter implementing output ports via persistence technologies
  - persistence.entity/repository/mapper: technical mapping for DB; not exposed to domain or application
  - web.dto/web.mapper: transport DTOs and mappers for the web adapter
  - config: infrastructure configuration (WebFlux, beans, properties)


## End‑to‑end flow

A request travels from the outside world through inbound adapters into the core (application + domain), and results travel back out the same path. Ports (interfaces) sit at the core boundary; adapters implement or call those ports.

```
Outside World (Client)
        │  HTTP request (JSON over HTTP, etc.)
        ▼
[Inbound Adapter]  REST Controller (WebFlux)
        │  invokes
        ▼
[Application]  Input Port (port.in, interface)
        │  implemented by
        ▼
[Application]  Use Case (orchestration/transactions)
        │  delegates to
        ▼
[Domain]  Aggregates · Entities · Value Objects · Domain Services
        │  needs external I/O via
        ▼
[Application]  Output Port (port.out, interface/contract)
        │  implemented by
        ▼
[Outbound Adapter]  Persistence/HTTP/Messaging
        │  DB/Remote call
        ▼
External Systems (DB/Other Services)

<––––––––––––––––––––––––––––––––––––  response/result propagates back  ––––––––––––––––––––––––––––––––––––>
External Systems → Outbound Adapter → Output Port → Domain → Use Case → Input Port → Inbound Adapter → Client
```

Narrative
1) A client sends a request (e.g., HTTP POST).
2) The inbound adapter (REST controller) validates/parses transport DTOs and calls an application input port.
3) A use case implementation handles orchestration and transaction boundaries; it invokes domain operations.
4) Domain model enforces business rules (aggregates/services). No framework or transport dependencies here.
5) For external I/O, the use case calls output ports (defined as interfaces in the application layer).
6) Outbound adapters implement those output ports using technical details (DB, HTTP, messaging) and return results.
7) The inbound adapter maps domain results to transport DTOs and returns the HTTP response to the client.

Notes
- Dependency rule: domain ← application ← adapters (infrastructure). Adapters depend inward, never the reverse.
- Ports are interfaces owned by the application layer; adapters either call (input) or implement (output) them.
- Keep mapping at the edges: transport ↔ domain in adapters; core remains technology‑agnostic and testable.


## API‑First with OpenAPI
- The common module contains openapi/openapi.yaml.
- openapi-generator (configured in common/build.gradle) generates WebFlux/Spring Boot 3 interfaces and DTOs into build/generated sources and wires them into compilation.
- Advantages:
  - Schemas drive consistent DTOs across modules.
  - Contracts are explicit and reviewable before implementation.
  - Enables client code generation if desired.

Basic generation flow
- Edit common/src/main/resources/openapi/openapi.yaml
- Run: ./gradlew :common:openApiGenerate
- Generated sources are added to the main source set of the common module automatically.


## How to add a new feature/use case
1) Define or update API contracts (if applicable) in common/openapi.yaml and regenerate DTOs.
2) In intake (or another bounded‑context module):
   - Add an input port interface under application/port/in (method names reflecting ubiquitous language).
   - Implement the use case under application/usecase, depending on domain model and services.
   - Define output port(s) under application/port/out for any external I/O needs.
   - Implement outbound adapters under infrastructure/adapter/out (e.g., persistence) to satisfy output ports.
   - Implement inbound adapters under infrastructure/adapter/in/web (controllers) to expose the input ports over HTTP.
   - Map between transport DTOs and domain model using web/persistence mappers as needed.
3) Keep business rules in domain; keep technical details in infrastructure.


## Build notes
- Java 21, Spring Boot 3.5 (WebFlux). Uses Gradle Wrapper.
- Initial build may fail if you try to start the intake module since there is no main application class yet; this scaffold is intentionally implementation‑light.

Useful commands
- Windows: gradlew.bat clean build
- macOS/Linux: ./gradlew clean build
- Generate DTOs only: ./gradlew :common:openApiGenerate


## License
This project is licensed under the terms of the LICENSE file included in the repository.
