package IOT.coldchain.application.port.in;
import IOT.coldchain.application.command.UpdateContainerRangeCommand;

public interface UpdateContainerRangeUseCase {
    void handle(UpdateContainerRangeCommand command);
}