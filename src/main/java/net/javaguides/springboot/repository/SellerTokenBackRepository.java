package net.javaguides.springboot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import net.javaguides.springboot.model.SellerTokenBack;
import java.util.ArrayList;
@Repository
public interface SellerTokenBackRepository extends JpaRepository<SellerTokenBack, Long>{
    // return a list find by seller_id
    ArrayList<SellerTokenBack> findBySellerId(String sellerId);
    SellerTokenBack findBySellingPartnerIdAndMarketplaceIdsAndSellerId(String sellingPartnerId, String marketplaceIds, String sellerId);
}
