package miniproj.vttp_batchb_miniproj.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Movie implements Serializable {
    private int id;
    @NotEmpty(message = "Title is mandatory!")
    @Size(min = 3, max = 128, message = "Title must be between 3 and 128 characters long!")
    private String title;
    private String original_language;
    private String overview;
    private double popularity;
    private String poster_path;
    @NotNull(message = "Please select a date!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "Date must be a future date!")
    private LocalDate release_date;
    private double vote_average;
    private int vote_count;
    private boolean hasWatched; // own param
    private String memories; // own param
    private float myRating; // own param

    // @NotEmpty(message = "Please enter a valid email!")
    // @Email(message = "Must be a well-formed email address!")
    // private String user;

    // @NotNull(message = "Please select a date!")
    // @DateTimeFormat(pattern = "yyyy-MM-dd")
    // @Future(message = "Date must be a future date!")
    // private Date date;

    // @NotEmpty(message = "Please choose at least 1 category!")
    // private List<String> categories;

    // @NotEmpty(message = "Content cannot be empty!")
    // private String overview;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public LocalDate getRelease_date() {
        return release_date;
    }

    public void setRelease_date(LocalDate release_date) {
        this.release_date = release_date;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public boolean isHasWatched() {
        return hasWatched;
    }

    public void setHasWatched(boolean hasWatched) {
        this.hasWatched = hasWatched;
    }

    public String getMemories() {
        return memories;
    }

    public void setMemories(String memories) {
        this.memories = memories;
    }

    public float getMyRating() {
        return myRating;
    }

    public void setMyRating(float myRating) {
        this.myRating = myRating;
    }

    public Movie() {
    }

    public Movie(int id, String title, String original_language, String overview, double popularity,
            String poster_path, LocalDate release_date, double vote_average, int vote_count, boolean hasWatched,
            String memories, float myRating) {
        this.id = id;
        this.title = title;
        this.original_language = original_language;
        this.overview = overview;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.hasWatched = hasWatched;
        this.memories = memories;
        this.myRating = myRating;
    }

}
