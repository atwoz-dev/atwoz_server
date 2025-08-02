package atwoz.atwoz.datingexam.domain;

import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;

public interface DatingExamAnswerEncoder {
    String encode(DatingExamSubmitRequest request);
}
