# Stack

- Spring Boot 4.0.6, Java 25
- Maven (use `./mvnw` wrapper, not system `mvn`)

# Code Intelligence

This project is indexed by GitNexus as **gitnexus-test**. Use GitNexus MCP tools for code intelligence.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze --skip-agents-md` first.

## Rules

- MUST run `gitnexus_impact` before editing any symbol. Warn user on HIGH/CRITICAL risk.
- MUST run `gitnexus_detect_changes()` before committing.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename`.

# Commands

```sh
./mvnw spring-boot:run          # run dev server
./mvnw test                     # run tests
./mvnw package                  # build jar (target/)
./mvnw test -Dtest=ClassName    # run single test class
```

# Structure

```
src/main/java/com/example/frehen/testgitnexus/   # application code
src/test/java/com/example/frehen/testgitnexus/   # tests
```

Entrypoint: `TestGitnexusApplication.java`

# Notes

- No CI, linter, or formatter configured yet — follow standard Spring/Java conventions.
- `target/` is gitignored; never commit build artifacts.
