package lk.ijse.pos_system.repository.custom;

import lk.ijse.pos_system.entity.Item;
import lk.ijse.pos_system.repository.CrudRepo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ItemRepo extends CrudRepo<Item, String> {

    String generateItemCode() throws SQLException, ClassNotFoundException;

    Item getItem(String itemCode) throws SQLException, ClassNotFoundException;

    ArrayList<String> getItemCodes() throws SQLException, ClassNotFoundException;

    List<String> getItemDescriptions() throws SQLException, ClassNotFoundException;

    String getItemCode(String description) throws SQLException, ClassNotFoundException;

    boolean duplicateEntryExists(Item newItem) throws SQLException, ClassNotFoundException;

    boolean updateQtyOnHand(String itemCode, int orderQty) throws SQLException, ClassNotFoundException;

    String getQtyOnHand(String itemCode) throws SQLException, ClassNotFoundException;

    boolean editQtyOnHand(String itemCode, int qtyBackToStock) throws SQLException, ClassNotFoundException;

    boolean updateEditedQtyOnHand(String itemCode, int newQtyOnHand) throws SQLException, ClassNotFoundException;

    int splitPackSize(String itemCode, String txtPackSize) throws SQLException, ClassNotFoundException;
}
