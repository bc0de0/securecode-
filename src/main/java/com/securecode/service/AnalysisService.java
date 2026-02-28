package com.securecode.service;

import com.securecode.dto.AnalysisRequestDTO;
import java.util.UUID;

public interface AnalysisService {
    AnalysisRequestDTO submitAnalysis(AnalysisRequestDTO dto);

    void processAnalysisAsync(UUID requestId);
}
