package miniproj.vttp_batchb_miniproj.repositories;

import miniproj.vttp_batchb_miniproj.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserRepo {
    public static final String HASH_KEY_NAME = "Users";
    @Autowired
    @Qualifier("redis-obj")
    RedisTemplate<String, Object> redisTemplate;

    // Save or update user in Redis
    public void saveUser(User user) {
        redisTemplate.opsForHash().put(HASH_KEY_NAME, user.getEmail(), user);
    }

    // Get user by email (since email is the unique identifier)
    public User getUserByEmail(String email) {
        return (User) redisTemplate.opsForHash().get(HASH_KEY_NAME, email);
    }

    // Update user: For Redis, we can treat the update as a save operation
    public void updateUser(User user) {
        // Simply save the updated user using the email as the key
        saveUser(user);
    }

    // Delete user by email
    public void deleteUserByEmail(String email) {
        redisTemplate.opsForHash().delete(HASH_KEY_NAME, email);
    }

    // Check if the user exists
    public boolean existsByEmail(String email) {
        return redisTemplate.opsForHash().hasKey(HASH_KEY_NAME, email);
    }
}
