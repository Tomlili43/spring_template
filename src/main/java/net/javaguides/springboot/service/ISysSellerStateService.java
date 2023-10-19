package net.javaguides.springboot.service;

import net.javaguides.springboot.model.Seller;
import java.util.Map;

public interface ISysSellerStateService {

    public String generateUrlBySeller(Seller seller) throws Exception;
    public Map<String, Object> decryptStateById(String stateId) throws Exception;

}
