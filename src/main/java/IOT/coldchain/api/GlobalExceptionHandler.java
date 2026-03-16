package IOT.coldchain.api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 404 — container / cargo not found
@ExceptionHandler(RuntimeException.class)
public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
}
// 422 — business rule violated (spoiled container, safe container deletion etc.)
 @ExceptionHandler(IllegalStateException.class)
 public ResponseEntity<Map<String, Object>> handleBusinessRule(IllegalStateException ex) {
    return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Business Rule Violation", ex.getMessage());
}
// 400 — bad input (min >= max temp, cargo not in container etc.)
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<Map<String, Object>> handleBadInput(IllegalArgumentException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
}
// 500 — truly unexpected crash (last resort catch-all)
@ExceptionHandler(Exception.class) public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred.");
    }
    // --- shared response builder ---
private ResponseEntity<Map<String, Object>> buildResponse( HttpStatus status, String error, String message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("status", status.value());
    body.put("error", error);
    body.put("message", message);
    return ResponseEntity.status(status).body(body);
}
}