package xyz.giautm.tex.model;

import java.util.ArrayList;
import java.util.List;

/**
 * xyz.giautm.tex.models
 * 01/02/2016 - giau.tran.
 */
public class DeliveryMan {
    private String name;
    private String phone;
    private boolean isActive;
    private String tikiId;
    private List<Double> latLng;
    private long latLngUpdateAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getTikiId() {
        return tikiId;
    }

    public void setTikiId(String tikiId) {
        this.tikiId = tikiId;
    }

    public List<Double> getLatLng() {
        return latLng;
    }

    public void setLatLng(double lat, double lng) {
        List<Double> latLng = new ArrayList<>(2);
        latLng.add(lat);
        latLng.add(lng);
        this.latLng = latLng;
    }

    public void setLatLng(List<Double> latLng) {
        this.latLng = latLng;
    }

    public long getLatLngUpdateAt() {
        return latLngUpdateAt;
    }

    public void setLatLngUpdateAt(long latLngUpdateAt) {
        this.latLngUpdateAt = latLngUpdateAt;
    }
}
