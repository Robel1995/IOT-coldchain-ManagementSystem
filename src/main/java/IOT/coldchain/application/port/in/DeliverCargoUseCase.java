package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.DeliverCargoCommand;

public interface DeliverCargoUseCase {
    void handle(DeliverCargoCommand command);
}