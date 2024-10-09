package services.parallel.workers;

import data.Centroid;
import data.Site;

import java.util.List;
import java.util.concurrent.Callable;

public class CentroidRecomputeWorker implements Callable<Centroid> {

    List<Site> sitesInCluster;

    public CentroidRecomputeWorker(List<Site> sitesInCluster) {
        this.sitesInCluster = sitesInCluster;
    }

    //code taken form DataSet
    @Override
    public Centroid call() {
        double totalX = 0.0;
        double totalY = 0.0;

        for (Site site : sitesInCluster) {
            totalX += site.getLatitude();
            totalY += site.getLongitude();
        }

        double meanX = totalX / (double) sitesInCluster.size();
        double meanY = totalY / (double) sitesInCluster.size();

        if (Double.isNaN(meanX)) meanX = 0.0;//in case a cluster has no sites in it
        if (Double.isNaN(meanY)) meanY = 0.0;

        // Create a new centroid with the calculated mean coordinates
        return new Centroid(meanX, meanY);
    }


}
