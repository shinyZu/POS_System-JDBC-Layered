package lk.ijse.pos_system.business.custom.impl;

import lk.ijse.pos_system.business.custom.ItemBO;
import lk.ijse.pos_system.repository.RepoFactory;
import lk.ijse.pos_system.repository.custom.DiscountRepo;
import lk.ijse.pos_system.repository.custom.ItemRepo;
import lk.ijse.pos_system.repository.custom.QueryRepo;
import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.dto.DiscountDTO;
import lk.ijse.pos_system.dto.ItemDTO;
import lk.ijse.pos_system.entity.Discount;
import lk.ijse.pos_system.entity.Item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemBOImpl implements ItemBO {

    private final QueryRepo queryRepo = (QueryRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.QUERY);
    private final ItemRepo itemRepo = (ItemRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.ITEM);
    private final DiscountRepo discountRepo = (DiscountRepo) RepoFactory.getRepoFactoryInstance().getRepo(RepoFactory.RepoTypes.DISCOUNT);

    @Override
    public List<String> getItemCodes() throws SQLException, ClassNotFoundException {
        return itemRepo.getItemCodes();
    }

    @Override
    public boolean addItem(ItemDTO dto) throws SQLException, ClassNotFoundException {
        return itemRepo.add(new Item(dto.getItemCode(),dto.getDescription(), dto.getPackSize(),dto.getUnitPrice(), dto.getQtyOnHand()));
    }

    @Override
    public boolean addDiscount(DiscountDTO dto) throws SQLException, ClassNotFoundException {
        return discountRepo.add(new Discount(dto.getItemCode(), dto.getDescription(), dto.getDiscount()));
    }

    @Override
    public boolean updateItem(ItemDTO dto) throws SQLException, ClassNotFoundException {
        return itemRepo.update(new Item(dto.getItemCode(),dto.getDescription(), dto.getPackSize(),dto.getUnitPrice(), dto.getQtyOnHand()));
    }

    @Override
    public boolean updateDiscount(DiscountDTO dto) throws SQLException, ClassNotFoundException {
        return discountRepo.update(new Discount(dto.getItemCode(), dto.getDescription(), dto.getDiscount()));
    }

    @Override
    public boolean deleteItem(ItemDTO dto) throws SQLException, ClassNotFoundException {
        return itemRepo.delete(new Item(dto.getItemCode()));
    }

    @Override
    public ArrayList<CustomDTO> getAllItems() throws SQLException, ClassNotFoundException {
        return queryRepo.getAllItems();
    }

    @Override
    public String generateItemCode() throws SQLException, ClassNotFoundException {
        return itemRepo.generateItemCode();
    }
}
