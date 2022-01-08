package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.util.CrudUtil;
import lk.ijse.pos_system.repository.custom.OrderRepo;
import lk.ijse.pos_system.entity.Orders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderRepoImpl implements OrderRepo {

    @Override
    public boolean add(Orders newOrder) throws SQLException, ClassNotFoundException {
        if (newOrder.getCustID().equals("")) {
            return CrudUtil.executeUpdate("INSERT INTO Orders (orderID,orderDate,orderCost) VALUES (?,?,?)",
                    newOrder.getOrderID(),
                    newOrder.getDate(),
                    newOrder.getOrderCost()
            );

        }

        return CrudUtil.executeUpdate("INSERT INTO Orders VALUES (?,?,?,?)",
                newOrder.getOrderID(),
                newOrder.getDate(),
                newOrder.getCustID(),
                newOrder.getOrderCost()
        );
    }

    @Override
    public boolean delete(Orders orderToRemove) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("DELETE FROM Orders WHERE  orderID = ?",orderToRemove.getOrderID());
    }

    @Override
    public boolean update(Orders orderToBeUpdated) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("UPDATE Orders SET orderCost = ? WHERE orderID = ?",
                orderToBeUpdated.getOrderCost(),
                orderToBeUpdated.getOrderID()
        );
    }

    @Override
    public String getOrderId() throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Orders ORDER BY orderId DESC LIMIT 1");

        if (rst.next()){
            int tempId = Integer.parseInt(rst.getString(1).split("-")[1]);
            tempId = tempId+1;

            if (tempId <= 9){
                return "O-00" + tempId;
            }else if (tempId <= 99){
                return "O-0" + tempId;
            }else {
                return "O-" + tempId;
            }

        }else {
            return "O-001";
        }
    }

    @Override
    public List<String> searchOrdersByCustID(String custID) throws SQLException, ClassNotFoundException {

        ArrayList<String> listOfOrders = new ArrayList();

        ResultSet rst = CrudUtil.executeQuery("SELECT orderId FROM Orders WHERE custID = ?", custID);
        while (rst.next()) {
            listOfOrders.add(rst.getString(1));
        }
        return listOfOrders;
    }

    @Override
    public boolean updateOrderCost(String orderIdSelected, double newOrderCost) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("UPDATE Orders SET orderCost = ? WHERE orderID = ?",newOrderCost,orderIdSelected);

    }

    @Override
    public String generateInvoiceId(String invoiceNo) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Orders ORDER BY orderId DESC LIMIT 1");

        if (rst.next()){
            String id = rst.getString(1).split("-")[1];
            id = "INV-"+id;
            return id;

        }else {
            return "INV-001";
        }
    }
}
