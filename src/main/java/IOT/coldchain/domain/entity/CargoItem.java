package IOT.coldchain.domain.entity;

public class CargoItem {
    private String serialNumber;
    private String medicineName; // example "Polio Vaccines"
    private boolean isRuined;

    protected CargoItem(String serialNumber, String medicineName) {
        this.serialNumber = serialNumber;
        this.medicineName = medicineName;
        this.isRuined = false;
    }

    protected void markAsRuined() {
        this.isRuined = true;
    }
    public static CargoItem reconstitute(String serialNumber, String medicineName, boolean isRuined) {
        CargoItem item = new CargoItem(serialNumber, medicineName);
        item.isRuined = isRuined; // restore exact DB state
        return item;
    }

    public String getSerialNumber() { return serialNumber; }
    public String getMedicineName() { return medicineName; }
    public boolean isRuined() { return isRuined; }
}