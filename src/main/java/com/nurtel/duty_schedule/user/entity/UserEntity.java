package com.nurtel.duty_schedule.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    //@Size(min = 6, message = "Password must be at least 6 characters long")
    protected String username;
    protected String password;
}
