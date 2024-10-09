package services;

import data.Centroid;
import data.DataSet;
import data.Site;
import data.TestResult;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequentialService implements ClusteringService {

    //Starting vars
    private final DataSet data;
    private TestingType testingType;
    private int numberOfClusters;
    private int numberOfSites;

    //Testing vars
    private final Map<Integer, TestResult> resultMap = new HashMap<>();
    private List<Site> sites;
    private double runTimeBlock = 10.0;//sec
    private double totalRunTime = 0.0;
    private int testCyclesCounter = 0;

    //Set up vars
    private static final Double PRECISION = 0.0;
    private static final int NUM_SITES_TO_INCREASE = 500;
    private static final int NUM_CLUSTERS_TO_INCREASE = 5;
    //makes data set in constructor
    public SequentialService(DataSet data) {

        this.data = data;
    }

    @Override
    public void runTesting() {
        if (testingType == null) return;

        double startTime;
        sites = data.getNSites(this.numberOfSites);

        System.out.println("Running tests...");

        while (true) {
            startTime = System.currentTimeMillis();

            TestResult testResult1 = calculateClusters(sites);
            TestResult testResult2 = calculateClusters(sites);
            TestResult testResult3 = calculateClusters(sites);
            TestResult avgResult = new TestResult(testResult1, testResult2, testResult3);
            resultMap.put(testCyclesCounter++, avgResult);

            totalRunTime += (System.currentTimeMillis() - startTime) / 1000.0;

            if (totalRunTime > runTimeBlock) {
                System.err.println("BREAK DUE TO EXCEEDING TIME LIMIT");
                break;
            }

            if (testingType == TestingType.LOCKED_CLUSTERS) {
                this.numberOfSites += NUM_SITES_TO_INCREASE;// test demands we increase the num of sites by N every test cycle
                sites = data.getNSites(this.numberOfSites);
            } else if (testingType == TestingType.LOCKET_SITES) {
                this.numberOfClusters += NUM_CLUSTERS_TO_INCREASE;// test demands we increase the num of sites by 500 every test cycle
                if (numberOfClusters >= numberOfSites / 3) {
                    System.err.println("BREAK DUE TO EXCEEDING CLUSTER LIMIT");
                    break;
                }
            }
        }
        printTestResults(testCyclesCounter, totalRunTime);
    }

    @Override
    public TestResult calculateKMeans() {
        System.out.println("Calculating...");
        sites = data.getNSites(this.numberOfSites);
        return calculateClusters(sites);
    }


    private TestResult calculateClusters(List<Site> sites) {
        //data vars
        double SSE = Double.MAX_VALUE;
        List<Centroid> centroids = data.getFirstCentroids(sites, numberOfClusters); //using different centroids in all 3 runs in a test

        //helper vars
        int cycleCounter = 0;
        long startTime = System.currentTimeMillis();
        Map<Integer, Integer> clusterSizeCounter = new HashMap<>();
        for (int i = 0; i < numberOfClusters; i++) clusterSizeCounter.put(i, 0);
        double minDist, dist, newSSE;

        while (true) {
            for (Site site : sites) {
                minDist = Double.MAX_VALUE;
                // find the centroid at a minimum distance from it and add the site to its cluster
                for (int i = 0; i < centroids.size(); i++) {
                    dist = DataSet.euclideanDistance(site, centroids.get(i));
                    if (dist < minDist) {
                        minDist = dist;
                        site.setClusterNo(i);
                    }
                }
            }

            // recompute centroids according to new cluster assignments
            centroids = DataSet.recomputeCentroids(numberOfClusters, sites);

            // exit condition, SSE changed less than PRECISION parameter
            newSSE = DataSet.calculateTotalSSE(centroids, sites);
            if (SSE - newSSE <= PRECISION) break;
            SSE = newSSE;

            cycleCounter++;
        }

        //counts how many sites in cluster
        for (Site site : sites)
            clusterSizeCounter.put(site.getClusterNo(), clusterSizeCounter.get(site.getClusterNo()) + 1);
        long totalTime = System.currentTimeMillis() - startTime;
        //return a result of testing (time is parsed so its not 10 decimals long)
        return new TestResult(cycleCounter, clusterSizeCounter, centroids, this.numberOfClusters, totalTime / 1000.0, sites);
    }


    private void printTestResults(int testsCycledCounter, double totalRunTime) {
        DecimalFormat df = new DecimalFormat("#.#####");
        String cyanColorCode = "\u001B[36m", resetColorCode = "\u001B[0m";

        //Print results
        for (Map.Entry<Integer, TestResult> entry : resultMap.entrySet()) {
            System.out.println("\n====== Test No: " + entry.getKey() + " ======");
            entry.getValue().printData();
            System.out.println("==================");
        }

        System.out.println(cyanColorCode + "\n* TEST RESULTS *\n" + resetColorCode);
        System.out.println("Total run time (sec): " + Double.parseDouble(df.format((totalRunTime))));
        System.out.println("Total tests done: " + testsCycledCounter);
        System.out.println("Current number of clusters: " + this.numberOfClusters);
        System.out.println("Current number of sites: " + this.numberOfSites);
    }


    @Override
    public void setNumberOfClustersAndSites(int numberOfClusters, int numberOfSites) {
        this.numberOfClusters = numberOfClusters;
        this.numberOfSites = numberOfSites;
    }

    @Override
    public void setTestingType(TestingType testingType) {
        this.testingType = testingType;
    }


    @Override
    public void setRunTimeBlock(double runTimeBlock) {
        this.runTimeBlock = runTimeBlock;
    }

    //these not in interface cuz they are only used once for distributed testing
    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public int getNumberOfSites() {
        return numberOfSites;
    }

    public double getTotalRunTime() {
        return totalRunTime;
    }

    public int getTestCyclesCounter() {
        return testCyclesCounter;
    }

    public Map<Integer, TestResult> getResultMap() {
        return resultMap;
    }
}
