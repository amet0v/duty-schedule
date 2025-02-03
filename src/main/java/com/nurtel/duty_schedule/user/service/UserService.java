package com.nurtel.duty_schedule.user.service;

import com.nurtel.duty_schedule.employee.entity.EmployeeEntity;
import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.user.entity.UserEntity;
import com.nurtel.duty_schedule.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    public static UserEntity createUser(
            UserRepository userRepository,
            String username,
            String password,
            PasswordEncoder passwordEncoder
    ) throws BadRequestException {
        Optional<UserEntity> checkUser = userRepository.findByUsername(username);
        if (checkUser.isPresent()) throw new BadRequestException("Логин занят");
        if (password.length() < 6) throw new BadRequestException("Минимальная длина пароля - 6 символов");
        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        user = userRepository.save(user);
        return user;
    }

    public static UserEntity editPrincipal(
            UserRepository userRepository,
            String username,
            String password,
            PasswordEncoder passwordEncoder,
            String principalName
    ) throws BadRequestException, NotFoundException {
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()){
            Optional<UserEntity> checkUser = userRepository.findByUsername(username);
            if (checkUser.isPresent()) throw new BadRequestException("Логин занят");
        }
        Optional<UserEntity> principalOption = userRepository.findByUsername(principalName);
        UserEntity principal = UserEntity.builder().build();
        if (principalOption.isPresent()) {
            principal = principalOption.get();
        } else throw new NotFoundException("Не найден пользователь: " + principalName);
        if (username != null) principal.setUsername(username);
        if (password.length() < 6) throw new BadRequestException("Минимальная длина пароля - 6 символов");
        principal.setPassword(passwordEncoder.encode(password));

        principal = userRepository.save(principal);
        return principal;
    }
}
