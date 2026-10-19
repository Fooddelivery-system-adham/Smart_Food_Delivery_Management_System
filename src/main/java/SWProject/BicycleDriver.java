package SWProject;

public class BicycleDriver extends Driver {
    public BicycleDriver(int id, String name, String status, double lat, double lon, double speed) {
        super(id, name, "BICYCLE", status, lat, lon, speed);
    }

    @Override
    public boolean canDeliver(double distanceKm) {
        return distanceKm <= 5.0;    
    }
}