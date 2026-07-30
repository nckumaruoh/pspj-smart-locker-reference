package edu.klh.pspj.smartlocker.persistence;

import edu.klh.pspj.smartlocker.model.Parcel;

import java.util.ArrayList;
import java.util.List;

public final class LoadResult {
    private final List<Parcel> parcels;
    private final List<String> warnings;

    public LoadResult(List<Parcel> parcels, List<String> warnings) {
        this.parcels = new ArrayList<>(parcels);
        this.warnings = new ArrayList<>(warnings);
    }

    public List<Parcel> getParcels() {
        return new ArrayList<>(parcels);
    }

    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }
}
