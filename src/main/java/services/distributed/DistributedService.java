package services.distributed;

import data.DataSet;
import data.TestResult;
import services.ClusteringService;
import services.TestingType;
import services.distributed.dataPackages.DataPackage;
import services.distributed.dataPackages.ResultDataPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Map;

public class DistributedService implements ClusteringService {

    //Starting vars
    private final DataSet data;
    private TestingType testingType = null;
    private int numberOfClusters;
    private int numberOfSites;

    //Testing vars
    private double runTimeBlock;//sec

    //Server vars
    private static final int SERVER_PORT = 9001;
    private static Socket socket = null;
    private ObjectOutputStream outSocket;
    private ObjectInputStream inSocket;


    public DistributedService(DataSet data) throws IOException {
        this.data = data;
        socket = new Socket("localhost", SERVER_PORT);
        outSocket = new ObjectOutputStream(socket.getOutputStream());
        inSocket = new ObjectInputStream(socket.getInputStream());
    }


    @Override
    public void runTesting() {
        DataPackage dataPackage = new DataPackage(testingType, numberOfClusters, numberOfSites, runTimeBlock, true);

        try {
            //sent data package for server to calculate
            outSocket.writeObject(dataPackage);
            outSocket.flush();// The purpose of flush() is to ensure that any buffered data in the output stream is immediately sent to the underlying stream.

            //get the results after the server is finished to print
            ResultDataPackage resultDataPackage = (ResultDataPackage) inSocket.readObject();
            printTestResults(resultDataPackage);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TestResult calculateKMeans() {
        DataPackage dataPackage = new DataPackage(testingType, numberOfClusters, numberOfSites, runTimeBlock, false);
        TestResult testResult;
        try {
            //sent data package for server to calculate
            outSocket.writeObject(dataPackage);
            outSocket.flush();// The purpose of flush() is to ensure that any buffered data in the output stream is immediately sent to the underlying stream.

            //get the results after the server is finished to print
            testResult = (TestResult) inSocket.readObject();
//            printTestResults(resultDataPackage);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return testResult;
    }

    private void printTestResults(ResultDataPackage result) {
        DecimalFormat df = new DecimalFormat("#.#####");
        String cyanColorCode = "\u001B[36m", resetColorCode = "\u001B[0m";

        //Print results
        for (Map.Entry<Integer, TestResult> entry : result.getResultMap().entrySet()) {
            System.out.println("\n====== Test No: " + entry.getKey() + " ======");
            entry.getValue().printData();
            System.out.println("==================");
        }

        System.out.println(cyanColorCode + "\n* TEST RESULTS *\n" + resetColorCode);
        System.out.println("Total run time (sec): " + Double.parseDouble(df.format((result.getTotalRunTime()))));
        System.out.println("Total tests done: " + result.getTestsCycledCounter());
        System.out.println("Current number of clusters: " + result.getNumberOfClusters());
        System.out.println("Current number of sites: " + result.getNumberOfSites());
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
