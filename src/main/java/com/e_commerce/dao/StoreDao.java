package com.e_commerce.dao;

import com.e_commerce.entity.Store;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreDao extends JpaRepository<Store, Long> {
    List<Store> findByIsActive(Boolean active);


    @EntityGraph(attributePaths = {"admin"})
    List<Store> findAll();

    @Transactional
    @Modifying
    @Query("UPDATE Store c SET c.isActive = :value where c.id = :storeId")
    int disableStore(@Param("value") boolean value,
                            @Param("storeId") Long storeId
    );

    @Transactional
    @Modifying
    @Query("UPDATE Store c SET c.isActive = :value where c.id = :storeId")
    int enableStore(@Param("value") boolean value,
                     @Param("storeId") Long storeId
    );
}
