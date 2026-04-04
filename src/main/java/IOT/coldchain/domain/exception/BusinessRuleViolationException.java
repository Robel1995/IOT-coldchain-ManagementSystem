package IOT.coldchain.domain.exception;

/**
 * EXCEPTION — BusinessRuleViolationException
 *
 * Thrown when a domain operation is attempted that would violate a business rule.
 *
 * Examples:
 *  - Loading cargo into a SPOILED zone
 *  - Delivering vaccines from a SPOILED truck (patient safety)
 *  - Issuing a STOP command to a non-IN_TRANSIT truck
 *  - Deleting a truck that is not SPOILED
 *  - Installing a duplicate sensor (same MAC address)
 *
 * Mapped to HTTP 422 Unprocessable Entity by the GlobalExceptionHandler.
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String message) {
        super("Business Rule Violation: " + message);
    }
}