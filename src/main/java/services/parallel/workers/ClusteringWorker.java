package services.parallel.workers;

import data.Centroid;
import data.DataSet;
import data.Site;

import java.util.List;
import java.util.concurrent.Callable;

public class ClusteringWorker implements Callable<List<Site>> {

    private final List<Site> sites;
    private final List<Centroid> centroids;

    public ClusteringWorker(List<Site> sites, List<Centroid> centroids) {
        this.sites = sites;
        this.centroids = centroids;
    }

    @Override
    public List<Site> call() {
        double minDist, dist;

        for (Site site : sites) {
            minDist = Double.MAX_VALUE;
            for (int i = 0; i < centroids.size(); i++) {
                dist = DataSet.euclideanDistance(site, centroids.get(i));
                if (dist < minDist) {
                    minDist = dist;
                    site.setClusterNo(i);
                }
            }
        }
        return sites;
    }

}
