package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos_system.business.BOFactory;
import lk.ijse.pos_system.business.custom.ItemBO;
import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.dto.DiscountDTO;
import lk.ijse.pos_system.dto.ItemDTO;
import lk.ijse.pos_system.util.NavigationUtil;
import lk.ijse.pos_system.util.ValidationUtil;
import lk.ijse.pos_system.view.tm.ItemDiscountTM;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class ManageItemsFormController {

    public AnchorPane contextMangeItem;
    public ComboBox<String> cmbItemCode;
    public TextField txtItemCode;
    public TextField txtDescription;
    public TextField txtPackSize;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtDiscount;

    public TableView<ItemDiscountTM> tblItemDiscount;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colQtyOnHand;
    public TableColumn colDiscount;

    public JFXButton btnAddNewItem;
    public JFXButton btnEditItem;
    public JFXButton btnDeleteItem;

    private final ItemBO itemBO = (ItemBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.ITEM);
    private ArrayList<ItemDiscountTM> tmItemDiscountList = new ArrayList<>();
    private String itemForSearch = null;
    private String itemSearched = null;
    private List<String> itemCodeList = null;
    private int rowSelectedForDelete = -1;
    private URL resource;

    public void initialize() {

        try {
            btnAddNewItem.setDisable(false);
            btnEditItem.setDisable(true);
            btnDeleteItem.setDisable(true);

            loadItemCodes();
            initTable();
            setItemCode();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        cmbItemCode.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    itemForSearch = newValue;
                    itemSearched = oldValue;
                });

        tblItemDiscount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                //
            } else {
                try {
                    loadItemData(newValue);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            btnAddNewItem.setDisable(true);
            btnEditItem.setDisable(false);
            btnDeleteItem.setDisable(false);
        });

        tblItemDiscount.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            rowSelectedForDelete = (int) newValue;
        });

        mapValidations();
    }

    private void initTable() throws SQLException, ClassNotFoundException {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

        setItemsToTable(itemBO.getAllItems());
    }

    private void setItemCode() throws SQLException, ClassNotFoundException {
        txtItemCode.setText(itemBO.generateItemCode());
    }

    private void setItemsToTable(ArrayList<CustomDTO> itemsWithDiscountList) throws SQLException, ClassNotFoundException {
        tblItemDiscount.getItems().clear();
        tmItemDiscountList.clear();

        for (CustomDTO dto : itemsWithDiscountList) {
            tmItemDiscountList.add(new ItemDiscountTM(
                    dto.getItemCode(),
                    dto.getDescription(),
                    dto.getPackSize(),
                    dto.getUnitPrice(),
                    dto.getQtyOnHand(),
                    String.valueOf(dto.getDiscount())
            ));
        }
        tblItemDiscount.setItems(FXCollections.observableArrayList(tmItemDiscountList));
    }

    private void loadItemCodes() throws SQLException, ClassNotFoundException {
        itemCodeList = itemBO.getItemCodes();
        cmbItemCode.getItems().addAll(itemCodeList);
    }

    public void searchItemOnAction(ActionEvent actionEvent) {
        if (itemForSearch == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an Item Code...").show();
            return;
        }

        ArrayList<CustomDTO> itemsWithDiscount = null;
        try {
            itemsWithDiscount = itemBO.getAllItems();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (CustomDTO itm : itemsWithDiscount) {
            if (itemForSearch.equals(itm.getItemCode())) {
                txtItemCode.setText(itm.getItemCode());
                txtDescription.setText(itm.getDescription());
                txtPackSize.setText(itm.getPackSize());
                txtUnitPrice.setText(String.valueOf(itm.getUnitPrice()));
                txtQtyOnHand.setText(String.valueOf(itm.getQtyOnHand()));
                txtDiscount.setText(String.valueOf(itm.getDiscount()));
            }
        }
        btnAddNewItem.setDisable(true);
        btnEditItem.setDisable(false);
        btnDeleteItem.setDisable(false);
    }

    private void loadItemData(ItemDiscountTM rowSelected) { // when a row of the table is selected
        txtItemCode.setText(rowSelected.getItemCode());

        txtDescription.setText(rowSelected.getDescription());
        txtPackSize.setText(rowSelected.getPackSize());
        txtUnitPrice.setText(String.valueOf(rowSelected.getUnitPrice()));
        txtQtyOnHand.setText(String.valueOf(rowSelected.getQtyOnHand()));
        txtDiscount.setText(String.valueOf(rowSelected.getDiscount()));

        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.setPromptText("Item Code");
    }

    LinkedHashMap<TextField, Pattern> mapItemDetails = new LinkedHashMap<>();
    String descriptionRegEx = "^[A-Z][a-z/ ]{3,}[A-Z][a-z]{3,}|[A-Z][a-z]{3,}$";
    String packSizeRegEx = "^[0-9]+[\\s](Kg|L)$";
    String unitPriceRegEx = "^[1-9][0-9]*([.][0-9]+)?$";
    String qtyOnHandRegEx = "^[0-9]{1,6}$";
    String discountRegEx = "^[1-9][0-9]*([.][0-9]+)?$";

    Pattern descriptionPtn = Pattern.compile(descriptionRegEx);
    Pattern packSizePtn = Pattern.compile(packSizeRegEx);
    Pattern unitPricePtn = Pattern.compile(unitPriceRegEx);
    Pattern qtyOnHandPtn = Pattern.compile(qtyOnHandRegEx);
    Pattern discountPtn = Pattern.compile(discountRegEx);

    private void mapValidations() {
        mapItemDetails.put(txtDescription,descriptionPtn);
        mapItemDetails.put(txtPackSize,packSizePtn);
        mapItemDetails.put(txtUnitPrice,unitPricePtn);
        mapItemDetails.put(txtQtyOnHand,qtyOnHandPtn);
        mapItemDetails.put(txtDiscount,discountPtn);
    }

    public void validateFieldOnKeyRelease(KeyEvent keyEvent) {
        Object response = ValidationUtil.validateFormFields(mapItemDetails);

        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (response instanceof TextField) {
                TextField invalidField = (TextField) response;
                invalidField.requestFocus();

            } else if (response instanceof Boolean) {
                //
            }
        }
    }

    private void setStyleToInitial() {
        for (TextField keyTextField : mapItemDetails.keySet()) {
            keyTextField.setStyle("");
        }
    }

    public void addNewItemOnAction(ActionEvent actionEvent) {
        if (txtItemCode.getText().equals("") || txtUnitPrice.getText().equals("") || txtQtyOnHand.getText().equals("")) {
            new Alert(Alert.AlertType.WARNING, "Please fill all the required details...").show();
            btnAddNewItem.setDisable(false);
            return;
        }

        ItemDTO itemDTO = new ItemDTO(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtPackSize.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText())
        );

        DiscountDTO discountDTO = new DiscountDTO(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtDiscount.getText()
        );

        try {
            if (itemBO.addItem(itemDTO)) {
                new Alert(Alert.AlertType.CONFIRMATION, "Item Added Successfully..", ButtonType.OK).showAndWait();

                if (itemBO.addDiscount(discountDTO)) {
                    //new Alert(Alert.AlertType.CONFIRMATION, "Discount Added Successfully..").show();
                } else {
                    // new Alert(Alert.AlertType.CONFIRMATION, "Try Again..").show();
                }

                cmbItemCode.getItems().clear();
                loadItemCodes();
                setItemsToTable(itemBO.getAllItems());
                clearFields();

            } else {
                new Alert(Alert.AlertType.WARNING, "Duplicate Customer ID").show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void editItemOnAction(ActionEvent actionEvent) {
        ItemDTO itemDTO = new ItemDTO(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtPackSize.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText())
        );

        DiscountDTO discountDTO = new DiscountDTO(
                txtItemCode.getText(),
                txtDescription.getText(),
                txtDiscount.getText()
        );

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to edit this Item?", yes, no);
        alert.setTitle("Confirmation Alert");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no) == yes) {

            try {
                if (itemBO.updateItem(itemDTO) | itemBO.updateDiscount(discountDTO)) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Item Updated Successfully..", ButtonType.OK).show();

                    setItemsToTable(itemBO.getAllItems());
                    clearFields();
                    txtItemCode.setText(itemBO.generateItemCode());

                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteItemOnAction(ActionEvent actionEvent) {

        if (rowSelectedForDelete == -1) {
            new Alert(Alert.AlertType.WARNING, "Please Select a Row to Remove").show();
        } else {

            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this Item?", yes, no);
            alert.setTitle("Confirmation Alert");

            Optional<ButtonType> result = alert.showAndWait();

            ItemDTO itemDTO = new ItemDTO(txtItemCode.getText());

            if (result.orElse(no) == yes) {

                try {
                    if (itemBO.deleteItem(itemDTO)) {

                        tmItemDiscountList.remove(rowSelectedForDelete);
                        tblItemDiscount.setItems(FXCollections.observableArrayList(tmItemDiscountList));
                        tblItemDiscount.refresh();

                        new Alert(Alert.AlertType.CONFIRMATION, "Item Deleted Successfully", ButtonType.OK).show();
                        clearFields();

                    } else {
                        new Alert(Alert.AlertType.WARNING, "Try Again...").show();
                    }

                    cmbItemCode.getItems().clear();
                    loadItemCodes();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearFields() {
        setStyleToInitial();

        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.setPromptText("Item Code");

        txtItemCode.clear();
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtDiscount.clear();

        btnAddNewItem.setDisable(false);
        btnEditItem.setDisable(true);
        btnDeleteItem.setDisable(true);
    }

    public void clearFieldsOnAction(ActionEvent actionEvent) {
        setStyleToInitial();
        clearFields();
    }

    public void enableBtnAddNewItem(MouseEvent mouseEvent) {
        btnAddNewItem.setDisable(false);
    }

    public void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/AdminDashBoardForm.fxml");
        NavigationUtil.navigateToPage(resource, contextMangeItem);
    }

    public void logoutOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/MainForm.fxml");
        NavigationUtil.logOutOnAction(resource, contextMangeItem);
    }

}
