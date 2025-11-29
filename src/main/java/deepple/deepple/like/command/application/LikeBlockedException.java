package deepple.deepple.like.command.application;

public class LikeBlockedException extends RuntimeException {
    public LikeBlockedException(long senderId, long receiverId) {
        super("Like action is blocked between sender and receiver. senderId: " + senderId + ", receiverId: "
            + receiverId);
    }
}
