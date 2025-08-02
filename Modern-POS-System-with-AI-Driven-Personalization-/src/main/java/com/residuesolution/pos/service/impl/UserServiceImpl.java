package com.residuesolution.pos.service.impl;

import com.residuesolution.pos.dto.User;
import com.residuesolution.pos.entity.UserEntity;
import com.residuesolution.pos.repository.UserRepository;
import com.residuesolution.pos.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder; // Add password encoder

    @Override
    public Boolean addUser(User user) {
        // Convert DTO to Entity
        UserEntity userEntity = mapper.map(user, UserEntity.class);

        // Encode the password before saving
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

        UserEntity savedUser = userRepository.save(userEntity);
        return savedUser != null;
    }

    @Override
    public List<User> getAllUsers() {
        // Fetch all users from the repository
        List<UserEntity> userEntities = userRepository.findAll();
        // Convert List of Entities to List of DTOs using Stream.toList() for an immutable list
        return userEntities.stream()
                .map(userEntity -> {
                    User userDto = mapper.map(userEntity, User.class);
                    // Don't expose password in DTO
                    userDto.setPassword(null);
                    return userDto;
                })
                .toList();
    }

    @Override
    public User getUserById(Integer id) {
        // Fetch user by ID
        Optional<UserEntity> userEntity = userRepository.findById(id);
        // Convert Entity to DTO if found
        if (userEntity.isPresent()) {
            User userDto = mapper.map(userEntity.get(), User.class);
            // Don't expose password in DTO
            userDto.setPassword(null);
            return userDto;
        }
        return null;
    }

    @Override
    public Boolean updateUser(Integer id, User user) {
        // Check if the user exists
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);
        if (optionalUserEntity.isPresent()) {
            UserEntity existingUser = optionalUserEntity.get();
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());

            // Only update password if provided
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            existingUser.setRole(user.getRole());
            existingUser.setBiometricData(user.getBiometricData());
            existingUser.setMfaEnabled(user.isMfaEnabled());
            existingUser.setActive(user.isActive());

            // Save the updated user entity
            userRepository.save(existingUser);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteUser(Integer id) {
        // Check if the user exists
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}