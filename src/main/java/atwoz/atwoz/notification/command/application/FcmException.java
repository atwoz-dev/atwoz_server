package atwoz.atwoz.notification.command.application;

import com.google.firebase.messaging.FirebaseMessagingException;

public class FcmException extends RuntimeException {

    public FcmException(FirebaseMessagingException cause) {
        super("FCM 전송 실패", cause);
    }

    @Override
    public synchronized FirebaseMessagingException getCause() {
        return (FirebaseMessagingException) super.getCause();
    }
}