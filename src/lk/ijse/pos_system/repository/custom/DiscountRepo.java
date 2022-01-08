package lk.ijse.pos_system.repository.custom;

import lk.ijse.pos_system.entity.Discount;
import lk.ijse.pos_system.repository.CrudRepo;

import java.sql.SQLException;

public interface DiscountRepo extends CrudRepo<Discount, String> {

    String getDiscount(String itemCode) throws SQLException, ClassNotFoundException;
}
