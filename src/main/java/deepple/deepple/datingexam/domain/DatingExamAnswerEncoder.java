package deepple.deepple.datingexam.domain;

import deepple.deepple.datingexam.domain.dto.DatingExamSubmitRequest;

public interface DatingExamAnswerEncoder {
    String encode(DatingExamSubmitRequest request);
}
