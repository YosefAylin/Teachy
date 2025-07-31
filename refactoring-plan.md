# Folder Structure Refactoring Plan

## Current Issues
1. Configuration classes (SecurityConfig, AdminInitializer) are in root package
2. DTOs are mixed with controllers (RegisterDto)
3. Test package structure doesn't match main structure (capitalization inconsistency)
4. No clear separation of concerns in controllers
5. No dedicated package for exceptions, utils, or config

## Proposed Structure
```
src/main/java/io/jos/onlinelearningplatform/
├── config/                  # Configuration classes
│   ├── SecurityConfig.java
│   └── AdminInitializer.java
├── controller/              # REST controllers
│   ├── admin/
│   ├── auth/                # Authentication controllers
│   ├── student/
│   ├── teacher/
│   └── home/
├── dto/                     # Data Transfer Objects
│   ├── request/
│   └── response/
├── exception/               # Custom exceptions
├── model/                   # Domain models/entities
├── repository/              # Data access layer
├── service/                 # Business logic
│   └── impl/
└── util/                    # Utility classes

src/test/java/io/jos/onlinelearningplatform/
├── config/
├── controller/
├── repository/
├── service/
└── util/
```

## Specific Changes
1. Move SecurityConfig.java to config package
2. Move AdminInitializer.java to config package
3. Move RegisterDto.java to dto/request package
4. Reorganize controllers by domain/feature
5. Fix test package structure to match main structure
6. Update all imports to reflect new package structure