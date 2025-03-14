package atwoz.atwoz.community.command.domain.selfintroduction;

import atwoz.atwoz.common.entity.BaseEntity;
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
public class SelfIntroduction extends BaseEntity {

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

    public void update(String title, String content) {
        setTitle(title);
        setContent(content);
    }

    private SelfIntroduction(@NonNull Long memberId, @NonNull String title, @NonNull String content) {
        this.memberId = memberId;
        setTitle(title);
        setContent(content);
    }

    private void setTitle(@NonNull String title) {
        validateTitle(title);
        this.title = title;
    }

    private void setContent(@NonNull String content) {
        validateContent(content);
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
