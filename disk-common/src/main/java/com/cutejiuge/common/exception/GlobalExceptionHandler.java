package com.cutejiuge.common.exception;

import com.cutejiuge.common.response.Result;
import com.cutejiuge.common.response.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cutejiuge.common.interceptor.TraceIdInterceptor.getCurrentTraceId;

/**
 * 全局异常处理类
 *
 * @author cutejiuge
 * @since 2025/8/22 上午12:32
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Object> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage()).traceId(getTraceId(request));
    }

    /**
     * 处理参数验证异常（@Valid注解）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("参数验证异常: {} - {}", request.getRequestURI(), e.getMessage());

        List<Map<String, String>> errors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            Map<String, String> error = new HashMap<>();
            error.put("field", fieldError.getField());
            error.put("message", fieldError.getDefaultMessage());
            errors.add(error);
        }

        return Result.validationError("参数验证失败", errors).traceId(getTraceId(request));
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("参数绑定异常: {} - {}", request.getRequestURI(), e.getMessage());

        List<Map<String, String>> errors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            Map<String, String> error = new HashMap<>();
            error.put("field", fieldError.getField());
            error.put("message", fieldError.getDefaultMessage());
            errors.add(error);
        }

        return Result.validationError("参数绑定失败", errors).traceId(getTraceId(request));
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.warn("约束违反异常: {} - {}", request.getRequestURI(), e.getMessage());

        List<Map<String, String>> errors = e.getConstraintViolations().stream()
                .map(violation -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", getFieldName(violation));
                    error.put("message", violation.getMessage());
                    return error;
                })
                .collect(Collectors.toList());

        return Result.validationError("参数约束违反", errors).traceId(getTraceId(request));
    }

    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("方法参数类型不匹配异常: {} - {}", request.getRequestURI(), e.getMessage());

        String message = String.format("参数 '%s' 的值 '%s' 类型不正确，期望类型为 %s",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());

        return Result.error(ResultCode.PARAM_VALIDATION_ERROR.getCode(), message).traceId(getTraceId(request));
    }

    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("HTTP请求方法不支持异常: {} - {}", request.getRequestURI(), e.getMessage());

        String message = String.format("请求方法 '%s' 不支持，支持的方法: %s",
                e.getMethod(), String.join(", ", e.getSupportedMethods()));

        return Result.error(ResultCode.METHOD_NOT_SUPPORTED.getCode(), message).traceId(getTraceId(request));
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Object> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("404异常: {} - {}", request.getRequestURI(), e.getMessage());

        String message = String.format("请求路径 '%s' 不存在", e.getRequestURL());
        return Result.error(ResultCode.PATH_NOT_FOUND.getCode(), message).traceId(getTraceId(request));
    }

    /**
     * 处理JSON解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("JSON解析异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(ResultCode.JSON_PARSE_ERROR).traceId(getTraceId(request));
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("文件上传大小超限异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(ResultCode.FILE_SIZE_EXCEEDED.getCode(), "上传文件大小超过限制").traceId(getTraceId(request));
    }

    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleSQLException(SQLException e, HttpServletRequest request) {
        log.error("SQL异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ResultCode.DATABASE_ERROR).traceId(getTraceId(request));
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误，请联系管理员").traceId(getTraceId(request));
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(ResultCode.PARAM_VALIDATION_ERROR.getCode(), e.getMessage()).traceId(getTraceId(request));
    }

    /**
     * 处理非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleIllegalStateException(IllegalStateException e, HttpServletRequest request) {
        log.error("非法状态异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统状态异常，请稍后重试").traceId(getTraceId(request));
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {} - {}", request.getRequestURI(), e.getMessage(), e);

        // 如果是已知的业务异常，直接返回错误信息
        if (e.getMessage() != null && (
                e.getMessage().contains("Token") ||
                        e.getMessage().contains("验证码") ||
                        e.getMessage().contains("权限") ||
                        e.getMessage().contains("锁"))) {
            return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage()).traceId(getTraceId(request));
        }

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR).traceId(getTraceId(request));
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("未知异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR).traceId(getTraceId(request));
    }

    private String getTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null) {
            traceId = (String) request.getAttribute("traceId");
        }
        return traceId;
    }

    private String getFieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String[] paths = propertyPath.split("\\.");
        return paths[paths.length - 1];
    }
}
