package net.javaguides.springboot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import net.javaguides.springboot.controller.ResponseHandler;
import net.javaguides.springboot.model.SaveCTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.UserRepository;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // get all users
    @GetMapping("/users")
    public List<User> getAllEmployees() {
        return userRepository.findAll();
    }

    // create user rest api
    @PostMapping("/users")
    public User createEmployee(@RequestBody User user) {
        return userRepository.save(user);
    }

    // get user by email
    @PostMapping(value = "/verification", consumes = {"application/json"})
    public ResponseEntity<Object> loginVrify(@RequestBody @Valid User user, BindingResult bindingResult) throws NoSuchFieldException, IllegalAccessException {
        try {
            List<String> errorMessages = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
            if (errorMessages.size() > 0) {
                return ResponseHandler.generateResponse(errorMessages.toString(), HttpStatus.MULTI_STATUS, errorMessages);
            }
            User userInfo = userRepository.findByEmail(user.getEmail());
            if (userInfo != null && user.getEmail().equals(userInfo.getEmail()) && user.getPassword().equals(userInfo.getPassword())) {
                Object res = userInfo;
                return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, res);
            } else {
                return ResponseHandler.generateResponse("Username or password is not correct", HttpStatus.MULTI_STATUS, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.MULTI_STATUS, null);
        }
    }

    // get user by id rest api
    @GetMapping("/users/{id}")
    //show url

    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id :" + id));
        return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, user);
    }


    @PostMapping(value = "/User/SaveCToken", consumes = {"application/json"})
    public ResponseEntity<Object> saveCToken(@RequestBody SaveCTokenRequest request) {
        String userEmail = request.getEmail();
        String ctoken = request.getCtoken();

        // 根据 userEmail 查询对应的 User 对象
        User user = userRepository.findByEmail(userEmail);

        if (user != null) {
            // 用户存在，更新 ctoken
            user.setCtoken(ctoken);
        } else {
            // 用户不存在，创建新的 User 对象并保存到数据库
            user = new User(userEmail, ctoken);
        }

        userRepository.save(user);

        return ResponseHandler.generateResponse("CToken saved successfully.", HttpStatus.OK, user);
    }
}
