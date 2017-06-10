package com.example.esca.landmarket.models;

import java.util.ArrayList;


/**
 * Created by Boris on 06.05.2017.
 */

public class LandInfoArray {
    private ArrayList<LandInfo> lands = new ArrayList<>();

    public LandInfoArray(ArrayList<LandInfo> lands) {
        this.lands = lands;
    }

    public LandInfoArray() {
    }

    public int getSize() {
        return lands.size();
    }

    public ArrayList<LandInfo> getLands() {
        return lands;
    }

    public void setLands(ArrayList<LandInfo> lands) {
        this.lands = lands;
    }
}
