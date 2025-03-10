package atwoz.atwoz.community.command.domain.introduction;

import atwoz.atwoz.common.entity.BaseEntity;
import atwoz.atwoz.community.command.domain.introduction.exception.InvalidIntroductionContentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "introductions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Introduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private Long memberId;

    @Getter
    private String content;

    public static Introduction write(@NonNull Long memberId, String content) {
        return new Introduction(memberId, content);
    }

    private Introduction(Long memberId, String content) {
        validateContent(content);
        this.memberId = memberId;
        this.content = content;
    }

    private void validateContent(String content) {
        if (content == null || content.isEmpty() || content.length() < 30)  {
            throw new InvalidIntroductionContentException();
        }
    }
}
