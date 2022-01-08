package lk.ijse.pos_system.business.custom.impl;

import lk.ijse.pos_system.business.custom.PurchaseOrderBO;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.*;
import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.dto.CustomerDTO;
import lk.ijse.pos_system.dto.ItemDTO;
import lk.ijse.pos_system.dto.OrderDTO;
import lk.ijse.pos_system.dto.OrderDetailDTO;
import lk.ijse.pos_system.entity.Customer;
import lk.ijse.pos_system.entity.Item;
import lk.ijse.pos_system.entity.OrderDetail;
import lk.ijse.pos_system.entity.Orders;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderBOImpl implements PurchaseOrderBO {

    private final CustomerRepo customerRepo = (CustomerRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.CUSTOMER);
    private final ItemRepo itemRepo = (ItemRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ITEM);
    private final OrderRepo orderRepo = (OrderRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDER);
    private final OrderDetailRepo orderDetailRepo = (OrderDetailRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ORDERDETAIL);
    private final DiscountRepo discountRepo = (DiscountRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.DISCOUNT);

    @Override
    public boolean purchaseOrder(OrderDTO orderDTO, ArrayList<OrderDetailDTO> items) throws SQLException, ClassNotFoundException {
        Connection con = null;

        Orders orders = new Orders(
                orderDTO.getOrderID(),
                orderDTO.getDate(),
                orderDTO.getCustID(),
                orderDTO.getOrderCost()
        );

        ArrayList<OrderDetail> orderedItems = new ArrayList<>();
        for (OrderDetailDTO odt : items) {
            orderedItems.add(new OrderDetail(
                    odt.getOrderID(),
                    odt.getItemCode(),
                    odt.getOrderQTY(),
                    odt.getDiscount()
            ));
        }
        
        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false); // to pause the action of saving data to tables

            if (orderRepo.add(orders)) { // will be true if the order table is updated, but doesnt save bcz setAutoCommit is false

                for (OrderDetail odt : orderedItems) {

                    if (orderDetailRepo.add(odt)) { // will be true if the order detail table gets updated (but not visible bcz it doesnt actually save, bcz setAutoCommit is false) takes the data to the table to save but doesnt save

                        if (itemRepo.updateQtyOnHand(odt.getItemCode(), odt.getOrderQTY())) { // will be true if qtyOnHand of Item table is updated
                            /*con.commit();
                            return true;*/

                        } else {
                            con.rollback();
                            return false;
                        }

                    } else { // will be false if some issue is raised during updating Order Detail Table
                        con.rollback(); // send back the sent data bundle bcz of of some security issues/error
                        return false;
                    }
                }
                con.commit();
                return true;

            } else {
                con.rollback();
                return false;
            }

        } finally {
            con.setAutoCommit(true);
        }
    }

    @Override
    public String getOrderId() throws SQLException, ClassNotFoundException {
        return orderRepo.getOrderId();
    }

    @Override
    public List<String> getCustomerIds() throws SQLException, ClassNotFoundException {
        return customerRepo.getCustomerIds();
    }

    @Override
    public List<String> getItemCodes() throws SQLException, ClassNotFoundException {
        return itemRepo.getItemCodes();
    }

    @Override
    public List<String> getItemDescriptions() throws SQLException, ClassNotFoundException {
        return itemRepo.getItemDescriptions();
    }

    @Override
    public ItemDTO getItem(String itemCode) throws SQLException, ClassNotFoundException {
        Item item = itemRepo.getItem(itemCode);
        return new ItemDTO(
                item.getItemCode(),
                item.getDescription(),
                item.getPackSize(),
                item.getUnitPrice(),
                item.getQtyOnHand()
        );
    }

    @Override
    public String getDiscount(String itemCode) throws SQLException, ClassNotFoundException {
        return discountRepo.getDiscount(itemCode);
    }

    @Override
    public String getItemCode(String description) throws SQLException, ClassNotFoundException {
        return itemRepo.getItemCode(description);
    }

    @Override
    public CustomerDTO getCustomer(String custIdForSearch) throws SQLException, ClassNotFoundException {
        Customer customer = customerRepo.getCustomer(custIdForSearch);
        return new CustomerDTO(
                customer.getCustID(),
                customer.getCustTitle(),
                customer.getCustName(),
                customer.getCustAddress(),
                customer.getCity(),
                customer.getProvince(),
                customer.getPostalCode()
        );
    }

    @Override
    public boolean addCustomer(CustomerDTO odt) throws SQLException, ClassNotFoundException {
        return customerRepo.add(new Customer(
                odt.getCustID(),
                odt.getCustTitle(),
                odt.getCustName(),
                odt.getCustAddress(),
                odt.getCity(),
                odt.getProvince(),
                odt.getPostalCode()
        ));
    }

    @Override
    public int splitPackSize(String itemCode, String packSize) throws SQLException, ClassNotFoundException {
        return itemRepo.splitPackSize(itemCode, packSize);
    }

    @Override
    public String generateCustomerID() throws SQLException, ClassNotFoundException {
        return customerRepo.generateCustomerID();
    }

    @Override
    public String generateInvoiceId(String invoiceNo) throws SQLException, ClassNotFoundException {
        return orderRepo.generateInvoiceId(invoiceNo);
    }

    @Override
    public CustomerDTO getCustomerOfOrder(String orderId) throws SQLException, ClassNotFoundException {
        return customerRepo.getCustomerOfOrder(orderId);
    }
}
