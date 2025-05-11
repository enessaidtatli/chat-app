package io.github.enessaidtatli.security;

import io.github.enessaidtatli.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).map(
                userEntity -> User.builder()
                        .username(userEntity.getEmail())
                        .password(userEntity.getPassword())
                        .authorities(userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList())
                        .build()).get();
    }

}
