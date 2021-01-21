package co.uk.itskerwin.redditClone.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String exMessage) {
        super(exMessage);
    }
}
