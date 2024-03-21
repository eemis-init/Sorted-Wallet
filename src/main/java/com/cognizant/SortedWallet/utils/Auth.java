package com.cognizant.SortedWallet.utils;

import com.cognizant.SortedWallet.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class Auth {
    //	Check for user authentication
    public User check(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        model.addAttribute("user", user);
        return user;
    }
}


