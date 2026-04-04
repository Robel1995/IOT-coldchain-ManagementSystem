package IOT.coldchain.domain.exception;

/**
 * EXCEPTION — DomainException (Base)
 *
 * The base class for all domain-specific exceptions in the EPSS cold-chain system.
 *
 * Why custom exceptions?
 *  Using raw IllegalStateException and IllegalArgumentException everywhere works,
 *  but loses the ability to distinguish "a business rule was violated" from
 *  "a programming error occurred." Custom domain exceptions make the distinction
 *  explicit and allow the API layer's GlobalExceptionHandler to map them to the
 *  correct HTTP status codes consistently.
 *
 * DIP compliance: these exceptions live in the domain layer.
 * The API layer catches them — the domain never catches from the API layer.
 *
 * Hierarchy:
 *   DomainException (base)
 *     ├─ BusinessRuleViolationException  (400 Bad Request  / 422 Unprocessable)
 *     ├─ AggregateNotFoundException      (404 Not Found)
 *     └─ PhysicsViolationException       (400 Bad Request)
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}