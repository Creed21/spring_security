package fon.master.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SimpleDataNotFoundException extends RuntimeException {
    public SimpleDataNotFoundException(String message) {
        super(message);
    }
}
