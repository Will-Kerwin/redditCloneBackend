package co.uk.itskerwin.redditClone.exception;

public class SubredditNotFoundException extends RuntimeException {
    public SubredditNotFoundException(String exMessage) {
        super(exMessage);
    }
}
