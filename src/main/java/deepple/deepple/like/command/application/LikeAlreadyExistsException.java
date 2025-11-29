package deepple.deepple.like.command.application;

public class LikeAlreadyExistsException extends RuntimeException {
    public LikeAlreadyExistsException(long senderId, long receiverId) {
        super("id: " + senderId + " -> id: " + receiverId + "로의 좋아요가 이미 존재합니다.");
    }
}