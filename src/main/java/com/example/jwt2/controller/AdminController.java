package com.example.jwt2.controller;

import com.example.jwt2.domain.APIUser;
import com.example.jwt2.domain.Categories;
import com.example.jwt2.dto.apiuser.APIUserResponseDTO;
import com.example.jwt2.dto.restaurant.RestaurantDTO;
import com.example.jwt2.service.APIUserService;
import com.example.jwt2.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RestaurantService restaurantService;
    private final APIUserService apiUserService;

    private static final String UPLOADED_FOLDER = "D://images//restaurant//";

    // 식당 등록
    @Transactional
    @PostMapping("/upload")
    public ResponseEntity<?> uploadRestaurant(
            @RequestParam("name") String name,
            @RequestParam("address") String location,
            @RequestParam("category") Categories category,
            @RequestParam("description") String description,
            @RequestParam("openingTime") String openingTimeString,
            @RequestParam("closingTime") String closingTimeString,
            @RequestParam("callNumber") String callNumber,
            @RequestParam("image") List<MultipartFile> files) {

        LocalTime openingTime = LocalTime.parse(openingTimeString);
        LocalTime closingTime = LocalTime.parse(closingTimeString);

        List<String> imagePaths = new ArrayList<>();
        int imageCount = 0;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    UUID uuid = UUID.randomUUID();
                    String filePath = UPLOADED_FOLDER + uuid.toString() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(filePath);
                    Files.write(path, bytes);
                    imagePaths.add(filePath);
                    imageCount++;

                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
        }

        RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                .name(name)
                .location(location)
                .category(category)
                .description(description)
                .openingTime(openingTime)
                .closingTime(closingTime)
                .imagePaths(imagePaths)
                .callNumber(callNumber)
                .imageCount(imageCount)
                .build();

        restaurantService.createRestaurant(restaurantDTO);

        return ResponseEntity.ok("Restaurant uploaded successfully!");
    }

    // id에 해당하는 식당 삭제
    @DeleteMapping("/store/delete/{id}")
    public ResponseEntity<String> removeRestaurant(@PathVariable Long id) {
        this.restaurantService.removeOne(id);
        return ResponseEntity.ok("Restaurant successfully deleted.");
    }

    // 유저 전체 목록
    @GetMapping("/user/list")
    public ResponseEntity<?> getAllUsers() {
        List<APIUserResponseDTO> responseDTOList = this.apiUserService.getAllRoleUser();
        return new ResponseEntity<>(responseDTOList, HttpStatus.OK);
    }
    // 유저 삭제
    @DeleteMapping("/user/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        this.apiUserService.deleteRoleUser(userId);
        return ResponseEntity.ok("delete user successfully");
    }
}
