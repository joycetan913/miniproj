package miniproj.vttp_batchb_miniproj.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import miniproj.vttp_batchb_miniproj.constants.Constant;
import miniproj.vttp_batchb_miniproj.models.User;
import miniproj.vttp_batchb_miniproj.services.UserService;

@Controller
@RequestMapping
public class UserController {
    @Autowired
    private UserService userService;

    // Show the login page
    @GetMapping("/")
    public String showLoginPage() {
        return "login"; // Returns login.html page
    }

    // Show the login page
    @GetMapping("/logout")
    public String showLoginPage(HttpSession session) {
        session.removeAttribute(Constant.ATTR_USER);
        return "logout"; // Returns login.html page
    }

    // Handle login and registration
    @PostMapping(path ="/login", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public String handleLogin(@RequestParam String email, @RequestParam String password, Model model,
            HttpSession session) {
        // Check if the user exists
        User existingUser = userService.getUserByEmail(email);

        if (existingUser == null) {
            // If the user does not exist, register them
            User newUser = new User(email, password); // Assuming User has an email and password constructor
            userService.registerUser(newUser);
            Constant.setSessionUserAttribute(session, newUser);
            return "redirect:/movies"; // Redirect to home page after registration
        }

        // If the user exists, check if the password is correct
        if (!existingUser.getPassword().equals(password)) {
            // If the password is wrong, display error
            model.addAttribute("errorMessage", "Incorrect password. Please try again.");
            return "login"; // Return to login page with error
        }
        Constant.setSessionUserAttribute(session, existingUser);
        // If the password is correct, route to home page
        return "redirect:/movies"; // Redirect to home page
    }
}
