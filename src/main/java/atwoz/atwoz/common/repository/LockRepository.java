package atwoz.atwoz.common.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockRepository {
    private final JdbcTemplate jdbcTemplate;

    public void getLock(String key, int lockWaitingTime) {
        String sql = "SELECT GET_LOCK(?, ?)";
        jdbcTemplate.queryForObject(sql, Boolean.class, lockWaitingTime);
    }

    public void releaseLock(String key) {
        String sql = "SELECT RELEASE_LOCK(?)";
        jdbcTemplate.queryForObject(sql, Boolean.class, key);
    }
}
