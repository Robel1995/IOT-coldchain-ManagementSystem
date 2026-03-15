package IOT.coldchain.domain.factory;

import IOT.coldchain.domain.entity.Container;
import IOT.coldchain.domain.enums.ContainerStatus;

public class ContainerFactory {
    // Factory Pattern
    public static Container createNew(String containerId, double minTemp, double maxTemp) {
        if (minTemp >= maxTemp) {
            throw new IllegalArgumentException("Min temp must be less than max temp");
        }

        // Pass null for the lists since a brand new container is empty!
        return Container.reconstitute(containerId, minTemp, maxTemp, ContainerStatus.SAFE, null, null);
    }
}