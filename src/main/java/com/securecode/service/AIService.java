package com.securecode.service;

import com.securecode.dto.AnalysisResultDTO;
import com.securecode.dto.FindingDTO;
import java.util.List;

public interface AIService {
    AnalysisResultDTO summarizeFindings(List<FindingDTO> findings, String contextDocs, String userAiContext);
}
