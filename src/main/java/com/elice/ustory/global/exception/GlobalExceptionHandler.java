package com.elice.ustory.global.exception;

import com.elice.ustory.global.exception.dto.ErrorResponse;
import com.elice.ustory.global.exception.model.ConflictException;
import com.elice.ustory.global.exception.model.CustomException;
import com.elice.ustory.global.exception.model.ForbiddenException;
import com.elice.ustory.global.exception.model.InternalServerException;
import com.elice.ustory.global.exception.model.UnsupportedMediaTypeException;
import com.elice.ustory.global.exception.model.NotFoundException;
import com.elice.ustory.global.exception.model.UnauthorizedException;
import com.elice.ustory.global.exception.model.UnsupportedMethodTypeException;
import com.elice.ustory.global.exception.model.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String VALIDATION_LOG_MESSAGE = "Validation error occurred: ";
    private static final String UNAUTHORIZED_LOG_MESSAGE = "Unauthorized error occurred: ";
    private static final String FORBIDDEN_LOG_MESSAGE = "Forbidden error occurred: ";
    private static final String NOT_FOUND_LOG_MESSAGE = "Not found error occurred: ";
    private static final String METHOD_NOT_ALLOWED_LOG_MESSAGE = "Method not allowed: ";
    private static final String CONFLICT_LOG_MESSAGE = "Conflict error occurred: ";
    private static final String UNSUPPORTED_MEDIA_TYPE_LOG_MESSAGE = "Unsupported Media Type error occurred: ";
    private static final String INTERNAL_SERVER_LOG_MESSAGE = "Internal server error occurred: ";

    private static final String METHOD_NOT_ALLOWED_MESSAGE = "Method not allowed. Allowed method: %s";


    /** Validation Exception
     *
     *  필수 파라미터를 기입하지 않은 경우 UnsatisfiedServletRequestParameterException 을 통해 핸들링
     *  Valid 애너테이션을 사용한 경우 MethodArgumentNotValidException 을 통해 핸들링
     * */
    @ExceptionHandler({
            ValidationException.class,
            UnsatisfiedServletRequestParameterException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {

        printWarnLog(VALIDATION_LOG_MESSAGE, ex);

        CustomException customException;

        if (ex instanceof UnsatisfiedServletRequestParameterException){
            // TODO: "Parameter conditions \"diaryId\" not met for actual request parameters: " detail 메세지 수정 해야함
            customException = new ValidationException(ex.getMessage(), ErrorCode.MISSING_REQUIRED_PARAMETER);

        } else if (ex instanceof MethodArgumentNotValidException) {
            customException = new ValidationException(ex.getMessage(), ErrorCode.VALIDATION_PARAMETER_EXCEPTION);

        } else {
            customException = (ValidationException) ex;
        }

        ErrorResponse response = new ErrorResponse(customException);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /** Unauthorized Exception */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(UnauthorizedException ex) {

        printWarnLog(UNAUTHORIZED_LOG_MESSAGE, ex);

        return new ResponseEntity<>(new ErrorResponse(ex), HttpStatus.UNAUTHORIZED);
    }

    /** Forbidden Exception */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {

        printWarnLog(FORBIDDEN_LOG_MESSAGE, ex);

        return new ResponseEntity<>(new ErrorResponse(ex), HttpStatus.FORBIDDEN);
    }

    /** Not Found Exception
     *
     * API URL 을 찾을수 없는 경우는 NoHandlerFoundException 을 통해 핸들링
     * */
    @ExceptionHandler({NotFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception ex) {

        printWarnLog(NOT_FOUND_LOG_MESSAGE, ex);

        CustomException customException;
        if (ex instanceof NoHandlerFoundException) {
            customException = new NotFoundException(ex.getMessage(), ErrorCode.NOT_FOUND_EXCEPTION);
        } else {
            customException = (NotFoundException) ex;
        }

        ErrorResponse response = new ErrorResponse(customException);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /** Conflict Exception */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {

        printWarnLog(CONFLICT_LOG_MESSAGE, ex);

        return new ResponseEntity<>(new ErrorResponse(ex), HttpStatus.CONFLICT);
    }

    /** Unsupported Media Type Exception */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {

        printWarnLog(UNSUPPORTED_MEDIA_TYPE_LOG_MESSAGE, ex);

        CustomException ex2 = new UnsupportedMediaTypeException(ex.getMessage());

        return new ResponseEntity<>(new ErrorResponse(ex2), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /** 잘못된 메서드로 요청 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException ex) {

        printWarnLog(METHOD_NOT_ALLOWED_LOG_MESSAGE, ex);

        // 사용 가능한 메서드를 알려주는 메세지 작성
        String message = String.format(METHOD_NOT_ALLOWED_MESSAGE, ex.getSupportedHttpMethods());

        CustomException ex2 = new UnsupportedMethodTypeException(message);

        // 405 상태코드는 Allow 헤더를 포함시켜야 한다.
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header("Allow", ex.getSupportedHttpMethods().toString())
                .body(new ErrorResponse(ex2, message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        CustomException customException = new ValidationException(ex.getMessage(), ErrorCode.PARAMETER_INCORRECT_FORMAT);

        ErrorResponse errorResponse = new ErrorResponse(customException);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /** 예외 처리를 벗어난 서버 에러 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerException(Exception ex) {

        printErrorLog(INTERNAL_SERVER_LOG_MESSAGE, ex);

        CustomException ex2 = new InternalServerException(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex2), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void printWarnLog(String message, Exception ex) {
        logger.warn(message, ex);
    }

    private void printErrorLog(String message, Exception ex) {
        logger.error(message, ex);
    }
}
