package net.javaguides.springboot.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.javaguides.springboot.model.Seller;
import net.javaguides.springboot.model.SellerState;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.SellerStateRepository;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.service.impl.SysSellerStateService;
import net.javaguides.springboot.service.impl.SysSellerTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin
@RestController
@RequestMapping(value="/SellingPartnerAPI/")
public class SellingPartnerAPIController {

    private SysSellerStateService sellerStateService;
    private SysSellerTokenService sellerTokenService;
    @Autowired
    private SellerStateRepository sellerStateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public void setSellerStateService(SysSellerStateService sellerStateService) {
        this.sellerStateService = sellerStateService;
    }

    @Autowired
    public void setSellerTokenService(SysSellerTokenService sellerTokenService) {
        this.sellerTokenService = sellerTokenService;
    }

    @GetMapping("/OAuth")
    public ResponseEntity<Object> OAuth(HttpServletRequest request) throws JsonProcessingException, UnsupportedEncodingException {
        /*
         * This function is an endpoint for OAuth flow. It receives a POST request with a JSON body containing certain parameters:
         * - state
         * - selling_partner_id
         * - spapi_oauth_code
         * - mws_auth_token (optional)
         *
         * The function verifies that all necessary parameters are present and that the received 'state' matches the 'state' previously saved by the user.
         * If all validations pass, the function saves the seller's 'selling_partner_id' and 'spapi_oauth_code' in a JSON file on the server.
         * The function returns a JSON response with a success message and the received parameters.
         * If any validation fails, the function returns a JSON response with an error message and a corresponding error message.
         */

        // Process the parameters and perform validation
        String state = request.getParameter("state");
        String sellingPartnerId = request.getParameter("selling_partner_id");
        String spapiOauthCode = request.getParameter("spapi_oauth_code");

        if (state == null || sellingPartnerId == null || spapiOauthCode == null) {
            // If any of the required parameters is missing, return an error response
            return ResponseHandler.generateResponse("Missing required parameters.",
                    HttpStatus.MULTI_STATUS, null);
        }
        // Perform state verification and other validations
        // ...
        SellerState sellerState = sellerStateRepository.findByState(state);
        System.out.println("sellerState: " + sellerState);
        if (sellerState != null) {
            String region = sellerState.getRegion();
            String countryCode = sellerState.getCountryCode();
            String domainSuffix = sellerState.getDominSuffix();
            String requestUrl = "https://api.tolosupplychains.com/api/v1/OAuth?" + request.getQueryString();
            System.out.println(requestUrl + ", " + spapiOauthCode + ", " + sellingPartnerId + ", " + domainSuffix);
            Object LWA_res = sellerStateService.getLWARefreshToken(requestUrl, spapiOauthCode,
                    sellingPartnerId, domainSuffix);
            System.out.println("LWA_res: " + LWA_res);
            if (LWA_res == null) {
                // If getting LWA refresh token failed, return an error response
                return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                        .body("Exchange spapi_oauth_code for LWA refresh token failed.");
            }
//            System.out.println("sending seller api");
//            // Send request to the Flask API to retrieve data
//            String sellerApiReturnMessage = sellerStateService.send_message_to_seller_api(sellingPartnerId, countryCode, region);
//
//            System.out.println("sended seller api");
            System.out.println("saving to sql server");
            // Save to SQL server
            String sqlSaveReturnMessage = sellerTokenService.save_to_sql_server(
                    new HashMap<String, Object>() {{
                        put("selling_partner_id", sellingPartnerId);
                        put("spapi_oauth_code", spapiOauthCode);
                        put("seller_id", sellerState.getUserEmail());
                        put("company_token", sellerState.getCtoken());
                        put("platform", "amazon.com");
                        put("marketplaceIds", countryCode);
                        putAll((Map<String, Object>) LWA_res);
                    }}
            );
            System.out.println("saved to sql server");

            // Construct the response message
            String message = "Exchange spapi_oauth_code for LWA refresh token succeeded."
                    + sqlSaveReturnMessage ;

            if (sellerState.getFromAppFlag() != null && sellerState.getFromAppFlag() == 1) {
                return ResponseEntity.ok("OAuth for selling_partner_id " + sellingPartnerId + " succeeded.");
            }
            Map<String, Object> redirectParams = new HashMap<>();
            if (sellerState.getCtoken() != null) {
                redirectParams.put("ctoken", sellerState.getCtoken());
            }
            redirectParams.put("user_email", sellerState.getUserEmail());
            System.out.println(sellerState.getUserEmail());
            redirectParams.put("redirect", true);
            User user = userRepository.findByEmail(sellerState.getUserEmail());
            // 构建重定向URI
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, Object> entry : redirectParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8.name());
                String encodedValue = URLEncoder.encode(value.toString(), StandardCharsets.UTF_8.name());
                queryString.append(encodedKey).append("=").append(encodedValue).append("&");
            }
            String encodedParams = queryString.toString();
            if (encodedParams.endsWith("&")) {
                encodedParams = encodedParams.substring(0, encodedParams.length() - 1);
            }

//            String redirectUri = "https://api.tolosupplychains.com/"
//                    + "?" + encodedParams;
            String redirectUri = "https://api.tolosupplychains.com/home/" + user.getId();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUri)
                    .build();
        }


        // Return a success response
        return ResponseEntity.ok("OAuth for selling_partner_id " + sellingPartnerId + " succeeded.");
    }

//    @PostMapping("/Login")
//    public ResponseEntity<Object> login(@RequestBody Map<String, Object> requestData) {
//        if (requestData.containsKey("redirect")) {
//            // 从授权页返回
//            // 处理第一个页面的逻辑
//            return ResponseHandler.generateResponse("Redirecting to Info page", HttpStatus.OK, requestData);
//        } else {
//            // 带登录参数的URL，可能不带
//            // 处理登录页面的逻辑
//            return ResponseHandler.generateResponse("Login page", HttpStatus.OK, requestData);
//        }
//    }

    /**
     * from kent
     */
    @PostMapping("/GetOpenUrl")
    public ResponseEntity<Object> login( @RequestBody Map<String, Object> requestData) {
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource("classpath:application-domainMappings.yml");
            InputStream inputStream = resource.getInputStream();

            // 解析YAML文件
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);

            // 访问domainMappings
            List<Map<String, Object>> domainMappings = (List<Map<String, Object>>) data.get("domainMappings");

            // 根据countryCode查找对应的region和domainSuffix
            String countryCode = (String) requestData.getOrDefault("countryCode", "US");
            String region = null;
            String domainSuffix = null;

            for (Map<String, Object> mapping : domainMappings) {
                String code = (String) mapping.get("countryCode");
                if (code.equals(countryCode)) {
                    region = (String) mapping.get("region");
                    domainSuffix = (String) mapping.get("domainSuffix");
                    break;
                }
            }

            if (region != null && domainSuffix != null) {
                // 执行相应的逻辑，使用找到的region和domainSuffix
                System.out.println("Region: " + region);
                System.out.println("Domain Suffix: " + domainSuffix);
            } else {
                // 未找到匹配的countryCode
                System.out.println("No mapping found for countryCode: " + countryCode);
            }

            if (requestData != null && requestData.containsKey("from_app") && (boolean) requestData.get("from_app")) {
                Seller seller = new Seller();

                seller.setUserEmail((String) requestData.getOrDefault("user_email", ""));
                seller.setCountryCode((String) requestData.getOrDefault("countryCode", ""));
                seller.setRegion(region);
                seller.setDominSuffix(domainSuffix);

                String amazonUrl = sellerStateService.generateUrlBySeller(seller);
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("open_url", amazonUrl);
                return ResponseEntity.ok().body(responseMap);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("no params or from_app is error");
            }
        } catch (Exception e) {
            // 处理异常情况
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }

    }
}
