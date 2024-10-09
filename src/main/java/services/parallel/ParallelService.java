package services.parallel;

import data.Centroid;
import data.DataSet;
import data.Site;
import data.TestResult;
import services.ClusteringService;
import services.TestingType;
import services.parallel.workers.CentroidRecomputeWorker;
import services.parallel.workers.ClusteringWorker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ParallelService implements ClusteringService {

    //Starting vars
    private final DataSet data;
    private TestingType testingType;
    private int numberOfClusters;
    private int numberOfSites;

    //Testing vars
    private final Map<Integer, TestResult> resultMap = new HashMap<>();
    private double runTimeBlock = 10.0;//sec
    private List<Site> sites;

    //Set up vars
    private static final int NUM_SITES_TO_INCREASE = 500;
    private static final int NUM_CLUSTERS_TO_INCREASE = 5;
    private static final Double PRECISION = 0.0;

    //Threads
    ExecutorService threadPool = Executors.newCachedThreadPool();
    private final ExecutorCompletionService<List<Site>> clusterCompletionService = new ExecutorCompletionService<>(threadPool);
    private final ExecutorCompletionService<Centroid> centroidCompletionService = new ExecutorCompletionService<>(threadPool);


    public ParallelService(DataSet data) {
        this.data = data;
    }

    @Override
    public void runTesting() {
        if (testingType == null) return;

        int testsCycledCounter = 0;
        double startTime, totalRunTime = 0.0;
        sites = data.getNSites(this.numberOfSites);

        System.out.println("Running tests...");

        while (true) {
            startTime = System.currentTimeMillis();

            TestResult testResult1 = calculateClusters(sites);//the entire testing process can be divided into 3 separate threads as well
            TestResult testResult2 = calculateClusters(sites);
            TestResult testResult3 = calculateClusters(sites);
            TestResult avgResult = new TestResult(testResult1, testResult2, testResult3);
            resultMap.put(testsCycledCounter++, avgResult);

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
        printTestResults(testsCycledCounter, totalRunTime);
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
        double newSSE;

        while (true) {
            //clustering is done with threads by dividing sites into batches and adding them to specific
            sites = calculateClustersThreaded(sites, centroids);

            // recompute centroids according to new cluster assignments
            centroids = recomputeCentroidsThreaded(numberOfClusters, sites);

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

    //this way isn't the most efficient because its creating new lists
    //thread safe list could be used but still isn't much better
    private List<Site> calculateClustersThreaded(List<Site> sites, List<Centroid> centroids) {
        int batchSize = 1000; //take 1000 sites for one thread
        List<Future<List<Site>>> clusteringFutureResults = new ArrayList<>();

        for (int i = 0; i < sites.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, sites.size());//get batch or remaining (if less than batch size)
            List<Site> sublist = sites.subList(i, endIndex);// Get the sublist of sites for the current batch
            clusteringFutureResults.add(this.clusterCompletionService.submit(new ClusteringWorker(sublist, centroids)));// sent a sub list to be clustered
        }
        return retrieveCalculatedClusters(clusteringFutureResults);
    }

    private List<Site> retrieveCalculatedClusters(List<Future<List<Site>>> futureResults) {
        if (futureResults == null) return null;
        List<Site> clusteredSites = new ArrayList<>();

        for (Future<List<Site>> futureSites : futureResults) {//go through all future results and get new list of clustered sites
            try {
                clusteredSites.addAll(futureSites.get());// .get() is blocking and will wait for the result, will not skip if it isn't finished
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return clusteredSites;
    }

    private List<Centroid> recomputeCentroidsThreaded(int numberOfClusters, List<Site> sites) {
        List<Future<Centroid>> centroidsFutureResults = new ArrayList<>();
        Map<Integer, List<Site>> sitesInCluster = new HashMap<>();
        for (int i = 0; i < numberOfClusters; i++) sitesInCluster.put(i, new ArrayList<>());

        for (Site site : sites) {//we divide sites into the map so we dont have to iterate through all sited for every cluster over and over
            sitesInCluster.get(site.getClusterNo()).add(site);
        }
        for (int i = 0; i < numberOfClusters; i++) {
            centroidsFutureResults.add(this.centroidCompletionService.submit(new CentroidRecomputeWorker(sitesInCluster.get(i))));
        }
        return retrieveCalculatedCentroids(centroidsFutureResults);
    }

    private List<Centroid> retrieveCalculatedCentroids(List<Future<Centroid>> futureResults) {
        if (futureResults == null) return null;
        List<Centroid> recomputedCentroids = new ArrayList<>();

        for (Future<Centroid> centroidFuture : futureResults) {
            try {
                recomputedCentroids.add(centroidFuture.get());// .get() is blocking and will wait for the result, will not skip if it isn't finished
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return recomputedCentroids;
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

}
