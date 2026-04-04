package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.AddVaccineBoxCommand;

public interface AddVaccineBoxUseCase {
    void handle(AddVaccineBoxCommand command);
}
