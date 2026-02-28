package com.securecode.repository;

import com.securecode.model.AnalysisRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AnalysisRequestRepository extends JpaRepository<AnalysisRequest, UUID> {
}
