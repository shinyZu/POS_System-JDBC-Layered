package lk.ijse.pos_system.dto;

public class CustomDTO {

    private String custID;
    private String custTitle;
    private String custName;
    private String city;
    private double totalOrderCost;

    private String itemCode;
    private String description;
    private String packSize;
    private double unitPrice;
    private int orderQTY;
    private double subTotal;
    private double discount;
    private double total;

    private int qtyOnHand;

    private int salesQuantity;
    private double income;

    public CustomDTO() {
    }

    public CustomDTO(String custID, String custTitle, String custName, String city, double totalOrderCost) {
        this.setCustID(custID);
        this.setCustTitle(custTitle);
        this.setCustName(custName);
        this.setCity(city);
        this.setTotalOrderCost(totalOrderCost);
    }

    public CustomDTO(String itemCode, String description, String packSize, double unitPrice, int orderQTY, double subTotal, double discount, double total) {
        this.setItemCode(itemCode);
        this.setDescription(description);
        this.setPackSize(packSize);
        this.setUnitPrice(unitPrice);
        this.setOrderQTY(orderQTY);
        this.setSubTotal(subTotal);
        this.setDiscount(discount);
        this.setTotal(total);
    }

    public CustomDTO(String itemCode, String description, String packSize, double unitPrice, int qtyOnHand, double discount) {
        this.itemCode = itemCode;
        this.description = description;
        this.packSize = packSize;
        this.unitPrice = unitPrice;
        this.setQtyOnHand(qtyOnHand);
        this.discount = discount;
    }

    public CustomDTO(String itemCode, String description, int salesQuantity, double income) {
        this.itemCode = itemCode;
        this.description = description;
        this.setSalesQuantity(salesQuantity);
        this.setIncome(income);
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getCustTitle() {
        return custTitle;
    }

    public void setCustTitle(String custTitle) {
        this.custTitle = custTitle;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTotalOrderCost() {
        return totalOrderCost;
    }

    public void setTotalOrderCost(double totalOrderCost) {
        this.totalOrderCost = totalOrderCost;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getOrderQTY() {
        return orderQTY;
    }

    public void setOrderQTY(int orderQTY) {
        this.orderQTY = orderQTY;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public int getSalesQuantity() {
        return salesQuantity;
    }

    public void setSalesQuantity(int salesQuantity) {
        this.salesQuantity = salesQuantity;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    @Override
    public String toString() {
        return "CustomDTO{" +
                "itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", packSize='" + packSize + '\'' +
                ", unitPrice=" + unitPrice +
                ", orderQTY=" + orderQTY +
                ", subTotal=" + subTotal +
                ", discount=" + discount +
                ", total=" + total +
                '}';
    }
}
