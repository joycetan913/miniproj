package miniproj.vttp_batchb_miniproj.services;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import miniproj.vttp_batchb_miniproj.models.Movie;
import miniproj.vttp_batchb_miniproj.models.MovieWrapper;
import miniproj.vttp_batchb_miniproj.models.User;
import miniproj.vttp_batchb_miniproj.repositories.MovieRepo;
import miniproj.vttp_batchb_miniproj.repositories.UserRepo;

@Service
public class MovieService {
    @Autowired
    MovieRepo movieRepo;

    @Autowired
    UserRepo userRepo;
    // TMDB URL
    public static final String SEARCH_URL = "https://api.themoviedb.org/3/discover/movie";
    public static final String DETAIL_URL = "https://api.themoviedb.org/3/movie";
    @Value("${tmdb.api.key}")
    private String apiKey;

    // Redis
    public void addMovieToWatchlist(User user, Movie movie) {
        user.getWatchlist().add(movie);
        userRepo.saveUser(user);
    }

    public boolean removeMovieToWatchlist(User user, Movie movie) {
        boolean hasRemoved = user.getWatchlist().removeIf((Movie lhs) -> {
            return lhs.getId() == movie.getId();
        });
        if (hasRemoved) {
            userRepo.saveUser(user);
            return true;
        } else {
            return false;
        }
    }

    public void updateReviewMovie(User user, Movie movie) {
        int indexToUpdate = -1;
        ArrayList<Movie> wl = user.getWatchlist();
        for (int i = 0; i < wl.size(); ++i) {
            if (wl.get(i).getId() == movie.getId()) {
                indexToUpdate = i;
                break;
            }
        }
        if (indexToUpdate >= 0) {
            user.getWatchlist().remove(indexToUpdate);
            user.getWatchlist().add(indexToUpdate, movie);
            userRepo.saveUser(user);
        }
    }

    // api_key
    public MovieWrapper getMovies(int page) {

        // Build the URL with the query parameters (3rd party api - fine tune api resp)
        // ->test in thunderclient
        // include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc
        String url = UriComponentsBuilder
                .fromUriString(SEARCH_URL)
                .queryParam("api_key", apiKey)
                .queryParam("language", "en-us")
                .queryParam("page", page)
                .queryParam("sort_by", "popularity.desc")
                .queryParam("include_adult", false)
                .queryParam("include_video", false)
                .toUriString();

        // Construct the request
        RequestEntity<Void> req = RequestEntity
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        // so that can catch 400/500 error
        // payload is a string
        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> resp = template.exchange(req, String.class);

            // Get the payload
            String payload = resp.getBody();
            // System.out.println("API Response: " + payload);
            return getMovieList(payload);

        } catch (Exception ex) { // exception go here
            ex.printStackTrace();
            return null;
        }

    }

    // Fetch movie data base on the button click, with the movie id, we fetch from
    // tmdb, then insert into redis
    // users:{ email: {email, password, watchlist:[]}}
    // https://api.themoviedb.org/3/movie/{movie_id}
    public Movie getMoviesById(int movieId) {
        String url = UriComponentsBuilder
                .fromUriString(DETAIL_URL + "/" + movieId)
                .queryParam("api_key", apiKey)
                .toUriString();

        // Construct the request
        RequestEntity<Void> req = RequestEntity
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        // so that can catch 400/500 error
        // payload is a string
        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> resp = template.exchange(req, String.class);

            // Get the payload
            String payload = resp.getBody();
            // System.out.println("API Response: " + payload);
            return processMovie(payload);

        } catch (Exception ex) { // exception go here
            ex.printStackTrace();
            return null;
        }
    }

    Movie processMovie(String payload) {
        final String posterImageBaseUrl = "https://image.tmdb.org/t/p/w440_and_h660_face";
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject movieInfo = reader.readObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Extract necessary fields for each article
        int id = movieInfo.getInt("id");
        String originalLanguage = movieInfo.getString("original_language");
        String overview = movieInfo.getString("overview");
        double popularity = movieInfo.getJsonNumber("popularity").doubleValue();
        String posterPath = posterImageBaseUrl + movieInfo.getString("poster_path");
        String releaseDateStr = movieInfo.getString("release_date");
        String title = movieInfo.getString("title");
        double voteAverage = movieInfo.getJsonNumber("vote_average").doubleValue();
        int voteCount = movieInfo.getInt("vote_count");
        // Parse releaseDate from string to LocalDate
        LocalDate releaseDate = LocalDate.parse(releaseDateStr, formatter);

        // Create a new Movie object using the parsed fields
        Movie movie = new Movie(id, title, originalLanguage, overview, popularity, posterPath,
                releaseDate, voteAverage, voteCount, false, "", 5);

        return movie;
    }
    /*
     * "id": 845781,
     * "original_language": "en", // audio voice
     * "overview":
     * "After Santa Claus (codename: Red One) is kidnapped, the North Pole's Head of Security must team up with the world's most infamous tracker in a globe-trotting, action-packed mission to save Christmas."
     * ,
     * "popularity": 6255.699,
     * "poster_path": "/cdqLnri3NEGcmfnqwk2TSIYtddg.jpg",
     * "release_date": "2024-10-31",
     * "title": "Red One",
     * "vote_average": 7.0,
     * "vote_count": 1079
     */

    MovieWrapper getMovieList(String payload) {
        // Create JsonReader
        final String posterImageBaseUrl = "https://image.tmdb.org/t/p/w440_and_h660_face";
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject result = reader.readObject();
        JsonArray movieData = result.getJsonArray("results");
        MovieWrapper mw = new MovieWrapper();
        ArrayList<Movie> listMovie = new ArrayList<>();
        mw.setCurrentPage(result.getInt("page"));
        mw.setTotalPages(result.getInt("total_pages"));
        mw.setTotalResults(result.getInt("total_results"));
        // DateTimeFormatter to parse the release date string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < movieData.size(); i++) {
            JsonObject movieInfo = movieData.getJsonObject(i);

            // Extract necessary fields for each article
            int id = movieInfo.getInt("id");
            String originalLanguage = movieInfo.getString("original_language");
            String overview = movieInfo.getString("overview");
            double popularity = movieInfo.getJsonNumber("popularity").doubleValue();
            String posterPath = posterImageBaseUrl + movieInfo.getString("poster_path");
            String releaseDateStr = movieInfo.getString("release_date");
            String title = movieInfo.getString("title");
            double voteAverage = movieInfo.getJsonNumber("vote_average").doubleValue();
            int voteCount = movieInfo.getInt("vote_count");
            // Parse releaseDate from string to LocalDate
            LocalDate releaseDate = LocalDate.parse(releaseDateStr, formatter);

            // Create a new Movie object using the parsed fields
            Movie movie = new Movie(id, title, originalLanguage, overview, popularity, posterPath,
                    releaseDate, voteAverage, voteCount, false, "", 5);

            // Create a new Movie object and add it to the list
            listMovie.add(movie);
        }

        mw.setMovies(listMovie);
        return mw;
    }
}
