package lk.ijse.pos_system.repository.custom;

import lk.ijse.pos_system.entity.OrderDetail;
import lk.ijse.pos_system.repository.CrudRepo;

import java.sql.SQLException;

public interface OrderDetailRepo extends CrudRepo<OrderDetail, String> {

    int getOrderQTY(String itemCode) throws SQLException, ClassNotFoundException;

    boolean updateOrderQty(String orderId, String itemCode, int newOrderQty, double unitPrice, int packSize, int dicountPerUnit) throws SQLException, ClassNotFoundException;

}
