package miniproj.vttp_batchb_miniproj.models;

import java.util.List;

//organise related data tgt instead of multiple params, return one obj
public class MovieWrapper {
    private List<Movie> movies;
    private int currentPage;
    private int totalPages;
    private int totalResults;

    // Default constructor
    public MovieWrapper() {
    }

    // Constructor with parameters
    public MovieWrapper(List<Movie> movies, int currentPage, int totalPages) {
        this.movies = movies;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    // Getter and Setter methods

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}