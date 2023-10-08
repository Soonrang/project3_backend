package com.example.jwt2.service;

import com.example.jwt2.domain.APIUser;
import com.example.jwt2.dto.apiuser.APIUserResponseDTO;
import com.example.jwt2.repository.APIUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class APIUserService {

    private final APIUserRepository apiUserRepository;

    // 유저 전체 목록
    public List<APIUserResponseDTO> getAllRoleUser() {
        List<APIUser> apiUserList = this.apiUserRepository.findAllByRole("ROLE_USER");

        List<APIUserResponseDTO> responseDTOList = new ArrayList<>();
        apiUserList.stream().forEach(apiUser -> {
            APIUserResponseDTO responseDTO = new APIUserResponseDTO();
            responseDTO.setMid(apiUser.getMid());
            responseDTO.setRole(apiUser.getRole());
            responseDTO.setNickname(apiUser.getNickname());
            responseDTO.setEmail(apiUser.getEmail());
            responseDTO.setBirth(apiUser.getBirth());
            responseDTO.setPhoneNumber(apiUser.getPhoneNumber());

            responseDTOList.add(responseDTO);
        });

        return responseDTOList;
    }

    public void deleteRoleUser(String userid) {
        this.apiUserRepository.deleteById(userid);
    }
}
