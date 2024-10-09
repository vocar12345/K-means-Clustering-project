package distributedServer;

import data.DataSet;
import data.TestResult;
import services.SequentialService;
import services.distributed.dataPackages.DataPackage;
import services.distributed.dataPackages.ResultDataPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CalculateThread implements Runnable {

    private final Socket socket;
    private ObjectOutputStream outSocket;
    private ObjectInputStream inSocket;

    private SequentialService sequentialService;
    private DataSet dataSet;

    static final String filePath = "data/germany.json";


    public CalculateThread(Socket socket) {
        super();
        this.socket = socket;
        this.dataSet = new DataSet(filePath); //instead of sending all data through the socket the server itself will get the data
        this.sequentialService = new SequentialService(dataSet);
    }


    @Override
    public void run() {
        try {
            //open sockets
            outSocket = new ObjectOutputStream(socket.getOutputStream());
            inSocket = new ObjectInputStream(socket.getInputStream());

            //set up all the needed data for clustering
            DataPackage dataPackage = (DataPackage) inSocket.readObject();
            sequentialService.setTestingType(dataPackage.getTestingType());
            sequentialService.setRunTimeBlock(dataPackage.getRunTimeBlock());
            sequentialService.setNumberOfClustersAndSites(dataPackage.getNumberOfClusters(), dataPackage.getNumberOfSites());

            TestResult testResult;

            //run clustering
            if (dataPackage.isTesting()) {
                sequentialService.runTesting();

                //build result package and send back to requester
                ResultDataPackage resultDataPackage = new ResultDataPackage(
                        sequentialService.getNumberOfClusters(),
                        sequentialService.getNumberOfSites(),
                        sequentialService.getTotalRunTime(),
                        sequentialService.getTestCyclesCounter(),
                        sequentialService.getResultMap()
                );
                outSocket.writeObject(resultDataPackage);
            } else {
                testResult = sequentialService.calculateKMeans();
                outSocket.writeObject(testResult);
            }

            outSocket.flush();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inSocket != null) inSocket.close();
                if (outSocket != null) outSocket.close();
                if (this.socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
