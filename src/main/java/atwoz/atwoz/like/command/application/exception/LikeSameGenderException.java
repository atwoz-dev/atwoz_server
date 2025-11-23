package atwoz.atwoz.like.command.application.exception;

public class LikeSameGenderException extends RuntimeException {
    public LikeSameGenderException(long senderId, long receiverId) {
        super("Cannot send like to the same gender. senderId: " + senderId + ", receiverId: " + receiverId);
    }
}
