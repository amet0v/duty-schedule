package com.nurtel.duty_schedule.user.controller;

import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.MethodNotAllowedException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.routes.BaseRoutes;
import com.nurtel.duty_schedule.user.dto.request.UserRequest;
import com.nurtel.duty_schedule.user.dto.response.UserResponse;
import com.nurtel.duty_schedule.user.entity.UserEntity;
import com.nurtel.duty_schedule.user.repository.UserRepository;
import com.nurtel.duty_schedule.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${init.username}")
    private String initUsername;
    @Value("${init.password}")
    private String initPassword;

    @GetMapping(BaseRoutes.NOT_SECURED_INIT)
    public UserResponse init() {
        Optional<UserEntity> checkUser = userRepository.findByUsername(initUsername);
        UserEntity user;

        if (checkUser.isEmpty()) {
            user = UserEntity.builder()
                    .username(initUsername)
                    .password(passwordEncoder.encode(initPassword))
                    .build();

            userRepository.save(user);
        } else {
            user = checkUser.get();
        }
        return UserResponse.of(user);
    }

    @GetMapping(BaseRoutes.USERS)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @GetMapping(BaseRoutes.USER_BY_ID)
    public UserResponse getUser(@PathVariable Long id) throws NotFoundException {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) throw new NotFoundException("Пользователь с указанным id не найден");
        return UserResponse.of(user.get());
    }

    @PostMapping(BaseRoutes.USERS)
    public UserResponse createUser(@RequestBody UserRequest request) throws BadRequestException {
        request.validate();

        return UserResponse.of(UserService.createUser(
                userRepository,
                request.getUsername(),
                request.getPassword(),
                passwordEncoder
        ));
    }

    @PutMapping(BaseRoutes.USER_EDIT)
    public UserResponse editPrincipalUser(Principal principal, @RequestBody UserRequest request) throws NotFoundException {
        return UserResponse.of(UserService.editPrincipal(
                userRepository,
                request.getUsername(),
                request.getPassword(),
                passwordEncoder,
                principal.getName()
        ));
    }

    @PutMapping(BaseRoutes.USER_BY_ID)
    public UserResponse editUser(
            @PathVariable Long id, @RequestBody UserRequest request, Principal principal
    ) throws NotFoundException, MethodNotAllowedException {
        UserEntity user = userRepository.findById(id).orElseThrow();
        if (!Objects.equals(principal.getName(), initUsername)) throw new MethodNotAllowedException();

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);
        return UserResponse.of(user);
    }

    @DeleteMapping(BaseRoutes.USER_BY_ID)
    public String deleteUser(@PathVariable Long id) throws NotFoundException {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) throw new NotFoundException("Пользователь с указанным id не найден");

        userRepository.deleteById(id);
        return HttpStatus.OK.name();
    }
}
