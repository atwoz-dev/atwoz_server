package atwoz.atwoz.community.command.domain.selfintroduction;

import atwoz.atwoz.community.command.domain.selfintroduction.exception.InvalidSelfIntroductionContentException;
import atwoz.atwoz.community.command.domain.selfintroduction.exception.InvalidSelfIntroductionTitleException;
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
    private String title;

    @Getter
    private String content;

    public static SelfIntroduction write(Long memberId, String title, String content) {
        return new SelfIntroduction(memberId, title, content);
    }

    private SelfIntroduction(@NonNull Long memberId, @NonNull String title, @NonNull String content) {
        validateTitle(title);
        validateContent(content);

        this.memberId = memberId;
        this.title = title;
        this.content = content;
    }

    private void validateContent(String content) {
        if (content.isBlank() || content.length() < 30) {
            throw new InvalidSelfIntroductionContentException();
        }
    }

    private void validateTitle(String title) {
        if (title.isBlank()) throw new InvalidSelfIntroductionTitleException();
    }
}
