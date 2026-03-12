package IOT.coldchain.application.query;

public class ContainerDto {
    public String containerId;
    public String status;

    public ContainerDto(String containerId, String status) {
        this.containerId = containerId;
        this.status = status;
    }
}