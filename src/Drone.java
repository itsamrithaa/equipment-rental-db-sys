public class Drone {
    private String serialNum;
    private String name;
    private String model;
    private String status;
    private String location;
    private String warrantyExp;
    private String warehouseAddress;

    public Drone(String serialNum, String name, String model, String status,
                 String location, String warrantyExp, String warehouseAddress) {
        this.serialNum = serialNum;
        this.name = name;
        this.model = model;
        this.status = status;
        this.location = location;
        this.warrantyExp = warrantyExp;
        this.warehouseAddress = warehouseAddress;
    }

    public String getSerialNum() { return serialNum; }
    public String getName() { return name; }
    public String getModel() { return model; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
    public String getWarrantyExp() { return warrantyExp; }
    public String getWarehouseAddress() { return warehouseAddress; }

    public void setSerialNum(String serialNum) { this.serialNum = serialNum; }
    public void setName(String name) { this.name = name; }
    public void setModel(String model) { this.model = model; }
    public void setStatus(String status) { this.status = status; }
    public void setLocation(String location) { this.location = location; }
    public void setWarrantyExp(String warrantyExp) { this.warrantyExp = warrantyExp; }
    public void setWarehouseAddress(String warehouseAddress) { this.warehouseAddress = warehouseAddress; }

    public String toString() {
        return String.format(
            "Drone Serial: %s | Name: %s | Model: %s | Status: %s | Location: %s | Warranty Exp: %s | Warehouse: %s",
            serialNum, name, model, status, location, warrantyExp, warehouseAddress
        );
    }
}
