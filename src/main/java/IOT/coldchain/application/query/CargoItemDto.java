package IOT.coldchain.application.query;

public class CargoItemDto {
    public String serialNumber;
    public String medicineName;
    public boolean ruined;

    public CargoItemDto (String serialNumber, String medicineName, boolean ruined){
        this.serialNumber = serialNumber;
        this.medicineName = medicineName;
        this.ruined = ruined;
    }
}
