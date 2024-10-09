//imports
import data.DataSet;
import data.TestResult;
import scene.WindowFrame;
import services.ClusteringService;
import services.SequentialService;
import services.TestingType;
import services.distributed.DistributedService;
import services.parallel.ParallelService;

import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static final String filePath = "data/germany.json";

    public static ClusteringService clusteringService;

    private static final Scanner scanner = new Scanner(System.in);
    //prints for aesthetic ui
    public static void main(String[] args) throws IOException {
        DataSet data = new DataSet(filePath);
        int parameter;

        System.out.println("\nWelcome to k-means waste facility clustering simulation.\nPlease select you desired simulation parameters.\n");
        System.out.println("""
                Select desired processing type (insert number):
                    1. Sequential
                    2. Parallel
                    3. Distributed (insure the server is running)
                    """);
        parameter = scanner.nextInt();

        System.out.println("Loading desired processing type...\n");
        //switch statement that creats an object and puts it in clusteringService interface
        switch (parameter) {
            case 1 -> clusteringService = new SequentialService(data);
            case 2 -> clusteringService = new ParallelService(data);
            case 3 -> clusteringService = new DistributedService(data);
        }

        //prints for testing/running
        System.out.println("""
                Select desired work (insert number):
                    1. Testing the algorithm
                    2. Running the visual simulation""");
        parameter = scanner.nextInt();
        //prints for desired testing type
        if (parameter == 1) {
            System.out.println("""
                    Select desired testing type (insert number):
                        1. Testing with LOCKED CLUSTERS and ramping sites
                        2. Testing with LOCKED SITES and ramping clusters""");
            parameter = scanner.nextInt();
            //prints for desired run time
            System.out.println("Please insert the desired run time in sec (preferable below 60)");
            clusteringService.setRunTimeBlock(scanner.nextInt());
            //prints for number of clusters and sites
            if (parameter == 1) {
                System.out.println("Please insert the desired number of clusters");
                clusteringService.setNumberOfClustersAndSites(scanner.nextInt(), 500);
                clusteringService.setTestingType(TestingType.LOCKED_CLUSTERS);
            } else if (parameter == 2) {
                System.out.println("Please insert the desired number of sites");
                clusteringService.setNumberOfClustersAndSites(5, scanner.nextInt());
                clusteringService.setTestingType(TestingType.LOCKET_SITES);
            }
            clusteringService.runTesting();
        } else if (parameter == 2) {
            System.out.println("Please insert the desired number of clusters");
            int clusters = scanner.nextInt();
            System.out.println("Please insert the desired number of sites (above 11k are randomly generated)");
            clusteringService.setNumberOfClustersAndSites(clusters, scanner.nextInt());

            TestResult testResult = clusteringService.calculateKMeans();
            testResult.printData();
            SwingUtilities.invokeLater(() -> new WindowFrame(testResult));
        }
    }
}