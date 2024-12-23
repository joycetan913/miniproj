package miniproj.vttp_batchb_miniproj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import miniproj.vttp_batchb_miniproj.models.User;
import miniproj.vttp_batchb_miniproj.repositories.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    // Register a new user
    public void registerUser(User user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }
        userRepo.saveUser(user);
    }

    // Get user by email
    public User getUserByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    // Validate password
    public boolean validatePassword(User user, String password) {
        return user.getPassword().equals(password);
    }
}
