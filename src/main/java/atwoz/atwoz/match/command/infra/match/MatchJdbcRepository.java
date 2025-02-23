package atwoz.atwoz.match.command.infra.match;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final static int LOCK_HOLD_TIME = 10;

    public void getLock(String key) {
        String sql = "SELECT GET_LOCK(?, ?)";
        jdbcTemplate.queryForObject(sql, Boolean.class, key, LOCK_HOLD_TIME);
    }

    public void releaseLock(String key) {
        String sql = "SELECT RELEASE_LOCK(?)";
        jdbcTemplate.queryForObject(sql, Boolean.class, key);
    }
}
