package com.cognizant.SortedWallet.controller;

import com.cognizant.SortedWallet.exceptions.UserAlreadyExistsException;
import com.cognizant.SortedWallet.model.User;
import com.cognizant.SortedWallet.repository.UserRepository;
import com.cognizant.SortedWallet.service.UserService;
import com.cognizant.SortedWallet.utils.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@Valid User user, Errors errors, Model model) {
        if (errors.hasErrors()) {
            return "register";
        }
        try {
            userService.register(user);
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model,HttpSession session) {
        if (userService.authenticate(username, password,session)) {
//            model.addAttribute("authenticater",true);
            session.setAttribute("authenticater",true);
            System.out.println(true);
            return "redirect:/expenses";
        } else {
            model.addAttribute("errorMessage", "Invalid username or password");
            return "login";
        }
    }
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request, Model model,HttpSession session) {
        session.removeAttribute("authenticater");
        request.getSession().invalidate();
        return "redirect:/login";
    }
}
