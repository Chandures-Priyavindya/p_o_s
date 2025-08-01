package com.residuesolution.pos.dto;

//import ch.qos.logback.core.joran.spi.NoAutoStart;
import com.residuesolution.pos.enums.Role;
//import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class User {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private String biometricData;
    private boolean mfaEnabled = false;
    private boolean isActive = true;
    private LocalDateTime createdAt;


}
