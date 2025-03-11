package atwoz.atwoz.community.command.domain.selfintroduction;

import atwoz.atwoz.community.command.domain.selfintroduction.exception.InvalidSelfIntroductionContentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "self_introductions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfIntroduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private Long memberId;

    @Getter
    private String content;

    public static SelfIntroduction write(Long memberId, String content) {
        return new SelfIntroduction(memberId, content);
    }

    private SelfIntroduction(@NonNull Long memberId, @NonNull String content) {
        validateContent(content);
        this.memberId = memberId;
        this.content = content;
    }

    private void validateContent(String content) {
        if (content.isBlank() || content.length() < 30) {
            throw new InvalidSelfIntroductionContentException();
        }
    }
}
