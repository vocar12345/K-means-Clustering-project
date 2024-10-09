package services;

import data.TestResult;

public interface ClusteringService {

    void runTesting();

    TestResult calculateKMeans();

    void setNumberOfClustersAndSites(int numberOfClusters, int numberOfSites);

    void setTestingType(TestingType setTestingType);

    void setRunTimeBlock(double runTimeBlock);

}
