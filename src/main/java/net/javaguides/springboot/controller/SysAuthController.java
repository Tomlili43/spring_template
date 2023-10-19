package net.javaguides.springboot.controller;

import net.javaguides.springboot.model.Seller;
import net.javaguides.springboot.service.impl.SysSellerStateService;

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
@RequestMapping(value="/api/v1/")
public class SysAuthController {

    private SysSellerStateService sellerStateService;

    public SysAuthController(SysSellerStateService sellerStateService) {
        this.sellerStateService = sellerStateService;
    }
    /**
     * sellerAuthorization
     * 获取必要的Seller信息生成state, 保存或更新state数据记录，
     * 并且返回重定向页面"user_email": "alinali@hkaift.com","region": "us-east-1","country_code": "US",
     * "domain_suffix": ".com","ctoken":None
     */
    @PostMapping(value="/sellerAuthorization",consumes={"application/json"})
    public ResponseEntity<Object> sellerAuthorization(@RequestBody @Valid Seller seller, BindingResult bindingResult)
            throws NoSuchFieldException, IllegalAccessException {
        try{
            List<String> errorMessages = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
            if (errorMessages.size() > 0) {
                return ResponseHandler.generateResponse(errorMessages.toString(), HttpStatus.MULTI_STATUS, errorMessages);
            }
            String redirect_url = sellerStateService.generateUrlBySeller(seller);
            System.out.println(redirect_url);

            return ResponseHandler.generateResponse("State generate SUCCESS", HttpStatus.OK, redirect_url);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.MULTI_STATUS, null);
        }
    }


}

