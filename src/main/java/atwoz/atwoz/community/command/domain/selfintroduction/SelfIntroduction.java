package atwoz.atwoz.community.command.domain.selfintroduction;

import atwoz.atwoz.common.entity.SoftDeleteBaseEntity;
import atwoz.atwoz.community.command.domain.selfintroduction.exception.InvalidSelfIntroductionContentException;
import atwoz.atwoz.community.command.domain.selfintroduction.exception.InvalidSelfIntroductionTitleException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "self_introductions",
    indexes = @Index(name = "idx_member_id", columnList = "memberId")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE self_introductions SET deleted_at = now() WHERE id = ?")
public class SelfIntroduction extends SoftDeleteBaseEntity {

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

    private boolean isOpened = Boolean.TRUE;

    private SelfIntroduction(@NonNull Long memberId, @NonNull String title, @NonNull String content) {
        this.memberId = memberId;
        setTitle(title);
        setContent(content);
    }

    public static SelfIntroduction write(Long memberId, String title, String content) {
        return new SelfIntroduction(memberId, title, content);
    }

    public void update(String title, String content) {
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
        if (title.isBlank()) {
            throw new InvalidSelfIntroductionTitleException();
        }
    }
}
