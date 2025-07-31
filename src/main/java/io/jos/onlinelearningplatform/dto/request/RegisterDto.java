package io.jos.onlinelearningplatform.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterDto {
    private String username;
    private String email;
    private String password;
    private String role; // "STUDENT", "TEACHER", or "ADMIN"
}