package atwoz.atwoz.block.application;

import atwoz.atwoz.block.application.required.BlockRepository;
import atwoz.atwoz.block.domain.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockModifyServiceTest {

    @InjectMocks
    private BlockModifyService blockModifyService;

    @Mock
    private BlockRepository blockRepository;

    @Nested
    @DisplayName("차단을 생성할 때")
    class CreateBlockTest {
        @Test
        @DisplayName("이미 차단된 멤버면 IllegalStateException 예외를 던진다.")
        void throwsIllegalStateExceptionWhenBlockAlreadyExists() {
            // given
            Long blockerId = 1L;
            Long blockedId = 2L;

            when(blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)).thenReturn(true);

            // when
            IllegalStateException result = assertThrows(IllegalStateException.class, () -> {
                blockModifyService.createBlock(blockerId, blockedId);
            });

            // then
            assertThat(result).isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("이미 차단된 멤버가 아니면 차단을 생성하고 저장한다.")
        void createsBlockWhenNotAlreadyBlocked() {
            // given
            Long blockerId = 1L;
            Long blockedId = 2L;

            when(blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)).thenReturn(false);

            try (MockedStatic<Block> blockMockedStatic = mockStatic(Block.class)) {
                Block mockBlock = mock(Block.class);
                blockMockedStatic.when(() -> Block.of(blockerId, blockedId)).thenReturn(mockBlock);

                when(blockRepository.save(mockBlock)).thenReturn(mockBlock);

                // when
                blockModifyService.createBlock(blockerId, blockedId);

                // then
                blockMockedStatic.verify(() -> Block.of(blockerId, blockedId), times(1));
                verify(blockRepository, times(1)).save(mockBlock);
            }
        }
    }
}