package miniproj.vttp_batchb_miniproj.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import miniproj.vttp_batchb_miniproj.models.Movie;

@Repository
public class MovieRepo {
    public static final String HASH_KEY_NAME = "Movies";
    @Autowired
    @Qualifier("redis-obj")
    RedisTemplate<String, Object> redisTemplate;

    public List<Movie> findAll() {
        List<Movie> payloads = redisTemplate
                .opsForHash()
                .values(HASH_KEY_NAME)
                .stream()
                .map(Movie.class::cast)
                .collect(Collectors.toList());
        return payloads;
    }

    public int size() {
        return redisTemplate.opsForHash().values(HASH_KEY_NAME).size();
    }
    // peter@gmail.com: {
    // movieid:{movie}
    // },
    // mary@gmail.com: {
    // movieid:{movie}
    // },

    // find movies by id
    public Optional<Movie> findByMoviesId(int id) {
        String stringId = String.valueOf(id);
        Object result = redisTemplate.opsForHash().get(HASH_KEY_NAME, stringId);

        // If result is null, return an empty Optional
        if (result == null) {
            return Optional.empty();
        }

        // Cast the result to ToDo and return as an Optional
        return Optional.of((Movie) result);
        // return (ToDo) redisTemplate.opsForHash().get(HASH_KEY_NAME, id);
    }

    // hset Movies id article - not used, shifted into user model.
    public Movie save(Movie mov) {
        redisTemplate.opsForHash().put(HASH_KEY_NAME, String.valueOf(mov.getId()), mov);
        return mov;
    }

}
