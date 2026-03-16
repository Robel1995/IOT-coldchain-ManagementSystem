package IOT.coldchain.application.port.in; import
        IOT.coldchain.application.command.InstallSensorCommand;

public interface InstallSensorUseCase {
    void handle(InstallSensorCommand command);
}