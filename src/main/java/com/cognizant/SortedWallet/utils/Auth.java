package com.cognizant.SortedWallet.utils;

import com.cognizant.SortedWallet.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class Auth {
    //	Check for user authentication
    public User retrieveAuthenticatedUser(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user!=null){
            System.out.println("User authnticated: "+user.getEmail());
        }
        else{
            System.out.println("User not authenticated");
        }
        return user;
    }
}


