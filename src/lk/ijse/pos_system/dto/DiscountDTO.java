package lk.ijse.pos_system.dto;

public class DiscountDTO {

    private String itemCode;
    private String description;
    private String discount;

    public DiscountDTO() {}

    public DiscountDTO(String itemCode, String description, String discount) {
        this.setItemCode(itemCode);
        this.setDescription(description);
        this.setDiscount(discount);
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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "DiscountDTO{" +
                "itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", discount='" + discount + '\'' +
                '}';
    }
}
