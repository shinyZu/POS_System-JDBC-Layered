package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.util.CrudUtil;
import lk.ijse.pos_system.repository.custom.OrderDetailRepo;
import lk.ijse.pos_system.entity.OrderDetail;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDetailRepoImpl implements OrderDetailRepo {

    @Override
    public boolean add(OrderDetail orderDetail) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("INSERT INTO OrderDetail VALUES (?,?,?,?)",
                orderDetail.getOrderID(),
                orderDetail.getItemCode(),
                orderDetail.getOrderQTY(),
                orderDetail.getDiscount()
        );
    }

    @Override
    public boolean delete(OrderDetail orderToRemove) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("DELETE FROM OrderDetail WHERE itemCode = ? AND orderID = ?",orderToRemove.getItemCode(),orderToRemove.getOrderID());
    }

    @Override
    public boolean update(OrderDetail orderDetailToBeUpdated) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("UPDATE OrderDetail SET orderQTY = ?, discount = ? WHERE orderId = ? AND itemCode = ?",
                orderDetailToBeUpdated.getOrderQTY(),
                orderDetailToBeUpdated.getDiscount(),
                orderDetailToBeUpdated.getOrderID(),
                orderDetailToBeUpdated.getItemCode()
        );
    }

    @Override
    public int getOrderQTY(String itemCode) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery("SELECT orderQTY FROM OrderDetail WHERE itemCode = ?", itemCode);

        if (rst.next()) {
            return rst.getInt(1);
        }
        return 0;
    }

    @Override
    public boolean updateOrderQty(String orderId, String itemCode, int newOrderQty, double unitPrice, int packSize, int discountPerUnit) throws SQLException, ClassNotFoundException {
        double newDiscount = unitPrice * packSize * newOrderQty * discountPerUnit / 100;
        return CrudUtil.executeUpdate("UPDATE OrderDetail SET orderQTY = ?, discount = ? WHERE orderId = ? AND itemCode = ?",
                newOrderQty,
                newDiscount,
                orderId,
                itemCode);
    }
}
