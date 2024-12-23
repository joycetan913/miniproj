package miniproj.vttp_batchb_miniproj.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import miniproj.vttp_batchb_miniproj.models.Movie;
import miniproj.vttp_batchb_miniproj.models.MovieWrapper;
import miniproj.vttp_batchb_miniproj.models.User;
import miniproj.vttp_batchb_miniproj.repositories.MovieRepo;
import miniproj.vttp_batchb_miniproj.services.MovieService;
import miniproj.vttp_batchb_miniproj.services.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class MovieRestController {
    @Autowired
    private MovieService movieSvc;

    @Autowired
    private UserService userSvc;

    @GetMapping("/redis/user_movies/{email}")
    public ResponseEntity<User> getUserMovies(@PathVariable("email") String email) {
        User userInfo = userSvc.getUserByEmail(email);
        // Check if there are no movies found
        if (userInfo == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }

        // Return the list of movies in JSON format with 200 OK status
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    // GET /search
    // params: page
    // Accept: application/json
    @GetMapping("/tmdb/search")
    public ResponseEntity<MovieWrapper> searchMovies(@RequestParam(value = "page", defaultValue = "1") int page) {
        // Fetch the movies for the given page from the repository (this could be from
        // DB or external API)
        MovieWrapper movies = movieSvc.getMovies(page);

        // Check if there are no movies found
        if (movies.getMovies().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }

        // Return the list of movies in JSON format with 200 OK status
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/tmdb/movie/{movieId}")
    public ResponseEntity<Movie> getMovieDetail(@PathVariable("movieId") int movieId) {
        // Fetch the movies for the given page from the repository (this could be from
        // DB or external API)
        Movie movie = movieSvc.getMoviesById(movieId);

        // Check if there are no movies found
        if (movie == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }

        // Return the list of movies in JSON format with 200 OK status
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    // public ResponseEntity<NewsArticle> getNewsById(@PathVariable("id") int id) {
    // // Fetch the article by ID from Redis using the service
    // NewsArticle article = newsService.getArticleById(id);

    // if (article != null) {
    // // Return the article with a 200 OK status
    // return new ResponseEntity<>(article, HttpStatus.OK);
    // } else {
    // // If article not found, return 404 Not Found
    // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    // }
    // }
}
