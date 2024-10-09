package data;

import java.io.Serializable;

public class Centroid implements Serializable {
    private Double latitude;
    private Double longitude;

    public Centroid(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "la: " + latitude + ", lo: " + longitude;
    }
}
