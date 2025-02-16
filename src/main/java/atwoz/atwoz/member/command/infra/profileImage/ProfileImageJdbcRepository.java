package atwoz.atwoz.member.command.infra.profileImage;

import atwoz.atwoz.member.command.domain.profileImage.ProfileImage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProfileImageJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<ProfileImage> profileImages) {

        String sql = "INSERT INTO profile_images (id, member_id, url, profile_order, is_primary, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "member_id = VALUES(member_id), url = VALUES(url), profile_order = VALUES(profile_order), is_primary = VALUES(is_primary), " +
                "created_at = VALUES(created_at), updated_at = VALUES(updated_at)";

        List<Object[]> batchArgs = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (ProfileImage profileImage : profileImages) {
            batchArgs.add(new Object[]{
                    profileImage.getId(),
                    profileImage.getMemberId(),
                    profileImage.getUrl(),
                    profileImage.getOrder(),
                    profileImage.isPrimary(),
                    now,
                    now
            });
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
