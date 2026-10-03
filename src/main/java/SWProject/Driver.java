package SWProject;

public class Driver {
    private final int driverId;
    private final String name;
    private final String driverType;
    private String status;	
    private final double latitude;
    private final double longitude;
    private final double averageSpeedKmh;

    public Driver(int driverId, String name, String driverType, String status,
                  double latitude, double longitude, double averageSpeedKmh) {
        this.driverId = driverId;
        this.name = name;
        this.driverType = driverType;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageSpeedKmh = averageSpeedKmh;
    }

    public int getDriverId() { return driverId; }
    public String getName() { return name; }
    public String getDriverType() { return driverType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getAverageSpeedKmh() { return averageSpeedKmh; }

    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status);
    }
}