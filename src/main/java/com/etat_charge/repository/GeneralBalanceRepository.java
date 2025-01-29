package com.etat_charge.repository;

import com.etat_charge.entity.GeneralBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GeneralBalanceRepository extends JpaRepository<GeneralBalance, Integer> {

    @Query("SELECT b FROM GeneralBalance b WHERE CAST(b.account AS string) LIKE '6%'")
    List<GeneralBalance> findByAccountStartingWithSix();
}
