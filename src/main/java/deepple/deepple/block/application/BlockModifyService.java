package deepple.deepple.block.application;

import deepple.deepple.block.application.provided.BlockCommander;
import deepple.deepple.block.application.required.BlockRepository;
import deepple.deepple.block.domain.Block;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockModifyService implements BlockCommander {
    private final BlockRepository blockRepository;

    @Override
    @Transactional
    public void createBlock(Long blockerId, Long blockedId) {
        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new IllegalStateException("이미 차단된 멤버입니다.");
        }
        Block block = Block.of(blockerId, blockedId);
        blockRepository.save(block);
    }
}
