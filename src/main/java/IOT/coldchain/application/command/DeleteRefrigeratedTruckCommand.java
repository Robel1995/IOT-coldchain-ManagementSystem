package IOT.coldchain.application.command;

public class DeleteRefrigeratedTruckCommand {
    public String containerId;
    public DeleteRefrigeratedTruckCommand(String containerId){
        this.containerId=containerId;
    }
}
