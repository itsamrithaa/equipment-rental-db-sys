public class Customer {
    private final int userId;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String email;
    private String startDate;         // store as ISO text for now
    private double warehouseDistance; // numeric distance
    private String nearestWarehouse;

    public Customer(int userId, String firstName, String lastName,
                    String address, String phone, String email,
                    String startDate, double warehouseDistance,
                    String nearestWarehouse) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.startDate = startDate;
        this.warehouseDistance = warehouseDistance;
        this.nearestWarehouse = nearestWarehouse;
    }

    // getters
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getStartDate() { return startDate; }
    public double getWarehouseDistance() { return warehouseDistance; }
    public String getNearestWarehouse() { return nearestWarehouse; }

    // setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setWarehouseDistance(double warehouseDistance) { this.warehouseDistance = warehouseDistance; }
    public void setNearestWarehouse(String nearestWarehouse) { this.nearestWarehouse = nearestWarehouse; }

    @Override
    public String toString() {
        return String.format(
            "UserID: %d | Name: %s %s | Address: %s | Phone: %s | Email: %s | Start: %s | Warehouse Dist: %.2f | Nearest Warehouse: %s",
            userId, firstName, lastName, address, phone, email, startDate, warehouseDistance, nearestWarehouse
        );
    }
}
