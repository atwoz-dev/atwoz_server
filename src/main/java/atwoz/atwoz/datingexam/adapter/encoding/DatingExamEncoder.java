package atwoz.atwoz.datingexam.adapter.encoding;

import atwoz.atwoz.datingexam.domain.DatingExamAnswerEncoder;
import atwoz.atwoz.datingexam.domain.dto.DatingExamSubmitRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatingExamEncoder implements DatingExamAnswerEncoder {
    private static final String ENCODED_ANSWERS_KEY = "as";
    private static final String ENCODED_QUESTION_ID_KEY = "q";
    private static final String ENCODED_ANSWER_ID_KEY = "a";
    private final ObjectMapper objectMapper;

    @Override
    public String encode(DatingExamSubmitRequest request) {
        var answers = request.answers().stream()
            .sorted(Comparator.comparingLong(answer -> answer.questionId()))
            .map(answer -> {
                var linkedHashMap = new LinkedHashMap<String, Object>();
                linkedHashMap.put(ENCODED_QUESTION_ID_KEY, answer.questionId());
                linkedHashMap.put(ENCODED_ANSWER_ID_KEY, answer.answerId());
                return linkedHashMap;
            })
            .toList();

        var resultMap = new java.util.LinkedHashMap<String, Object>();
        resultMap.put(ENCODED_ANSWERS_KEY, answers);

        try {
            return objectMapper.writeValueAsString(resultMap);
        } catch (IOException e) {
            throw new DatingExamEncodingFailedException("답안 인코딩에 실패했습니다. " + e.getMessage());
        }
    }
}
