package miniproj.vttp_batchb_miniproj.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class User implements Serializable {

    @Email(message = "Invalid email format")
    @NotNull(message = "Email cannot be null")
    @NotEmpty(message = "Email cannot be empty")
    private String email; // Unique identifier for the user

    @NotNull(message = "Password cannot be null")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private ArrayList<Movie> watchlist; // NOSQL, cannot split relationship between user and watchlist.

    // Constructor
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.watchlist = new ArrayList<Movie>();
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Movie> getWatchlist() {
        return this.watchlist;
    }

    public void setWatchlist(ArrayList<Movie> watchlist) {
        this.watchlist = watchlist;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
