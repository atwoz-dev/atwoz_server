package atwoz.atwoz.datingexam.adapter.encoding;

import atwoz.atwoz.datingexam.domain.DatingExamAnswerEncoder;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatingExamEncoder implements DatingExamAnswerEncoder {
    private static final String ENCODED_SUBJECTS_KEY = "ss";
    private static final String ENCODED_SUBJECT_ID_KEY = "s";
    private static final String ENCODED_ANSWERS_KEY = "as";
    private static final String ENCODED_QUESTION_ID_KEY = "q";
    private static final String ENCODED_ANSWER_ID_KEY = "a";
    private final ObjectMapper objectMapper;

    @Override
    public String encode(DatingExamSubmitRequest request) {
        Map<String, Object> transformed = Map.of(
            ENCODED_SUBJECTS_KEY, request.subjects().stream().map(subject -> Map.of(
                ENCODED_SUBJECT_ID_KEY, subject.subjectId(),
                ENCODED_ANSWERS_KEY, subject.answers().stream().map(answer -> Map.of(
                    ENCODED_QUESTION_ID_KEY, answer.questionId(),
                    ENCODED_ANSWER_ID_KEY, answer.answerId()
                )).toList()
            )).toList()
        );
        try {
            return objectMapper.writeValueAsString(transformed);
        } catch (IOException e) {
            throw new DatingExamEncodingFailedException("답안 인코딩에 실패했습니다. " + e.getMessage());
        }
    }
}
