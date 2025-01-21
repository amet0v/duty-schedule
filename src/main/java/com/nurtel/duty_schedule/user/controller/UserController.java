package com.nurtel.duty_schedule.user.controller;

import com.nurtel.duty_schedule.exceptions.BadRequestException;
import com.nurtel.duty_schedule.exceptions.MethodNotAllowedException;
import com.nurtel.duty_schedule.exceptions.NotFoundException;
import com.nurtel.duty_schedule.routes.BaseRoutes;
import com.nurtel.duty_schedule.user.dto.UserRequest;
import com.nurtel.duty_schedule.user.dto.UserResponse;
import com.nurtel.duty_schedule.user.entity.UserEntity;
import com.nurtel.duty_schedule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${init.username}")
    private String initUsername;
    @Value("${init.password}")
    private String initPassword;

    @PostMapping(BaseRoutes.NOT_SECURED_INIT)
    public UserResponse init(){
        Optional<UserEntity> checkUser = userRepository.findByUsername(initUsername);
        UserEntity user;

        if (checkUser.isEmpty()){
            user = UserEntity.builder()
                    .username(initUsername)
                    .password(passwordEncoder.encode(initPassword))
                    .build();

            userRepository.save(user);
        }
        else {
            user = checkUser.get();
        }
        return UserResponse.of(user);
    }

    @GetMapping(BaseRoutes.USER)
    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @GetMapping(BaseRoutes.USER_BY_ID)
    public UserResponse getUser(@PathVariable Long id) throws NotFoundException {
        return UserResponse.of(userRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @PostMapping(BaseRoutes.USER)
    public UserResponse createUser(@RequestBody UserRequest request) throws BadRequestException {
        request.validate();

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);
        return UserResponse.of(user);
    }

    @PutMapping(BaseRoutes.USER_EDIT)
    public UserResponse editUser(Principal principal, @RequestBody UserRequest request) throws NotFoundException {
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(NotFoundException::new);
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);
        return UserResponse.of(user);
    }

    @PutMapping(BaseRoutes.USER_BY_ID)
    public UserResponse editUser(
            @PathVariable Long id, @RequestBody UserRequest request, Principal principal
    ) throws NotFoundException, MethodNotAllowedException {
        UserEntity user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        if (!Objects.equals(principal.getName(), initUsername)) throw new MethodNotAllowedException();

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);
        return UserResponse.of(user);
    }

    @DeleteMapping(BaseRoutes.USER_BY_ID)
    public String deleteUser(@PathVariable Long id) throws NotFoundException {
        UserEntity user = userRepository.findById(id).orElseThrow(NotFoundException::new);

        userRepository.deleteById(id);
        return HttpStatus.OK.name();
    }
}
