public class Customer {
    private String userId;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String email;
    private String startDate;         // keep as String for CP2 simplicity
    private double warehouseDistance; // numeric distance

    public Customer(String userId, String firstName, String lastName,
                    String address, String phone, String email,
                    String startDate, double warehouseDistance) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.startDate = startDate;
        this.warehouseDistance = warehouseDistance;
    }

    // getters
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getStartDate() { return startDate; }
    public double getWarehouseDistance() { return warehouseDistance; }

    // setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setWarehouseDistance(double warehouseDistance) { this.warehouseDistance = warehouseDistance; }

    @Override
    public String toString() {
        return String.format(
            "UserID: %s | Name: %s %s | Address: %s | Phone: %s | Email: %s | Start: %s | Warehouse Dist: %.2f",
            userId, firstName, lastName, address, phone, email, startDate, warehouseDistance
        );
    }
}