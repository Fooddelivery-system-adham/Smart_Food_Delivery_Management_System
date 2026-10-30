package SWProject;

public class MotorcycleDriver extends Driver {
    public MotorcycleDriver(int id, String name, String status, double lat, double lon, double speed) {
        super(id, name, "MOTORCYCLE", status, lat, lon, speed);
    }

    @Override
    public boolean canDeliver(double distanceKm) {
        return distanceKm <= 15.0;     
    }
}