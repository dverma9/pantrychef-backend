package com.pantrychef.repository;

import com.pantrychef.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findFirstBy();
}