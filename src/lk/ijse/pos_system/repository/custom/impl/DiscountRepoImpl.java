package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.util.CrudUtil;
import lk.ijse.pos_system.repository.custom.DiscountRepo;
import lk.ijse.pos_system.entity.Discount;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscountRepoImpl implements DiscountRepo {

    @Override
    public boolean add(Discount newDiscount) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("INSERT INTO Discount (itemCode,description,discount) VALUES (?,?,?)",
                newDiscount.getItemCode(),
                newDiscount.getDescription(),
                newDiscount.getDiscount()
        );
    }

    @Override
    public boolean delete(Discount discount) throws SQLException, ClassNotFoundException {
        throw new UnsupportedOperationException("Not Supported Yet");
    }

    @Override
    public boolean update(Discount editDiscount) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery("SELECT itemCode,discount FROM Discount WHERE itemCode = ?", editDiscount.getItemCode());
        if (rst.next()) { // if Item is already specified with a discount
            if (rst.getString("discount").equals(editDiscount.getDiscount())) {
                return false;
            } else {
                return CrudUtil.executeUpdate("UPDATE Discount SET discount = ? WHERE itemCode = ?",editDiscount.getDiscount(),editDiscount.getItemCode());
            }
        } else { // if Item is specified a discount for the first time/ no discount specified earlier
            return add(editDiscount);
        }
    }

    @Override
    public String getDiscount(String itemCode) throws SQLException, ClassNotFoundException {

        ResultSet resultSet = CrudUtil.executeQuery("SELECT discount FROM Discount WHERE itemCode = ?", itemCode);

        if (resultSet.next()) {
            return resultSet.getString(1);

        } else {
            return "00";
        }
    }
}
