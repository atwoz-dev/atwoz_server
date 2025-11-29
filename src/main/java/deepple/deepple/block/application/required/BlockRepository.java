package deepple.deepple.block.application.required;

import deepple.deepple.block.domain.Block;
import org.springframework.data.repository.Repository;

public interface BlockRepository extends Repository<Block, Long> {
    Block save(Block block);

    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}
