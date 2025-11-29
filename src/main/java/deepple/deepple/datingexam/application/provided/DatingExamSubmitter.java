package deepple.deepple.datingexam.application.provided;

import deepple.deepple.datingexam.domain.dto.DatingExamSubmitRequest;
import jakarta.validation.Valid;

public interface DatingExamSubmitter {
    void submitSubject(@Valid DatingExamSubmitRequest submitRequest, long memberId);
}
