package atwoz.atwoz.datingexam.application.provided;

import atwoz.atwoz.datingexam.application.dto.DatingExamInfoResponse;

public interface DatingExamFinder {
    DatingExamInfoResponse findRequiredExamInfo();

    DatingExamInfoResponse findOptionalExamInfo();
}
