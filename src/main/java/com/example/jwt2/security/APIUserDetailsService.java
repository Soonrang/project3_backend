package com.example.jwt2.security;

import com.example.jwt2.domain.APIUser;
import com.example.jwt2.dto.apiuser.APIUserDTO;
import com.example.jwt2.repository.APIUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class APIUserDetailsService implements UserDetailsService {  // 사용자 세부 정보 서비스

    // 데이터베이스에 저장된 사용자 정보를 검색하기 위해 주입
    private final APIUserRepository apiUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<APIUser> result = this.apiUserRepository.findById(username);
        APIUser apiUser = result.orElseThrow(() -> new UsernameNotFoundException("Can not find Member ID"));

        APIUserDTO apiUserDTO = new APIUserDTO(
                apiUser.getMid(),
                apiUser.getMpw(),
                apiUser.getRole(),
                apiUser.getNickname()
        );

        log.info("API_UserDTO: " + apiUserDTO);

        return apiUserDTO;
    }

}
