package net.thumbtack.forums.controller;

import net.thumbtack.forums.dto.response.exception.ErrorDto;
import net.thumbtack.forums.dto.response.exception.ExceptionDto;
import net.thumbtack.forums.error.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@EnableWebMvc
public class GlobalControllerExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ExceptionDto handleFieldValidation(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        List<FieldError> fields = ex.getBindingResult().getFieldErrors();
        List<ErrorDto> errorDtoList = new ArrayList<>();
        for (int i = 0; i < errors.size(); i++) {
            String message = errors.get(i).getDefaultMessage();
            String field = fields.get(i).getField();
            errorDtoList.add(new ErrorDto("INVALID_FIELD", field, message));
        }
        return new ExceptionDto(errorDtoList);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ExceptionDto handleFieldValidation(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> errors = ex.getConstraintViolations();
        List<ErrorDto> errorDtoList = new ArrayList<>();
        for (ConstraintViolation violation : errors) {
            String message = violation.getMessage();
            String field = violation.getPropertyPath().toString();
            field = field.substring(field.indexOf(".") + 1);
            errorDtoList.add(new ErrorDto("INVALID_REQUEST_PARAM", field, message));
        }
        return new ExceptionDto(errorDtoList);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ExceptionDto handleFieldValidation(MethodArgumentTypeMismatchException ex) {
        ErrorDto errorDto = new ErrorDto("INVALID_URL", "URL", "Invalid URL variable!");
        return new ExceptionDto(Collections.singletonList(errorDto));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServerException.class)
    @ResponseBody
    public ExceptionDto handleServerException(ServerException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getError().name(), ex.getError().getField(), ex.getError().getMessage());
        return new ExceptionDto(Collections.singletonList(errorDto));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ExceptionDto handleNotFoundException() {
        ErrorDto errorDto = new ErrorDto("NOT_FOUND", "URL", "Invalid url!");
        return new ExceptionDto(Collections.singletonList(errorDto));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ExceptionDto handleBadJsonException(HttpMessageNotReadableException ex) {
        String message = "No JSON found!";
        Throwable exception = ex.getCause();
        if (exception != null) {
            message = exception.getMessage();
        }
        ErrorDto errorDto = new ErrorDto("INVALID_JSON", "Request Body", message);
        return new ExceptionDto(Collections.singletonList(errorDto));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseBody
    public ExceptionDto handleMissingCookieException() {
        ErrorDto errorDto = new ErrorDto("MISSING_COOKIE", "cookie", "Cookie required for this operation!");
        return new ExceptionDto(Collections.singletonList(errorDto));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ExceptionDto handleMethodNotSupportedException() {
        ErrorDto errorDto = new ErrorDto("INVALID_METHOD", "", "HTTP method is not supported for this URL!");
        return new ExceptionDto(Collections.singletonList(errorDto));
    }

  /*  @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public void handleMissingCookieException(Exception ex){
        System.out.println("test");
    }*/
}
