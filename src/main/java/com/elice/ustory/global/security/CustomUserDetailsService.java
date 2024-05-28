package com.elice.ustory.global.security;

import com.elice.ustory.domain.user.entity.Users;
import com.elice.ustory.domain.user.repository.UserRepository;
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
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Users user = userRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException(nickname));
        return new CustomUserDetails(user);
    }
}
