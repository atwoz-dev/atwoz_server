package atwoz.atwoz.like.command.application.exception;

public class LikeReceiverInactiveException extends RuntimeException {
    public LikeReceiverInactiveException(long receiverId) {
        super("Like receiver is inactive. receiverId: " + receiverId);
    }
}
