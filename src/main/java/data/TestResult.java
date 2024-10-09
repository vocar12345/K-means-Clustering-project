package data;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestResult implements Serializable {

    private Map<Integer, Integer> clusterSizeCounter;
    private List<Centroid> centroids;
    private List<Site> sites;
    private int numberOfCycles;
    private int clusterNo;
    private double runtime;


    public TestResult(int numberOfCycles, Map<Integer, Integer> clusterSizeCounter, List<Centroid> centroids, int clusterNo, double runtime, List<Site> sites) {
        this.numberOfCycles = numberOfCycles;
        this.clusterSizeCounter = clusterSizeCounter;
        this.centroids = centroids;
        this.clusterNo = clusterNo;
        this.runtime = runtime;
        this.sites = sites;
    }

    //Get avg results from 3 tests ran
    public TestResult(TestResult testResult1, TestResult testResult2, TestResult testResult3) {
        DecimalFormat df = new DecimalFormat("#.#####");

        numberOfCycles = (testResult1.numberOfCycles + testResult2.numberOfCycles + testResult3.numberOfCycles) / 3;

        //in case something breaks and list are not same size
        if (testResult1.clusterSizeCounter.size() != testResult2.clusterSizeCounter.size() && testResult2.clusterSizeCounter.size() != testResult3.clusterSizeCounter.size()) {
            this.clusterSizeCounter = null;
        } else
            this.clusterSizeCounter = calculateMapMean(testResult1.clusterSizeCounter, testResult2.clusterSizeCounter, testResult3.clusterSizeCounter);

        if (testResult1.centroids.size() != testResult2.centroids.size() && testResult2.centroids.size() != testResult3.centroids.size()) {
            this.centroids = null;
        } else this.centroids = calculateListMean(testResult1.centroids, testResult2.centroids, testResult3.centroids);

        this.clusterNo = (testResult1.clusterNo + testResult2.clusterNo + testResult3.clusterNo) / 3;
        this.runtime = Double.parseDouble(df.format((testResult1.runtime + testResult2.runtime + testResult3.runtime) / 3));
        this.sites = testResult1.sites; //sites are the same for all 3 tests
    }

    private Map<Integer, Integer> calculateMapMean(Map<Integer, Integer> map1, Map<Integer, Integer> map2, Map<Integer, Integer> map3) {
        Map<Integer, Integer> meanMap = new HashMap<>();

        for (int key : map1.keySet()) {
            int meanValue = (map1.getOrDefault(key, 0) + map2.getOrDefault(key, 0) + map3.getOrDefault(key, 0)) / 3;
            meanMap.put(key, meanValue);
        }
        return meanMap;
    }

    private static List<Centroid> calculateListMean(List<Centroid> list1, List<Centroid> list2, List<Centroid> list3) {
        List<Centroid> meanList = new ArrayList<>();

        for (int i = 0; i < list1.size(); i++) {
            Centroid centroid1 = list1.get(i);
            Centroid centroid2 = list2.get(i);
            Centroid centroid3 = list3.get(i);
            double meanLatitude = (centroid1.getLatitude() + centroid2.getLatitude() + centroid3.getLatitude()) / 3.0;
            double meanLongitude = (centroid1.getLongitude() + centroid2.getLongitude() + centroid3.getLongitude()) / 3.0;
            Centroid meanCentroid = new Centroid(meanLatitude, meanLongitude);
            meanList.add(meanCentroid);
        }
        return meanList;
    }

    public void printData() {
        DecimalFormat df = new DecimalFormat("#.#####");
        Map<Integer, Double> clusterAvgCapacities = calculateAvgSiteCapacities();

        System.out.println("Cycles: " + numberOfCycles + " | Clusters: " + clusterNo + " | Sites: " + sites.size() + " | Runtime: " + runtime);

        System.out.println("\n*** Cluster Data: ***");
        for (Map.Entry<Integer, Integer> entry : clusterSizeCounter.entrySet()) {
            System.out.println("Cluster " + entry.getKey() + ": " + entry.getValue() + " sites" + " || avg capacity: " + Double.parseDouble(df.format((clusterAvgCapacities.get(entry.getKey())))));
        }


        System.out.println("\n*** Centroids: ***");
        for (int i = 0; i < centroids.size(); i++)
            System.out.println("Centroid " + i + " : " + centroids.get(i));
    }

    private Map<Integer, Double> calculateAvgSiteCapacities() {
        double avgCapacity;
        Map<Integer, Double> clusterAvgCapacities = new HashMap<>();
        for (int i = 0; i < sites.size(); i++) clusterAvgCapacities.put(i, 0.0);

        for (Site site : sites) {
            if (site.getClusterNo() == -1) {
                System.err.println("THERE IS AN UNCLUTTERED SITE!");
                continue;
            }
            avgCapacity = (clusterAvgCapacities.get(site.getClusterNo()) + site.getCapacity()) / 2;
            clusterAvgCapacities.put(site.getClusterNo(), avgCapacity);
        }
        return clusterAvgCapacities;
    }

    public List<Site> getSites() {
        return sites;
    }

    public List<Centroid> getCentroids() {
        return centroids;
    }

    public int getClusterNo() {
        return clusterNo;
    }

}
