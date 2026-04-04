package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.DeliverVaccineBoxCommand;

public interface DeliverCargoUseCase {
    void handle(DeliverVaccineBoxCommand command);
}