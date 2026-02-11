# Submission Checklist

- [x] Spring Boot backend project created
- [x] Required REST APIs implemented
- [x] Haversine distance utility implemented
- [x] Dispatch optimization logic implemented
- [x] Validation and global exception handling added
- [x] Unit and integration tests added
- [x] README added with setup and API usage
- [x] Postman collection added
- [x] Build artifacts cleaned (`target/` removed)
- [x] IDE-specific artifacts cleaned (`.vscode/` removed)

## Final Verify Commands

```bash
mvn clean test -Dspring.profiles.active=h2
```

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
