package IOT.coldchain.domain.factory;

import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.domain.enums.ContainerStatus;
import IOT.coldchain.domain.valueobject.TemperatureThreshold;

public class ContainerFactory {
    // Factory Pattern
    public static Container createNew(String containerId, double minTemp, double maxTemp) {
        TemperatureThreshold threshold = new TemperatureThreshold(minTemp,maxTemp);
        // Pass null for the lists since a brand-new container is empty
        return Container.reconstitute(containerId, threshold.getMinTemp(), threshold.getMaxTemp(), ContainerStatus.SAFE, null, null);
    }
}