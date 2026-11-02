package SWProject;

public class ElectricVehicleDriver extends Driver {
    public ElectricVehicleDriver(int id, String name, String status, double lat, double lon, double speed) {
        super(id, name, "ELECTRIC_VEHICLE", status, lat, lon, speed);
    }

    @Override
    public boolean canDeliver(double distanceKm) {
        return distanceKm <= 30.0;
    }
}