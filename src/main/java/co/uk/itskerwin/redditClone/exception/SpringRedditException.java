package co.uk.itskerwin.redditClone.exception;

// creating custom exceptions is good
public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String exMessage) {
        super(exMessage);
    }
}
