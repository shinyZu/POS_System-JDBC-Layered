package lk.ijse.pos_system.business.custom.impl;

import lk.ijse.pos_system.business.custom.ManageOrderBO;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.*;
import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.dto.OrderDTO;
import lk.ijse.pos_system.dto.OrderDetailDTO;
import lk.ijse.pos_system.entity.OrderDetail;
import lk.ijse.pos_system.entity.Orders;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageOrderBOImpl implements ManageOrderBO {

    private final CustomerRepo customerRepo = (CustomerRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.CUSTOMER);
    private final ItemRepo itemRepo = (ItemRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ITEM);
    private final OrderRepo orderRepo = (OrderRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDER);
    private final OrderDetailRepo orderDetailRepo = (OrderDetailRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDERDETAIL);
    private final QueryRepo queryRepo = (QueryRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.QUERY);
    private final DiscountRepo discountRepo = (DiscountRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.DISCOUNT);

    @Override
    public List<String> searchOrdersByCustID(String custID) throws SQLException, ClassNotFoundException {
        return orderRepo.searchOrdersByCustID(custID);
    }

    @Override
    public ArrayList<CustomDTO> getOrderedItems(String orderSelected, CustomDTO itemSelected) throws SQLException, ClassNotFoundException {
        return queryRepo.getOrderedItems(orderSelected, itemSelected);
    }

    @Override
    public ArrayList<Double> getOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException {
        return queryRepo.getOldPaymentInfo(orderSelected);
    }

    @Override
    public int splitPackSize(String itemCode, String packSize) throws SQLException, ClassNotFoundException {
        return itemRepo.splitPackSize(itemCode, packSize);
    }

    @Override
    public boolean deleteItemFromOrder(OrderDetailDTO dto) throws SQLException, ClassNotFoundException {
        return orderDetailRepo.delete(new OrderDetail(dto.getItemCode(), dto.getOrderID()));
    }

    @Override
    public boolean editQtyOnHand(String itemCode, int qtyToRestock) throws SQLException, ClassNotFoundException {
        return itemRepo.editQtyOnHand(itemCode, qtyToRestock);
    }

    @Override
    public void deleteOrder(OrderDTO dto) throws SQLException, ClassNotFoundException {
        orderRepo.delete(new Orders(dto.getOrderID()));
    }

    @Override
    public boolean updateOrder(OrderDTO orderDTO, OrderDetailDTO orderDetailDTO, int newQtyOnHand) throws SQLException, ClassNotFoundException {

        Connection con = null;

        Orders orders = new Orders(
                orderDTO.getOrderID(),
                orderDTO.getDate(),
                orderDTO.getCustID(),
                orderDTO.getOrderCost()
        );

        OrderDetail orderDetail = new OrderDetail(
                orderDetailDTO.getOrderID(),
                orderDetailDTO.getItemCode(),
                orderDetailDTO.getOrderQTY(),
                orderDetailDTO.getDiscount()
        );

        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false); // to pause the action of saving data to tables

            if (orderRepo.update(orders)) {  // will be true if the Orders table is updated, but doesnt save bcz setAutoCommit is false

                if (orderDetailRepo.update(orderDetail/*,newQtyOnHand*/)) { // will be true if the order detail table and Item table gets updated (but not visible bcz it doesnt actually save, bcz setAutoCommit is false) takes the data to the table to save but doesnt save

                    if (itemRepo.updateEditedQtyOnHand(orderDetail.getItemCode(), newQtyOnHand)) {
                        con.commit();
                        return true;

                    } else { // will be false if some issue is raised during updating Item table
                        con.rollback(); // send back the sent data bundle bcz of of some security issues/error
                        return false;
                    }

                } else { // will be false if some issue is raised during updating Order Detail Table
                    con.rollback(); // send back the sent data bundle bcz of of some security issues/error
                    return false;
                }
            } else {
                con.rollback();
                return false;
            }

        } finally {
            con.setAutoCommit(true);
        }
    }

    @Override
    public boolean updateOrderAndOrderDetail(OrderDTO orderDTO, OrderDetailDTO orderDetailDTO) throws SQLException, ClassNotFoundException {
        Connection con = null;

        Orders orders = new Orders(
                orderDTO.getOrderID(),
                orderDTO.getDate(),
                orderDTO.getCustID(),
                orderDTO.getOrderCost()
        );

        OrderDetail orderDetail = new OrderDetail(
                orderDetailDTO.getOrderID(),
                orderDetailDTO.getItemCode(),
                orderDetailDTO.getOrderQTY(),
                orderDetailDTO.getDiscount()
        );

        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false); // to pause the action of saving data to tables

            if (orderRepo.update(orders)) {  // will be true if the Orders table is updated, but doesnt save bcz setAutoCommit is false

                if (orderDetailRepo.update(orderDetail)) { // will be true if the order detail table and Item table gets updated (but not visible bcz it doesnt actually save, bcz setAutoCommit is false) takes the data to the table to save but doesnt save
                    con.commit();
                    return true;

                } else { // will be false if some issue is raised during updating Order Detail Table
                    con.rollback(); // send back the sent data bundle bcz of of some security issues/error
                    return false;
                }
            } else { // will be false if some issue is raised during updating Orders Table
                con.rollback();
                return false;
            }

        } finally {
            con.setAutoCommit(true);
        }
    }

    @Override
    public String getDiscount(String itemCode) throws SQLException, ClassNotFoundException {
        return discountRepo.getDiscount(itemCode);
    }

    @Override
    public boolean isCustomerExists(String custID) throws SQLException, ClassNotFoundException {
        return customerRepo.isCustomerExists(custID);
    }
}
