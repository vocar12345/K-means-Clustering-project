package services.distributed.dataPackages;

import services.TestingType;

import java.io.Serializable;

public class DataPackage implements Serializable {
    private TestingType testingType;
    private int numberOfClusters;
    private int numberOfSites;
    private double runTimeBlock;
    private boolean testing;

    public DataPackage(TestingType testingType, int numberOfClusters, int numberOfSites, double runTimeBlock, boolean testing) {
        this.testingType = testingType;
        this.numberOfClusters = numberOfClusters;
        this.numberOfSites = numberOfSites;
        this.runTimeBlock = runTimeBlock;
        this.testing = testing;
    }

    @Override
    public String toString() {
        return "DataPackage{" +
                ", testingType=" + testingType +
                ", numberOfClusters=" + numberOfClusters +
                ", numberOfSites=" + numberOfSites +
                ", testing=" + testing +
                '}';
    }

    public boolean isTesting() {
        return testing;
    }

    public double getRunTimeBlock() {
        return runTimeBlock;
    }

    public TestingType getTestingType() {
        return testingType;
    }

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public int getNumberOfSites() {
        return numberOfSites;
    }

}
