package IOT.coldchain.domain.exception;

/**
 * EXCEPTION — PhysicsViolationException
 *
 * Thrown when a sensor reading or threshold value violates a physical law.
 * Currently: temperature below absolute zero (−273.15°C).
 *
 * Mapped to HTTP 400 Bad Request by the GlobalExceptionHandler.
 * Usually indicates a faulty sensor or data corruption — not a user error.
 */
public class PhysicsViolationException extends DomainException {

    public PhysicsViolationException(String message) {
        super("Physics Violation: " + message);
    }
}