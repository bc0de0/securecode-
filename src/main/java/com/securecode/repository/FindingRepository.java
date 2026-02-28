package com.securecode.repository;

import com.securecode.model.Finding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FindingRepository extends JpaRepository<Finding, UUID> {
}
