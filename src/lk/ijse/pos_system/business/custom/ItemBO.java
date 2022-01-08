package lk.ijse.pos_system.business.custom;

import lk.ijse.pos_system.business.SuperBO;
import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.dto.DiscountDTO;
import lk.ijse.pos_system.dto.ItemDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ItemBO extends SuperBO {

    List<String> getItemCodes() throws SQLException, ClassNotFoundException;

    boolean addItem(ItemDTO dto) throws SQLException, ClassNotFoundException;

    boolean addDiscount(DiscountDTO newDiscount) throws SQLException, ClassNotFoundException;

    boolean updateItem(ItemDTO editItem) throws SQLException, ClassNotFoundException;

    boolean updateDiscount(DiscountDTO editDiscount) throws SQLException, ClassNotFoundException;

    boolean deleteItem(ItemDTO itemToDelete) throws SQLException, ClassNotFoundException;

    ArrayList<CustomDTO> getAllItems() throws SQLException, ClassNotFoundException;

    String generateItemCode() throws SQLException, ClassNotFoundException;
}
