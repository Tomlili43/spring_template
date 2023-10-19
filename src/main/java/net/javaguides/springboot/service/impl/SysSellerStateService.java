package net.javaguides.springboot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.javaguides.springboot.model.Seller;
import net.javaguides.springboot.model.SellerState;
import net.javaguides.springboot.repository.SellerStateRepository;
import net.javaguides.springboot.service.ISysSellerStateService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SysSellerStateService implements ISysSellerStateService {

    @Autowired
    private Environment environment;
    @Autowired
    private SellerStateRepository stateRepository;
    private static final String SECRET_KEY = "phGfUTQOA8R8mr4ZL21EpT55tvWwBO8B"; // 32个字节的密钥 //这是随便写的一个对称密匙
    // 定义一个RestTemplate对象，用于发送HTTP请求
    private RestTemplate restTemplate = new RestTemplate();
    public String generateUrlBySeller(Seller seller) throws Exception {

        SellerState sellerState = this.encryptAndSaveState(seller);
        String appId = environment.getProperty("spring.datasource.APP_ID");
        Map<String, String> returnRes = new HashMap<>();
        returnRes.put("state", sellerState.getState());
        returnRes.put("application_id", appId);
        String returnResStr = String.join("&", returnRes.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new));
        return String.format("https://sellercentral.amazon%s/apps/authorize/consent?%s",
                sellerState.getDominSuffix(), returnResStr);
    }

    public Map generateStateBySeller(Seller seller) throws Exception {

        SellerState sellerState = this.encryptAndSaveState(seller);
        Map<String, String> returnRes = new HashMap<>();
        returnRes.put("state", sellerState.getState());
        return returnRes;
    }

    public SellerState encryptAndSaveState(Seller seller) throws Exception {

        // 将 seller 对象转换为 Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> sellerMap = objectMapper.convertValue(seller, Map.class);
        String encryptedState = this.encryptState(sellerMap);
        SellerState sellerState = new SellerState(seller);
        sellerState.setState(encryptedState);
        System.out.println("encryptAndSaveState sellerState: "+sellerState);
        System.out.println(stateRepository);
//        sellerStete.set;
//        stateRepository.save(sellerState);
        Optional<SellerState> existingState = Optional.ofNullable(stateRepository.findByState(sellerState.getState()));
        if (existingState.isPresent()) {
            // 更新现有记录
            System.out.println("update");
            SellerState existing = existingState.get();
            System.out.println(existing);
            existing.setState(encryptedState);
            stateRepository.save(existing);
            return existing;
        } else {
            // 插入新记录
            System.out.println("insert");
            stateRepository.save(sellerState);
            return sellerState;
        }
    }

    public Map<String, Object> decryptStateById(String stateId) throws Exception {
        Integer id = Integer.parseInt(stateId);
        Optional<SellerState> optionalState = stateRepository.findById(id);
        if (optionalState.isPresent()) {
            SellerState SellerStete = optionalState.get();
            String encryptedState = SellerStete.getState();
            // 省略解密逻辑
            Map<String, Object> decryptedState = this.decryptState(encryptedState);
            return decryptedState;
        } else {
            throw new IllegalArgumentException("Invalid state ID");
        }
    }


    public static String encryptState(Map<String, Object> state) throws Exception {
        String stateJson = new ObjectMapper().writeValueAsString(state);
        byte[] encryptedBytes = encrypt(stateJson.getBytes(StandardCharsets.UTF_8), SECRET_KEY);
        return Base64.encodeBase64URLSafeString(encryptedBytes);
    }

    public static Map<String, Object> decryptState(String encryptedState) throws Exception {
        byte[] encryptedBytes = Base64.decodeBase64(encryptedState);
        String decryptedJson = new String(decrypt(encryptedBytes, SECRET_KEY), StandardCharsets.UTF_8);
        return new ObjectMapper().readValue(decryptedJson, Map.class);
    }

    private static byte[] encrypt(byte[] input, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    private static byte[] decrypt(byte[] input, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }


    // 定义一个私有方法，用于与Amazon API交互，获取LWA刷新令牌
    public Object getLWARefreshToken(String redirectUri, String spapiOAuthCode, String sellingPartnerId, String domainSuffix) throws JsonProcessingException {
        // 定义URL，头部和参数
        String url = "https://api.amazon.com/auth/o2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String amznClientId = environment.getProperty("spring.datasource.AMAZON_CLIENT_ID");
        String amznClientSecret = environment.getProperty("spring.datasource.AMAZON_CLIENT_SECRET");
        System.out.println("getLWARefreshToken, redirect_uri " + redirectUri);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", spapiOAuthCode);
        params.add("redirect_uri", redirectUri);
        params.add("client_id", amznClientId);
        params.add("client_secret", amznClientSecret);

        // 创建一个HttpEntity对象，包含头部和参数
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // 发送POST请求，获取响应实体
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, requestEntity, Object.class);
        System.out.println(responseEntity.getStatusCodeValue());
        // 判断响应状态码是否为200
        if (responseEntity.getStatusCodeValue() == 200) {
            // 获取响应体中的数据，并添加一些额外的信息
            Object responseBody = responseEntity.getBody();
            if (responseBody instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) responseBody;
                map.put("client_id", amznClientId);
                map.put("client_secret", amznClientSecret);
                map.put("redirect_uri", "https://www.amazon" + domainSuffix + "/sp?ie=UTF8&seller=" + sellingPartnerId);
                map.put("create_token_time", LocalDateTime.now());
                System.out.println(map);
                return map;
            } else {
                System.out.println("Response body is not of type Map.");
                return false;
            }
        } else {
            System.out.println("POST request failed.");
            return false;
        }
    }


    public String send_message_to_seller_api(String selling_partner_id, String country_code, String region) {
        String url = "http://192.168.5.148:5001/AmznSellerAPI/fetchAll";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("seller_id", selling_partner_id);
        params.put("country_code", country_code);
        params.put("region", region);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return "该用户的数据获取请求已被接收。";
        } else {
            return "该用户的数据获取请求发送失败，response.status_code: " + responseEntity.getStatusCodeValue();
        }
    }
}
