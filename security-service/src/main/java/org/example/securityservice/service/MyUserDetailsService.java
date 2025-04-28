package org.example.securityservice.service;


import model.User;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
//
//    @Autowired
//    UserReopository userRepository; // Assuming you have a UserRepository to fetch user data
//    // Implement the loadUserByUsername method to load user details from your database
//    // For example:
//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         User user = userRepository.findByUsername(username);
//         if (user == null) {
//             throw new UsernameNotFoundException("User not found");
//         }
//         return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
//     }


}
