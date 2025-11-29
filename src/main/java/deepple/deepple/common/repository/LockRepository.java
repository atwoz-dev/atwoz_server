package deepple.deepple.common.repository;

import deepple.deepple.common.exception.CannotGetLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockRepository {
    private static final int LOCK_WAITING_TIME = 3;
    private final JdbcTemplate jdbcTemplate;

    public void withNamedLock(String key, Runnable action) {
        validateKey(key);
        validateAction(action);
        try {
            getLock(key, LOCK_WAITING_TIME);
            action.run();
        } catch (DataAccessException e) {
            throw new CannotGetLockException(e);
        } finally {
            releaseLock(key);
        }
    }

    public void getLock(String key, int lockWaitingTime) {
        String sql = "SELECT GET_LOCK(?, ?)";
        jdbcTemplate.queryForObject(sql, Boolean.class, key, lockWaitingTime);
    }

    public void releaseLock(String key) {
        String sql = "SELECT RELEASE_LOCK(?)";
        jdbcTemplate.queryForObject(sql, Boolean.class, key);
    }

    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new CannotGetLockException(new IllegalArgumentException("Key cannot be null or empty"));
        }
    }

    private void validateAction(Runnable action) {
        if (action == null) {
            throw new CannotGetLockException(new IllegalArgumentException("Action cannot be null"));
        }
    }
}
