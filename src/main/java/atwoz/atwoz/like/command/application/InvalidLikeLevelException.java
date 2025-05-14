package atwoz.atwoz.like.command.application;

public class InvalidLikeLevelException extends RuntimeException {
    public InvalidLikeLevelException(String likeLevel) {
        super(likeLevel + "은(는) 유효하지 않은 관심도 레벨입니다.");
    }
}