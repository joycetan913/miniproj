package miniproj.vttp_batchb_miniproj.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import miniproj.vttp_batchb_miniproj.constants.Constant;
import miniproj.vttp_batchb_miniproj.models.Movie;
import miniproj.vttp_batchb_miniproj.models.MovieWrapper;
import miniproj.vttp_batchb_miniproj.models.User;
import miniproj.vttp_batchb_miniproj.repositories.UserRepo;
import miniproj.vttp_batchb_miniproj.services.MovieService;
import miniproj.vttp_batchb_miniproj.services.UserService;

@Controller
@RequestMapping
public class MovieController {

    @Autowired
    private MovieService movieSvc;

    @Autowired
    private UserService userSvc;

    // movie login
    @GetMapping("/movies")
    public String getLanding(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User userSession = Constant.getSessionUserAttribute(session);
        if (userSession == null) {
            redirectAttributes.addFlashAttribute("message", "You need to be logged in to use Movie Watchlist!");
            return "redirect:/";
        }
        // only when login then load this page
        // fetch the movies for the given page from the repo (this could be from
        // DB or ext API)
        MovieWrapper movies = movieSvc.getMovies(page);
        // Add the movies and pagination information to the model
        // Serialize into Json for Thymeleaf HTML to understand
        model.addAttribute("movies", movies.getMovies());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", movies.getTotalPages());
        model.addAttribute("totalItems", movies.getTotalResults());
        return "movie";
    }

    // movie review view
    @GetMapping("/review/{movieId}")
    public String showReviewPage(@PathVariable("movieId") int movieId, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User userSession = Constant.getSessionUserAttribute(session);
        if (userSession == null) {
            redirectAttributes.addFlashAttribute("message", "You need to be logged in to use Movie Watchlist!");
            return "redirect:/";
        }
        // only when login then load this page
        // Fetch the movies for the given page from the repo (this could be from
        // DB or ext API)
        User user = userSvc.getUserByEmail(userSession.getEmail());
        // Check if there are no movies found
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "User not found.");
            return "redirect:/";
        }

        ArrayList<Movie> watchlist = user.getWatchlist();
        Movie reviewMovie = null;
        for (Movie m : watchlist) {
            if (m.getId() == movieId) {
                reviewMovie = m;
                break;
            }
        }

        if (reviewMovie == null) {
            redirectAttributes.addFlashAttribute("message", "Cannot review movie that is not in watchlist.");
            return "redirect:/watchlist";
        }

        // Add movie details to the model
        model.addAttribute("movie", reviewMovie);

        // Return the view for the review page
        return "review"; // review.html
    }

    // delete movie from watchlist
    @PostMapping("/watchlist/delete")
    public String removeMovieFromWatchlist(int movieId, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Movie movieData = movieSvc.getMoviesById(movieId);

        // Session is logged in or not
        User userSession = Constant.getSessionUserAttribute(session);
        if (userSession == null) {
            redirectAttributes.addFlashAttribute("message", "You need to be logged in to use Movie Watchlist!");
            return "redirect:/";
        }

        // for some reason the redis cannot find the current session user data
        User user = userSvc.getUserByEmail(userSession.getEmail());
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "User not found.");
            return "redirect:/";
        }

        if (movieData != null) {
            ArrayList<Movie> watchlist = user.getWatchlist();
            Movie searchMovie = null;
            for (Movie m : watchlist) {
                if (m.getId() == movieData.getId()) {
                    searchMovie = m;
                    break;
                }
            }
            if (searchMovie == null) {
                redirectAttributes.addFlashAttribute("message", "Movie already removed from watchlist!");
                return "redirect:/watchlist";
            }

            if (movieSvc.removeMovieFromWatchlist(user, movieData)) {
                redirectAttributes.addFlashAttribute("message", "Movie removed from watchlist successfully!");
                // show pop up saying added on the same webpage that triggers add
                return "redirect:/watchlist";
            } else {
                redirectAttributes.addFlashAttribute("message", "Movie not found, failed to removed from watchlist!");
                return "redirect:/watchlist";
            }

        } else {
            // show error pop up saying fail to add on the same webpage triggering add
            redirectAttributes.addFlashAttribute("message", "Failed to remove movie from watchlist.");
            return "redirect:/watchlist";
        }
    }

    // review pg after submit
    @PostMapping(path = "/watchlist/review", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public String reviewMovieFromWatchlist(Integer movieId, String memories, Float myRating, Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Movie movieData = movieSvc.getMoviesById(movieId);
        System.out.println("@PostMapping(\"/watchlist/review\"): id=" + movieId + ", memories=" + memories
                + ", myRatings=" + myRating);
        // Session is logged in or not
        User userSession = Constant.getSessionUserAttribute(session);
        if (userSession == null) {
            redirectAttributes.addFlashAttribute("message", "You need to be logged in to use Movie Watchlist!");
            return "redirect:/";
        }

        // For some reason the redis cannot find the current session user data.
        User user = userSvc.getUserByEmail(userSession.getEmail());
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "User not found.");
            return "redirect:/";
        }

        if (movieData != null) {
            ArrayList<Movie> watchlist = user.getWatchlist();
            Movie searchMovie = null;
            for (Movie m : watchlist) {
                if (m.getId() == movieData.getId()) {
                    searchMovie = m;
                    break;
                }
            }
            if (searchMovie == null) {
                redirectAttributes.addFlashAttribute("message", "Unable to review Movie that is not in watchlist!");
                return "redirect:/watchlist";
            }

            movieData.setMemories(memories);
            movieData.setMyRating(myRating);
            movieData.setHasWatched(true);
            movieSvc.updateReviewMovie(user, movieData);
            redirectAttributes.addFlashAttribute("message", "Movie reviewed successfully!");
            // show pop up of successful review
            return "redirect:/watchlist";

        } else {
            // show error pop up
            redirectAttributes.addFlashAttribute("message", "Failed to review movie from watchlist.");
            return "redirect:/watchlist";
        }
    }

    // show watchlist
    @GetMapping("/watchlist")
    public String getLanding(Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        User userSession = Constant.getSessionUserAttribute(session);
        if (userSession == null) {
            redirectAttributes.addFlashAttribute("message", "You need to be logged in to use Movie Watchlist!");
            return "redirect:/";
        }
        // only when login then load this page
        // Fetch the movies for the given page from the repository (this could be from
        // DB or external API)
        User user = userSvc.getUserByEmail(userSession.getEmail());
        // Check if there are no movies found
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "User not found.");
            return "redirect:/";
        }

        // Add the movies and pagination information to the model
        model.addAttribute("movies", user.getWatchlist());
        return "watchlist";
    }

    // add movie
    @PostMapping("/movies/add")
    public String addMovieToWatchList(int movieId, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("addMovieToWatchList: " + movieId);
        Movie movieData = movieSvc.getMoviesById(movieId);

        // Session is logged in or not
        User userSession = Constant.getSessionUserAttribute(session);
        if (userSession == null) {
            redirectAttributes.addFlashAttribute("message", "You need to be logged in to use Movie Watchlist!");
            return "redirect:/";
        }

        // For some reason the redis cannot find the current session user data.
        User user = userSvc.getUserByEmail(userSession.getEmail());
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "User not found.");
            return "redirect:/";
        }

        if (movieData != null) {
            ArrayList<Movie> watchlist = user.getWatchlist();
            Movie searchMovie = null;
            for (Movie m : watchlist) {
                if (m.getId() == movieData.getId()) {
                    searchMovie = m;
                    break;
                }
            }
            if (searchMovie != null) {
                redirectAttributes.addFlashAttribute("message", "Movie already added to watchlist!");
                return "redirect:/movies";
            }

            movieSvc.addMovieToWatchlist(user, movieData);
            redirectAttributes.addFlashAttribute("message", "Movie added to watchlist successfully!");
            // show pop up saying added on the same webpage triggering add
            return "redirect:/movies";
        } else {
            // show error pop up saying fail to add on the same webpage triggering add
            redirectAttributes.addFlashAttribute("message", "Failed to add movie to watchlist.");
            return "redirect:/movies";
        }

    }
}


