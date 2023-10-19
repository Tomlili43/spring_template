package net.javaguides.springboot.controller;

import net.javaguides.springboot.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/Login")
public class SysLoginController {

    @PostMapping(value="/verification",consumes={"application/json"})
    public ResponseEntity<Object> saveBySingle(@RequestBody @Valid User user, BindingResult bindingResult) throws NoSuchFieldException, IllegalAccessException {
        try {
            List<String> errorMessages = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
            if (errorMessages.size() > 0) {
                return ResponseHandler.generateResponse(errorMessages.toString(), HttpStatus.MULTI_STATUS, errorMessages);
            }
            // check username and password is valid
            // user email to retrieve user info from database
            if (user.getEmail().equals("alinali@hkaift.com") && user.getPassword().equals("2117c49b")) {
                Object res = user;
                return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, res);
            } else {
                return ResponseHandler.generateResponse("Username or password is not correct", HttpStatus.MULTI_STATUS, null);
            }
            // after configure JPA then below task can be done
            // UserVerification userVerification = new UserVerification();
            // User userInfo = userVerification.getUserInfo(user.getEmail());
            // if(user.getEmail().equals(userInfo.getEmail()) && user.getPassword().equals(userInfo.getPassword())){
            //     Object res = user;
            //     return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, res);
            // }else{
            //     return ResponseHandler.generateResponse("Username or password is not correct", HttpStatus.MULTI_STATUS, null);
            // }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.MULTI_STATUS, null);
        }
    }
}
