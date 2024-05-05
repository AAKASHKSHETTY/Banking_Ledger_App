package dev.codescreen.exceptions;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@AllArgsConstructor
public class InvalidUserException extends Exception{
    public InvalidUserException(String message){
        super(message);
    }
}
