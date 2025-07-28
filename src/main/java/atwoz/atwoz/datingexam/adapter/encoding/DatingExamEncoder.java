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
    private final ObjectMapper objectMapper;

    @Override
    public String encode(DatingExamSubmitRequest request) {
        Map<String, Object> transformed = Map.of(
            "ss", request.subjects().stream().map(subject -> Map.of(
                "s", subject.subjectId(),
                "as", subject.answers().stream().map(answer -> Map.of(
                    "q", answer.questionId(),
                    "a", answer.answerId()
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
