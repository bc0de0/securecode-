package com.securecode.scanner;

import com.securecode.model.enums.AnalysisType;
import com.securecode.dto.FindingDTO;
import java.util.List;

public interface ScannerAdapter {
    List<FindingDTO> scan(String input, AnalysisType type);
}
