package services.distributed.dataPackages;

import data.TestResult;

import java.io.Serializable;
import java.util.Map;

public class ResultDataPackage implements Serializable {
    private int numberOfClusters;
    private int numberOfSites;
    private double totalRunTime;
    private int testsCycledCounter;
    private final Map<Integer, TestResult> resultMap;


    public ResultDataPackage(int numberOfClusters, int numberOfSites, double totalRunTime, int testsCycledCounter, Map<Integer, TestResult> resultMap) {
        this.numberOfClusters = numberOfClusters;
        this.numberOfSites = numberOfSites;
        this.totalRunTime = totalRunTime;
        this.testsCycledCounter = testsCycledCounter;
        this.resultMap = resultMap;
    }

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public int getNumberOfSites() {
        return numberOfSites;
    }

    public double getTotalRunTime() {
        return totalRunTime;
    }

    public int getTestsCycledCounter() {
        return testsCycledCounter;
    }

    public Map<Integer, TestResult> getResultMap() {
        return resultMap;
    }
}
