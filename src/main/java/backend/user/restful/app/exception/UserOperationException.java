package backend.user.restful.app.exception;

public class UserOperationException extends RuntimeException {
    public UserOperationException(String message) {
        super(message);
    }
}
