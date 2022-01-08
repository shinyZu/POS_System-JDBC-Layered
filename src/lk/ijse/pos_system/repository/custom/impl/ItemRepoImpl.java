package lk.ijse.pos_system.repository.custom.impl;

import lk.ijse.pos_system.util.CrudUtil;
import lk.ijse.pos_system.repository.custom.ItemRepo;
import lk.ijse.pos_system.entity.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRepoImpl implements ItemRepo {

    @Override
    public boolean add(Item newItem) throws SQLException, ClassNotFoundException {

        if (duplicateEntryExists(newItem)) {
            return false;
        }

        return CrudUtil.executeUpdate("INSERT INTO Item VALUES(?,?,?,?,?)",
                newItem.getItemCode(),
                newItem.getDescription(),
                newItem.getPackSize(),
                newItem.getUnitPrice(),
                newItem.getQtyOnHand()
        );
    }

    @Override
    public boolean delete(Item item) throws SQLException, ClassNotFoundException {

        if (CrudUtil.executeUpdate("DELETE FROM Item WHERE itemCode = ?",item.getItemCode())){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean update(Item editItem) throws SQLException, ClassNotFoundException {

        return CrudUtil.executeUpdate("UPDATE Item SET description=?, packSize=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?",
                editItem.getDescription(),
                editItem.getPackSize(),
                editItem.getUnitPrice(),
                editItem.getQtyOnHand(),
                editItem.getItemCode()
        );

    }

    @Override
    public String generateItemCode() throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Item ORDER BY itemCode DESC LIMIT 1");

        if (rst.next()){
            int tempId = Integer.parseInt(rst.getString(1).split("-")[1]);
            tempId = tempId+1;

            if (tempId <= 9){
                return "I-00" + tempId;
            }else if (tempId <= 99){
                return "I-0" + tempId;
            }else {
                return "I-" + tempId;
            }

        }else {
            return "I-001";
        }
    }

    @Override
    public Item getItem(String itemCode) throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Item WHERE itemCode = ?", itemCode);

        if (rst.next()) {
            return new Item(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getInt(5)
            );
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<String> getItemCodes() throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Item");

        ArrayList<String> itemCodeList = new ArrayList<>();

        if (rst == null) {
            return null;
        } else {
            while (rst.next()) {
                itemCodeList.add(
                        rst.getString(1)
                );
            }
        }
        return itemCodeList;
    }

    @Override
    public List<String> getItemDescriptions() throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT description FROM Item");
        ArrayList<String> itemDescrpList = new ArrayList<>();

        if (rst == null) {
            return null;
        } else {
            while (rst.next()) {
                itemDescrpList.add(
                        rst.getString(1)
                );
            }
        }
        return itemDescrpList;
    }

    @Override
    public String getItemCode(String description) throws SQLException, ClassNotFoundException {

        ResultSet resultSet = CrudUtil.executeQuery("SELECT itemCode FROM Item WHERE description = ?", description);

        if (resultSet.next()) {
            return resultSet.getString(1);

        } else {
            return null;
        }
    }

    @Override
    public boolean duplicateEntryExists(Item newItem) throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT * FROM Item WHERE itemCode = ?", newItem.getItemCode());

        if (rst.next()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateQtyOnHand(String itemCode, int orderQty) throws SQLException, ClassNotFoundException {

        ResultSet rst = CrudUtil.executeQuery("SELECT qtyOnHand FROM Item WHERE itemCode = ?", itemCode);

        int currentQtyOnHand = 0;
        if (rst.next()) {
            currentQtyOnHand = Integer.parseInt(rst.getString(1));
        }

        return CrudUtil.executeUpdate("UPDATE Item SET qtyOnHand = ? WHERE itemCode = ?",(currentQtyOnHand - orderQty),itemCode);
    }

    @Override
    public String getQtyOnHand(String itemCode) throws SQLException, ClassNotFoundException {

        ResultSet resultSet = CrudUtil.executeQuery("SELECT qtyOnHand FROM Item WHERE itemCode = ?", itemCode);

        if (resultSet.next()) {
            return resultSet.getString(1);

        } else {
            return null;
        }
    }

    @Override
    public boolean editQtyOnHand(String itemCode, int qtyBackToStock) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("UPDATE Item SET qtyOnHand = ? WHERE itemCode = ?",qtyBackToStock,itemCode);
    }

    @Override
    public boolean updateEditedQtyOnHand(String itemCode, int newQtyOnHand) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("UPDATE Item SET qtyOnHand = ?  WHERE itemCode = ?",newQtyOnHand,itemCode);
    }

    @Override
    public int splitPackSize(String itemCode, String txtPackSize) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.executeQuery("SELECT packSize FROM Item WHERE itemCode = ?", itemCode);

        int tempPckSize;
        if (rst.next()) {
            tempPckSize = Integer.parseInt(rst.getString(1).split(" ")[0]);
            return tempPckSize;
        }
        return 0;
    }

}
