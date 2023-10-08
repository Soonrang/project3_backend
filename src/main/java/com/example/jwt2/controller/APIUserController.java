package com.example.jwt2.controller;

import com.example.jwt2.domain.APIUser;
import com.example.jwt2.domain.Reservation;
import com.example.jwt2.domain.Restaurant;
import com.example.jwt2.dto.apiuser.CreateAPIUserRequestDTO;
import com.example.jwt2.dto.apiuser.MyPageAPIUserRequestDTO;
import com.example.jwt2.dto.apiuser.MyPageAPIUserResponseDTO;
import com.example.jwt2.dto.reservation.MyPageReservationResponseDTO;
import com.example.jwt2.dto.reservation.ReservationDTO;
import com.example.jwt2.dto.restaurant.RestaurantDTO;
import com.example.jwt2.dto.restaurant.RestaurantReservationRequestDTO;
import com.example.jwt2.repository.APIUserRepository;
import com.example.jwt2.service.ReservationService;
import com.example.jwt2.service.RestaurantService;
import com.example.jwt2.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Log4j2
public class APIUserController {

    private final APIUserRepository apiUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestaurantService restaurantService;
    private final ReservationService reservationService;

    private final JWTUtil jwtUtil;

    // 일반 유저 회원가입
    @PostMapping ("/join")
    public ResponseEntity<String> joinUser(@RequestBody CreateAPIUserRequestDTO requestDTO) {

        // 이미 존재하는 사용자인지 검사
        Optional<APIUser> existingUser = apiUserRepository.findByMid(requestDTO.getMid());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>("User already exists!", HttpStatus.BAD_REQUEST);
        }

        // CreateAPIUserRequestDTO(유저 생성 요청)으로 받아와서 저장(완료)
        APIUser apiUser = APIUser.builder()
                .mid(requestDTO.getMid())
                .mpw(passwordEncoder.encode(requestDTO.getMpw()))
                .role("ROLE_USER") // 일반 사용자로 설정
                .nickname(requestDTO.getNickname())
                .email(requestDTO.getEmail())
                .birth(requestDTO.getBirth())
                .phoneNumber(requestDTO.getPhoneNumber())
                .build();

        this.apiUserRepository.save(apiUser);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }
    // 회원 정보 수정
    @GetMapping("editInfo")
    public ResponseEntity<?> EditUserInfoGET(@RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if(userId == null) {
            return new ResponseEntity<>("User not authorized.", HttpStatus.UNAUTHORIZED);
        }
        APIUser apiUser = this.apiUserRepository.findById(userId).orElseThrow();

        MyPageAPIUserResponseDTO responseDTO = new MyPageAPIUserResponseDTO();
//        responseDTO.setMpw(apiUser.getMpw());
        responseDTO.setNickname(apiUser.getNickname());
        responseDTO.setEmail(apiUser.getEmail());
        responseDTO.setPhoneNumber(apiUser.getPhoneNumber());

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    @PostMapping("editInfo")
    public ResponseEntity<?> EditUserInfoPOST(@RequestHeader("Authorization") String token, @RequestBody MyPageAPIUserRequestDTO requestDTO) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if(userId == null) {
            return new ResponseEntity<>("User not authorized.", HttpStatus.UNAUTHORIZED);
        }
        APIUser apiUser = this.apiUserRepository.findById(userId).orElseThrow();

        apiUser.changeNickname(requestDTO.getNickname());
        apiUser.changeEmail(requestDTO.getEmail());
        apiUser.changePhoneNumber(requestDTO.getPhoneNumber());

        this.apiUserRepository.save(apiUser);

        return new ResponseEntity<>("user info edited successfully", HttpStatus.OK);
    }

    // 식당 예약
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveRestaurant(@RequestHeader("Authorization") String token, @RequestBody RestaurantReservationRequestDTO reservationData) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if(userId == null) {
            return new ResponseEntity<>("User not authorized.", HttpStatus.UNAUTHORIZED);
        }

        // 현재 예약중인(접근한) 유저
        APIUser apiUser = this.apiUserRepository.findById(userId).orElseThrow();
        // 예약 할 식당 가져오기
        Long restaurantId = Long.parseLong(reservationData.getRestaurantId());
        Restaurant restaurant = this.restaurantService.getRestaurant(restaurantId);

        // 예약 중복 데이터 충돌 상황을 클라이언트에게 알림
        boolean exists = apiUser.getReservationList().stream().anyMatch(reservation ->
                restaurant.getId() == reservation.getRestaurant().getId()
        );
        if (exists) return new ResponseEntity<>("same reservation already have", HttpStatus.CONFLICT);

        // 예약한 레스토랑의 전체 예약횟수 추가
        this.restaurantService.plusReservationCount(restaurantId);

        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setApiUser(apiUser);
        reservationDTO.setRestaurant(restaurant);

        this.reservationService.createReservation(reservationDTO);

        return new ResponseEntity<>("User reserved successfully!", HttpStatus.OK);
    }

    // 예약 삭제
    @DeleteMapping("/deleteReservation/{reservationId}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long reservationId, @RequestHeader("Authorization") String token) {
        String currentUserId = jwtUtil.getUserIdFromToken(token);
        if(currentUserId == null) {
            return new ResponseEntity<>("User not authorized.", HttpStatus.UNAUTHORIZED);
        }

        try {
            this.reservationService.deleteReservation(reservationId);
            return new ResponseEntity<>("Reservation deleted successfully.", HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>("Reservation not found.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // 다른 예외를 위한 처리
            return new ResponseEntity<>("Failed to delete reservation.", HttpStatus.BAD_REQUEST);
        }
    }

    // 마이페이지 구현 예정
    // 필요한 정보: 아이디, 닉네임, 예약 정보, 유저 정보 수정 등
    @GetMapping("/mypage")
    public ResponseEntity<?> viewMyPage(@RequestHeader("Authorization") String token) {

        MyPageAPIUserResponseDTO responseDTO = new MyPageAPIUserResponseDTO();

        String currentUserId = jwtUtil.getUserIdFromToken(token);
        if(currentUserId == null) {
            return new ResponseEntity<>("User not authorized.", HttpStatus.UNAUTHORIZED);
        }
        APIUser currentAPIUser = this.apiUserRepository.findById(currentUserId).orElseThrow(() -> new NoSuchElementException("APIUser not found with ID: " + currentUserId));

        responseDTO.setMid(currentAPIUser.getMid());
        responseDTO.setNickname(currentAPIUser.getNickname());
        responseDTO.setEmail(currentAPIUser.getEmail());
        responseDTO.setPhoneNumber(currentAPIUser.getPhoneNumber());
        responseDTO.setBirth(currentAPIUser.getBirth());

        // 예약 식당 리스트 가져오기
        // currentAPIUser.getReservationList()에서 예약 정보 리스트 가져오기
        List<Reservation> reservationList = currentAPIUser.getReservationList();
        List<MyPageReservationResponseDTO> myPageReservationResponseDTOList = reservationList.stream()
                .map(reservation -> {
                    MyPageReservationResponseDTO myPageReservationResponseDTO = new MyPageReservationResponseDTO();
                    myPageReservationResponseDTO.setRestaurant_name(reservation.getRestaurant().getName());
                    myPageReservationResponseDTO.setReservation_id(reservation.getId());
                    return myPageReservationResponseDTO;
                })
                .collect(Collectors.toList());

        responseDTO.setReservationList(myPageReservationResponseDTOList);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 로그인한 아이디 추출
    @GetMapping("/getUserId")
    public ResponseEntity<String> getUserIdFromToken(@RequestHeader("Authorization") String token) {
        if (token == null)
            return null;

        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(userId);
    }

    // 로그인한 닉네임 추출
    @GetMapping("/getUserNickname")
    public ResponseEntity<String> getUserNicknameFromToken(@RequestHeader("Authorization") String token) {
        if (token == null)
            return null;

        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        APIUser apiUser = this.apiUserRepository.findById(userId).orElseThrow();
        String userNickname = apiUser.getNickname();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", Charset.forName("UTF-8")));
        return new ResponseEntity<>(userNickname, headers, HttpStatus.OK);
    }
}
