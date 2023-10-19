package net.javaguides.springboot.service.impl;

import net.javaguides.springboot.model.SellerTokenBack;
import net.javaguides.springboot.repository.SellerTokenBackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class SysSellerTokenService {

    private final SellerTokenBackRepository sellerTokenRepository;

    @Autowired
    public SysSellerTokenService(SellerTokenBackRepository sellerTokenRepository) {
        this.sellerTokenRepository = sellerTokenRepository;
    }

    public String save_to_sql_server(Map<String, Object> params) {
        System.out.println(params);

        String sellingPartnerId = (String) params.get("selling_partner_id");
        String marketplaceIds = (String) params.get("marketplaceIds");
        String seller_id = (String) params.get("seller_id");

        Optional<SellerTokenBack> existingToken = Optional.ofNullable(sellerTokenRepository.findBySellingPartnerIdAndMarketplaceIdsAndSellerId(
                sellingPartnerId, marketplaceIds, seller_id));
        SellerTokenBack sellerToken;

        if (existingToken.isPresent()) {
            sellerToken = existingToken.get();
            sellerToken.setGmtCreated(Timestamp.valueOf((LocalDateTime) params.get("create_token_time")));
        } else {
            sellerToken = new SellerTokenBack();
            sellerToken.setSellerId((String) params.get("seller_id"));
            sellerToken.setSellingPartnerId(sellingPartnerId);
            sellerToken.setMarketplaceIds(marketplaceIds);
        }

        // 更新其他属性
        sellerToken.setSpapiOauthCode((String) params.get("spapi_oauth_code"));
        sellerToken.setCompanyToken((String) params.get("company_token"));
        sellerToken.setPlatform((String) params.get("platform"));
        sellerToken.setAccessToken((String) params.get("access_token"));
        sellerToken.setRefreshToken((String) params.get("refresh_token"));
        sellerToken.setTokenType((String) params.get("token_type"));
        sellerToken.setExpiresIn(((Integer) params.get("expires_in")).longValue());
        sellerToken.setClientId((String) params.get("client_id"));
        sellerToken.setClientSecret((String) params.get("client_secret"));
        sellerToken.setRedirectUrl((String) params.get("redirect_uri"));

        sellerTokenRepository.save(sellerToken);

        return existingToken.isPresent() ? "数据更新成功。" : "数据保存成功。";
    }
}