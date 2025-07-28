package atwoz.atwoz.datingexam.application.provided;

import atwoz.atwoz.datingexam.adapter.webapi.dto.DatingExamInfoResponse;

public interface DatingExamFinder {
    DatingExamInfoResponse findRequiredExamInfo();

    DatingExamInfoResponse findOptionalExamInfo();
}
