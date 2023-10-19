package net.javaguides.springboot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.javaguides.springboot.model.SellerTokenBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.javaguides.springboot.repository.SellerTokenBackRepository;

@RestController
@RequestMapping("/api/v1/")
public class SellerTokenBackController {

	@Autowired
	private SellerTokenBackRepository SellerTokenBackRepository;
	
	// get all sellerTokens
	@GetMapping("/SellerTokenBacks")
	public List<SellerTokenBack> getAllEmployees(){
		return SellerTokenBackRepository.findAll();
	}		
	
	// create sellerToken rest api
	@PostMapping("/SellerTokenBacks")
	public SellerTokenBack createEmployee(@RequestBody SellerTokenBack sellerToken) {
		return SellerTokenBackRepository.save(sellerToken);
	}
	@PostMapping("/SellerTokenBacksList")
	public List<SellerTokenBack> createEmployeeList(@RequestBody List<SellerTokenBack> sellerTokenList) {
		return SellerTokenBackRepository.saveAll(sellerTokenList);
	}

	// // get sellerToken by sellerId rest api
	// @PostMapping("/getSellerTokenBySelerId")
	// public List<SellerTokenBack>  getSellerTokenBySelerId(@RequestBody Map<String, String> body) {
	// 	String sellerId = body.get("sellerId");
	// 	return SellerTokenBackRepository.findBySellerId(sellerId);
	// }

		// get user by email
	@PostMapping(value="/getSellerTokenBySelerId",consumes={"application/json"})
    public ResponseEntity<Object> loginVrify(@RequestBody HashMap<String,String> sellerId, BindingResult bindingResult) throws NoSuchFieldException, IllegalAccessException {
        try {
            List<String> errorMessages = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }
            if (errorMessages.size() > 0) {
                return ResponseHandler.generateResponse(errorMessages.toString(), HttpStatus.MULTI_STATUS, errorMessages);
            }
			// get sellerid from hashmap
			String sellerIdStr = sellerId.get("sellerId");
            List<SellerTokenBack> sellerTokenList = SellerTokenBackRepository.findBySellerId(sellerIdStr);
            if(sellerTokenList.size() > 0){
				Map<String, Object> res = new HashMap<>();
				res.put("sellerTokenList", sellerTokenList);
				return ResponseHandler.generateResponse("SUCCESS", HttpStatus.OK, res);
            }else{
                return ResponseHandler.generateResponse("Username or password is not correct", HttpStatus.MULTI_STATUS, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.MULTI_STATUS, null);
        }
    }
		

	
}
