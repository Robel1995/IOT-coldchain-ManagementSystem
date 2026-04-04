package IOT.coldchain.application.query;

public class VaccineBoxDto {
    public String serialNumber;
    public String medicineName;
    public boolean ruined;

    public VaccineBoxDto(String serialNumber, String medicineName, boolean ruined){
        this.serialNumber = serialNumber;
        this.medicineName = medicineName;
        this.ruined = ruined;
    }
}
