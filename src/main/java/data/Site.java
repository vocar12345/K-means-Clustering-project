package data;

import java.io.Serializable;

public class Site implements Serializable {

    private String name;
    private double capacity;
    private double latitude;// X cord
    private double longitude;// Y cord
    private int clusterNo;// instead of keeping sites in clusters with a list/map we just mark the site instead


    public Site(String name, double capacity, double latitude, double longitude) {
        this.name = name;
        this.capacity = capacity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.clusterNo = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getClusterNo() {
        return clusterNo;
    }

    public void setClusterNo(int clusterNo) {
        this.clusterNo = clusterNo;
    }

    @Override
    public String toString() {
        return "WWSite{" +
                "name='" + name + '\'' +
                ", capacity=" + capacity +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", clusterNumber=" + clusterNo +
                '}';
    }
}
