package SWProject;

public class CarDriver extends Driver {
    public CarDriver(int id, String name, String status, double lat, double lon, double speed) {
        super(id, name, "CAR", status, lat, lon, speed);
    }

    @Override
    public boolean canDeliver(double distanceKm) {
        return distanceKm <= 50.0;
    }
}