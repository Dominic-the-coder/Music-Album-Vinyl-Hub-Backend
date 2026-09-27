package com.example.mini_project.services;

import com.example.mini_project.models.User;
import com.example.mini_project.repositories.ImageRepository;
import com.example.mini_project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private ImageRepository imageRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();


    // GET ALL USERS
    public List<User> getAllUsers() {
        return repository.findAll();
    }


    // GET USER BY ID
    public User getUserById(int id) {
        return repository.findById(id).orElse(null);
    }


    // GET USER BY EMAIL
    public User getUserByEmail(String email) {
        return repository.findByEmail(email);
    }


    // CREATE USER
    public User saveUser(User user) {

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return repository.save(user);
    }


    // REGISTER USER
    public User register(
            String name,
            String email,
            String password
    ) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return saveUser(user);
    }


    // UPDATE USER
    public User updateUser(Integer id, User updatedUser) {

        User user = repository.findById(id).orElse(null);

        if (user == null) {
            return null;
        }

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());

        return repository.save(user);
    }

    public User updateProfileImage(Integer userId, Integer imageId) {

        User user = repository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        user.setImageId(imageId);

        return repository.save(user);
    }


    // CHANGE PASSWORD
    public User changePassword(
            Integer id,
            String currentPassword,
            String newPassword
    ) {

        User user = repository.findById(id)
                .orElse(null);

        if (user == null) {
            return null;
        }

        // Check if current password is correct
        boolean passwordMatches =
                passwordEncoder.matches(
                        currentPassword,
                        user.getPassword()
                );

        if (!passwordMatches) {
            return null;
        }

        // Encrypt the new password
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        return repository.save(user);
    }


    // DELETE USER
    public boolean deleteUser(int id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);

        return true;
    }
}