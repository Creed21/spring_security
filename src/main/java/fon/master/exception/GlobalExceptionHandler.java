package fon.master.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> handleException(Exception e) {
//        logger.info("Exception happened: " + e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sorry, something went wrong! :(");
//    }

    @ExceptionHandler(SimpleDataNotFoundException.class)
    public ResponseEntity<String> simpleDataNotFoundHandler(SimpleDataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

}
