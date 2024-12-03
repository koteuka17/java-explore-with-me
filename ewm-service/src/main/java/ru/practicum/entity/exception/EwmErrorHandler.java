package ru.practicum.entity.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class EwmErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenException(final ConflictException e) {
        log.error("error 409: {}: {}.", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(
                HttpStatus.CONFLICT.toString(),
                "Науршено ограничение целостности",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenException(final ConditionsNotMetException e) {
        log.error("error 409: {}: {}.", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(
                HttpStatus.CONFLICT.toString(),
                "Не выполнены условия для запрошенной операции",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError illegalArgumentHandle(final IllegalArgumentException e) {
        log.error("error 400: {}: {}.", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST.toString(),
                "Неверный запрос",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundHandle(final NotFoundException e) {
        log.error("error 404: {}: {}.", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(
                HttpStatus.NOT_FOUND.toString(),
                "Объект не найден",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError exceptionHandle(final Exception e) {
        log.error("error 500: {}: {}.", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Ошибка сервера",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBlankException(final MethodArgumentNotValidException e) {
        log.error("error 400: {}: {}.", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST.toString(),
                "Неверный запрос",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.error(e.getLocalizedMessage(), e.getMessage());
        log.error("error 400: {}: {}.", e.getClass().getSimpleName(), e.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST.toString(),
                "Неверный запрос",
                e.getMessage());
    }
}
