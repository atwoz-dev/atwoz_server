package atwoz.atwoz.datingexam.application.provided;

import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import jakarta.validation.Valid;

public interface DatingExamSubmitter {
    void submitRequiredSubject(@Valid DatingExamSubmitRequest submitRequest, long memberId);

    void submitOptionalSubject(@Valid DatingExamSubmitRequest submitRequest, long memberId);
}
