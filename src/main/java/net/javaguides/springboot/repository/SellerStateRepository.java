package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.SellerState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SellerStateRepository extends JpaRepository<SellerState, Integer> {
    SellerState findByState(String state);
}