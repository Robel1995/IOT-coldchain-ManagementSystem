package IOT.coldchain.domain.exception;

/**
 * EXCEPTION — AggregateNotFoundException
 *
 * Thrown when a requested aggregate cannot be found in the repository.
 * E.g., querying for a truckId that does not exist.
 *
 * Mapped to HTTP 404 Not Found by the GlobalExceptionHandler.
 */
public class AggregateNotFoundException extends DomainException {

    public AggregateNotFoundException(String aggregateType, String id) {
        super(aggregateType + " with ID '" + id + "' was not found.");
    }
}