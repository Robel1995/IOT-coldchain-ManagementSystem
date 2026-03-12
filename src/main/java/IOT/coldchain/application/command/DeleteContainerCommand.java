package IOT.coldchain.application.command;

public class DeleteContainerCommand {
    public String containerId;
    public DeleteContainerCommand(String containerId){
        this.containerId=containerId;
    }
}
