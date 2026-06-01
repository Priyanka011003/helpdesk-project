package com.example.helpdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.helpdesk.entity.Asset;

@Repository
public interface AssetRepository
        extends JpaRepository<Asset, Long> {

    boolean existsByAssetId(String assetId);

    List<Asset> findByAssetNameContainingIgnoreCase(
            String keyword);
}