package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lk.ijse.pos_system.business.BOFactory;
import lk.ijse.pos_system.business.custom.ManageOrderBO;
import lk.ijse.pos_system.dto.CustomDTO;
import lk.ijse.pos_system.dto.OrderDTO;
import lk.ijse.pos_system.dto.OrderDetailDTO;
import lk.ijse.pos_system.entity.OrderDetail;
import lk.ijse.pos_system.repository.custom.impl.DiscountRepoImpl;
import lk.ijse.pos_system.repository.custom.impl.ItemRepoImpl;
import lk.ijse.pos_system.repository.custom.impl.OrderDetailRepoImpl;
import lk.ijse.pos_system.util.NavigationUtil;
import lk.ijse.pos_system.util.ValidationUtil;
import lk.ijse.pos_system.view.tm.OrderListTM;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class ManageOrdersFormController {

    public AnchorPane contextManageOrder;
    public Label lblTime;
    public Label lblDate;
    public TextField txtCustomerID;
    public JFXButton btnSearchOrders;
    public Label lblCustomerID;
    public ListView<String> orderListView;
    public TextField txtItemCode;
    public TextField txtDescription;
    public TextField txtPackSize;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtDiscount;
    public TextField txtOrderQty;
    public JFXCheckBox chkBoxToTrash;
    public JFXCheckBox chkBoxToStock;
    public JFXCheckBox chkBoxFromStock;
    public JFXButton btnRemoveItem;
    public JFXButton btnEditOrderItem;

    public TableView<OrderListTM> tblManageOrder;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colOrderQty;
    public TableColumn colSubtotal;
    public TableColumn colDiscount;
    public TableColumn colTotal;

    public Label lblOrderSubtotal;
    public Label lblOrderTotalDiscount;
    public Label lblOrderCost;
    public Label lblOrderNewCost;
    public Label lblRefund;
    public Label lblAmountToPay;
    public Label lblModifiedDate;
    public Label lblModifiedTime;
    public JFXButton btnConfirmEdits;
    public JFXButton btnCancel;

    private final ManageOrderBO orderBO = (ManageOrderBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.MANAGE_ORDER);
    private OrderListTM itemSelected = null; // from Table
    private String orderIdSelected = null; // from ListView
    private List<String> listOfOrders = null;
    private ArrayList<OrderListTM> listOfOrderedItems = new ArrayList<>();
    private ArrayList<Double> oldPaymentInfo = null;
    private int oldOrderQty = 0; //orderQty before editing
    private int newOrderQty = 0; // new orderQty requested
    private double discount = 0;
    private double oldOrderCost = 0;
    private double newOrderCost = 0;
    private int rowSelectedForDelete = -1;
    private URL resource;

    public void initialize() {
        loadDateAndTime();

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colOrderQty.setCellValueFactory(new PropertyValueFactory<>("orderQTY"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        orderListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {

                orderIdSelected = newValue; // selected orderID from list
                setItemDetailsToTable(orderIdSelected);
                // loadOldPaymentInfo(orderIdSelected);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });

        tblManageOrder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue == null) {
                    //
                } else {

                    itemSelected = newValue;

                    loadItemDataToFields(newValue);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });

        mapValidations();
    }

    public void loadDateAndTime() {
        // load Date
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        lblDate.setText(f.format(date));

        // load Time
        Timeline time = new Timeline(new KeyFrame(Duration.ZERO, e -> {

            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss a");

            Calendar cal = new GregorianCalendar();
            Date date1 = cal.getTime();
            lblTime.setText(simpleTimeFormat.format(date1));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    LinkedHashMap<TextField, Pattern> mapCustomerId = new LinkedHashMap<>();
    String custIDRegEx = "^(C-)[0-9]{3}$";
    Pattern custIDPtn = Pattern.compile(custIDRegEx);

    private void mapValidations() {
        mapCustomerId.put(txtCustomerID,custIDPtn);
    }

    public void validateFieldOnKeyRelease(KeyEvent keyEvent) {
        Object response = ValidationUtil.validateFormFields(mapCustomerId);

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
        for (TextField keyTextField : mapCustomerId.keySet()) {
            keyTextField.setStyle("");
        }
    }

    public void searchOrdersOnAction(ActionEvent actionEvent) {
        try {
            if (txtCustomerID.getText().equals("")) {
                new Alert(Alert.AlertType.WARNING, "Invalid Customer ID").show();
                return;

            } else {
                if (!orderBO.isCustomerExists(txtCustomerID.getText())) {
                    new Alert(Alert.AlertType.WARNING, "Invalid Customer ID").show();
                    return;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            setStyleToInitial();
        }

        String validCustID = txtCustomerID.getText().split("-")[0];

        if (validCustID.equals("C")) {
            orderListView.getItems().clear();

            try {
                listOfOrders = orderBO.searchOrdersByCustID(txtCustomerID.getText());

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            for (String orderID : listOfOrders) {
                orderListView.getItems().add(orderID);
            }

            if (listOfOrders.size() != 0) {
                lblCustomerID.setText(txtCustomerID.getText());
            }

        } else {
            new Alert(Alert.AlertType.WARNING, "Invalid Customer ID...").show();
        }
    }

    /*public void editOrderItemsOnAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        System.out.println(itemSelected);
        btnConfirmEdits.setDisable(false);
        //qtyOnHand in Item table should be updated
        //OrderDetail's orderQty & discount should be updated
        //Orders table's orderCost should be updated

        String itemCode = txtItemCode.getText();
        int oldOrderQty = itemSelected.getOrderQTY();
        //int oldQtyOnHand = Integer.parseInt(orderBO.getQtyOnHand(itemSelected.getItemCode()));
        int oldQtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        int newOrderQty = 0;
        int newQtyOnHand = 0;
        int qtyBackToStock = 0;
        int qtyToReduceFromStock = 0;

        if ((Integer.parseInt(txtOrderQty.getText())) == oldOrderQty) {
            //btnEditOrderItem.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Please enter the OrderQTY to be updated...").show();

        } else { // if txtOrderQty is less than or greater than oldOrderQty
            newOrderQty = Integer.parseInt(txtOrderQty.getText());
            btnEditOrderItem.setDisable(false);

            if (chkBoxToStock.isSelected()) {

                if (newOrderQty < oldOrderQty) { //2<3

                    qtyBackToStock = (oldOrderQty - newOrderQty); // 3-2 = 1
                    newQtyOnHand = oldQtyOnHand + qtyBackToStock; // 10 + 1 = 11

                    if (orderBO.editQtyOnHand(itemCode, newQtyOnHand)) { // if qtyOnHand of Item Table is updated
                        new Alert(Alert.AlertType.INFORMATION, "Returned Quantity Added To Stock Successfully.", ButtonType.OK).show();
                        //new OrderDetailController().updateDiscount(itemCode,orderIdSelected,newOrderQty,txtUnitPrice.getText());
                        updateTableAndField(itemCode, newOrderQty, newQtyOnHand);

                    } else {
                        new Alert(Alert.AlertType.WARNING, "Error Occurred.Stock couldn't update.", ButtonType.OK).show();
                    }
                } else {

                }

            } else if (chkBoxFromStock.isSelected()) {
                if (newOrderQty > oldOrderQty) {

                    qtyToReduceFromStock = newOrderQty - oldOrderQty;
                    newQtyOnHand = oldQtyOnHand - qtyToReduceFromStock;

                    if (orderBO.editQtyOnHand(itemCode, newQtyOnHand)) {
                        new Alert(Alert.AlertType.INFORMATION, "Quantity Deducted From Stock Successfully.", ButtonType.OK).show();
                        updateTableAndField(itemCode, newOrderQty, newQtyOnHand);

                    } else {
                        new Alert(Alert.AlertType.WARNING, "Error Occurred.Stock couldn't update.", ButtonType.OK).show();
                    }
                }

            } else if (chkBoxToTrash.isSelected()) {

                double unitPrice = Double.parseDouble(txtUnitPrice.getText());
                int packSize =  Integer.parseInt(txtPackSize.getText().split(" ")[0]);
                int discountPerUnit = Integer.parseInt(orderBO.getDiscount(itemCode));

                if (orderBO.updateOrderQty(orderIdSelected, itemCode, newOrderQty)) {
                    new Alert(Alert.AlertType.INFORMATION, "Quantity Added To Trash Successfully.", ButtonType.OK).show();

                    // setItemDetailsToTable(orderIdSelected);
                    clearFields();
                    listOfOrderedItems = orderBO.getOrderedItems(orderIdSelected, itemSelected);
                    tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));

                } else {
                    new Alert(Alert.AlertType.WARNING, "Error : Order Detail didn't saved successfully.", ButtonType.OK).show();
                }

            } else {
                new Alert(Alert.AlertType.WARNING, "Please choose the Transfer Mode of the Update Order", ButtonType.OK).show();
            }
        }
        double oldOrderCost = Double.parseDouble(lblOrderCost.getText());
        double newOrderCost = 0;
        double newOrderDiscount = 0;
        for (OrderListTM otm : listOfOrderedItems) {
            newOrderCost += otm.getTotal();
            newOrderDiscount += otm.getDiscount();
        }
        loadNewPaymentInfo(newOrderCost, oldOrderCost);
        System.out.println(itemSelected);
    } */ // undo

//---------------------------------------------------------------------------------------------------------------------------------------------

    private void setItemDetailsToTable(String orderSelected) throws SQLException, ClassNotFoundException {
        listOfOrderedItems.clear();
        loadOldPaymentInfo(orderSelected);
        clearFields();
        clearNewPaymentInfo();
        CustomDTO customDTO = null;

        if (itemSelected != null) {
            customDTO = new CustomDTO(
                    itemSelected.getItemCode(),
                    itemSelected.getDescription(),
                    itemSelected.getPackSize(),
                    itemSelected.getUnitPrice(),
                    itemSelected.getOrderQTY(),
                    itemSelected.getSubTotal(),
                    itemSelected.getDiscount(),
                    itemSelected.getTotal()
            );
        }

        ArrayList<CustomDTO> dtoAllItems = orderBO.getOrderedItems(orderSelected, customDTO);
        for (CustomDTO dto : dtoAllItems) {
            listOfOrderedItems.add(new OrderListTM(
                    dto.getItemCode(),
                    dto.getDescription(),
                    dto.getPackSize(),
                    dto.getUnitPrice(),
                    dto.getOrderQTY(),
                    dto.getSubTotal(),
                    dto.getDiscount(),
                    dto.getTotal()
            ));
        }
        tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));
    }

    private void clearOldPaymentInfo() {
        lblOrderSubtotal.setText("0.00");
        lblOrderTotalDiscount.setText("0.00");
        lblOrderCost.setText("0.00");
    }

    private void clearNewPaymentInfo() {
        lblOrderNewCost.setText("0.00");
        lblRefund.setText("0.00");
        lblAmountToPay.setText("0.00");
    }

    private void clearFields() {
        txtItemCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtPackSize.clear();
        txtDiscount.clear();
        txtQtyOnHand.clear();
        txtOrderQty.clear();

    }

    //when an item is selected from the table
    private void loadItemDataToFields(OrderListTM itemSelected) throws SQLException, ClassNotFoundException {
        btnEditOrderItem.setDisable(false);
        btnRemoveItem.setDisable(false);

        txtItemCode.setText(itemSelected.getItemCode());
        txtDescription.setText(itemSelected.getDescription());
        txtPackSize.setText(itemSelected.getPackSize());
        txtUnitPrice.setText(String.valueOf(itemSelected.getUnitPrice()));
        txtOrderQty.setText(String.valueOf(itemSelected.getOrderQTY()));
        txtQtyOnHand.setText(new ItemRepoImpl().getQtyOnHand(itemSelected.getItemCode()));
        txtDiscount.setText(String.valueOf(itemSelected.getDiscount()));

        uncheckCheckBox();
    }

    private void loadOldPaymentInfo(String orderSelected) throws SQLException, ClassNotFoundException {
        oldPaymentInfo = orderBO.getOldPaymentInfo(orderSelected);

        lblOrderSubtotal.setText(String.valueOf(oldPaymentInfo.get(0)));
        lblOrderTotalDiscount.setText(String.valueOf(oldPaymentInfo.get(1)));
        lblOrderCost.setText(String.valueOf(oldPaymentInfo.get(2)));
    }

    public void editOrderItemsOnAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        btnConfirmEdits.setDisable(false);

        if (itemSelected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Please select an Item to update...").show();
            return;
        }
        oldOrderQty = itemSelected.getOrderQTY();

        if ((Integer.parseInt(txtOrderQty.getText())) == oldOrderQty) {
            new Alert(Alert.AlertType.INFORMATION, "Please enter the OrderQTY to be updated...").show();
            return;

        } else { // if txtOrderQty is less than or greater than oldOrderQty
            newOrderQty = Integer.parseInt(txtOrderQty.getText());
        }

        /*if ( !chkBoxFromStock.isSelected() || !chkBoxToStock.isSelected() || !chkBoxToTrash.isSelected()) {
            new Alert(Alert.AlertType.WARNING, "Please choose the Transfer Option of the Updated Order", ButtonType.OK).show();
            return;
        }
        else */

        if (chkBoxFromStock.isSelected()) {
            //System.out.println("chkBox 1 selected...");

        } else if (chkBoxToStock.isSelected()) {
           // System.out.println("chkBox 2 selected...");

        } else if (chkBoxToTrash.isSelected()) {
            //System.out.println("chkBox 3 selected...");

        } else {
            new Alert(Alert.AlertType.WARNING, "Please choose the Transfer Option of the Updated Order", ButtonType.OK).show();
            return;
        }
        if (txtOrderQty.getText().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No changes have been done to update...", ButtonType.OK).show();
            return;

        } else {
            int packSize = orderBO.splitPackSize(txtItemCode.getText(), txtPackSize.getText());
            int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
            newOrderQty = Integer.parseInt(txtOrderQty.getText());  // new orderQty
            double discountPerUnit = Double.parseDouble(orderBO.getDiscount(txtItemCode.getText()));
            double unitPrice = Double.parseDouble(txtUnitPrice.getText());
            double subTotal = unitPrice * packSize * newOrderQty;
            discount = subTotal * discountPerUnit / 100;
            double total = subTotal - discount;

            OrderListTM tm = new OrderListTM( // Item details to be updated in the table (only in the table not in database)
                    txtItemCode.getText(),
                    txtDescription.getText(),
                    txtPackSize.getText(),
                    unitPrice,
                    newOrderQty,
                    subTotal,
                    discount,
                    total
            );

            int rowNumber = isExists(tm);

            if (itemSelected == null) {

            } else { // updates the existing record
                OrderListTM tmItemForUpdate = listOfOrderedItems.get(rowNumber); // get the OrderListTM object / element in this particular rowNumber

                if (newOrderQty < oldOrderQty) {
                    OrderListTM tmUpdatedItem = new OrderListTM(
                            tmItemForUpdate.getItemCode(),
                            tmItemForUpdate.getDescription(),
                            tmItemForUpdate.getPackSize(),
                            unitPrice,
                            newOrderQty,
                            //tmItemForUpdate.getOrderQTY() - (tmItemForUpdate.getOrderQTY() - newOrderQty), // new + old
                            tmItemForUpdate.getSubTotal() - (tmItemForUpdate.getSubTotal() - subTotal),
                            tmItemForUpdate.getDiscount() - (tmItemForUpdate.getDiscount() - discount),
                            tmItemForUpdate.getTotal() - (tmItemForUpdate.getTotal() - total)
                    );

                    listOfOrderedItems.remove(rowNumber);
                    listOfOrderedItems.add(tmUpdatedItem);

                } else if (newOrderQty > oldOrderQty) {
                    OrderListTM tmUpdatedItem = new OrderListTM(
                            tmItemForUpdate.getItemCode(),
                            tmItemForUpdate.getDescription(),
                            tmItemForUpdate.getPackSize(),
                            unitPrice,
                            newOrderQty,
                            //tmItemForUpdate.getOrderQTY() + (newOrderQty - tmItemForUpdate.getOrderQTY()), // new + old
                            subTotal,
                            discount,
                            subTotal - discount
                    );

                    listOfOrderedItems.remove(rowNumber);
                    listOfOrderedItems.add(tmUpdatedItem);
                }
            }
        }
        lblModifiedDate.setText(String.valueOf(lblDate.getText()));
        lblModifiedTime.setText(String.valueOf(lblTime.getText()));

        tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));
        double total = 0;
        for (OrderListTM otm : listOfOrderedItems) {
            //if (otm.getItemCode().equals(itemSelected.getItemCode())) {
            total = total + otm.getTotal();
                //loadNewPaymentInfo(otm.getTotal(), Double.parseDouble(lblOrderCost.getText()));
            loadNewPaymentInfo(total, Double.parseDouble(lblOrderCost.getText()));
            //}
        }
    }

    public void calculateNewOrderTotalCost() {
        oldOrderCost = Double.parseDouble(lblOrderCost.getText());

        for (OrderListTM otm : listOfOrderedItems) {
            newOrderCost += otm.getTotal();
        }
        lblOrderNewCost.setText(String.valueOf(newOrderCost));

        if (newOrderCost > oldOrderCost) {

            double amountToPay = newOrderCost - oldOrderCost;
            String toPay = String.format("%.1f", amountToPay);
            lblAmountToPay.setText(toPay);

        } else if (newOrderCost < oldOrderCost) {

            double refund = oldOrderCost - newOrderCost;
            String toRefund = String.format("%.1f", refund);
            lblRefund.setText(toRefund);
        }
    }

    private int isExists(OrderListTM tm) {
        for (int i = 0; i < listOfOrderedItems.size(); i++) {
            if (tm.getItemCode().equals(listOfOrderedItems.get(i).getItemCode())) {
                return i; // return the row number if Item to be updated
            }
        }
        return -1;
    }

    private void loadNewPaymentInfo(double newOrderCost, double oldOrderCost) throws SQLException, ClassNotFoundException {
        lblOrderNewCost.setText(String.valueOf(newOrderCost));

        if (newOrderCost > oldOrderCost) {

            double amountToPay = newOrderCost - oldOrderCost;
            String toPay = String.format("%.1f", amountToPay);
            lblAmountToPay.setText(toPay);

        } else if (newOrderCost < oldOrderCost) {

            double refund = oldOrderCost - newOrderCost;
            String toRefund = String.format("%.1f", refund);
            lblRefund.setText(toRefund);
        }
    }

    private void updateTableAndField(String itemCode, int newOrderQty, int newQtyOnHand) throws SQLException, ClassNotFoundException {
        txtQtyOnHand.setText(String.valueOf(newQtyOnHand));

        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int packSize = Integer.parseInt(txtPackSize.getText().split(" ")[0]);
        int discountPerUnit = Integer.parseInt(new DiscountRepoImpl().getDiscount(itemCode));

        if (new OrderDetailRepoImpl().updateOrderQty(orderIdSelected, itemCode, newOrderQty, unitPrice, packSize, discountPerUnit)) {
            //setItemDetailsToTable(orderIdSelected);
            clearFields();
//            listOfOrderedItems = orderDetailDAO.getOrderedItems(orderIdSelected, itemSelected);
            tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));

        } else {
            // System.out.println("-------_Error-------");
        }
    }

    public void removeItemFromOrderOnAction(ActionEvent actionEvent) {
        double currentOrderCost = Double.parseDouble(lblOrderNewCost.getText());
        double costAfterRemove = 0;

        if (itemSelected == null) {
            new Alert(Alert.AlertType.WARNING, "Please Select a Row to Remove").show();
        } else {

            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this Item?", yes, no);
            alert.setTitle("Confirmation Alert");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.orElse(no) == yes) {

                int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText()); //61
                int qtyToBeRemoved = Integer.parseInt(txtOrderQty.getText()); //5
                int qtyToRestock = qtyOnHand + qtyToBeRemoved;

                OrderDetailDTO orderDetailDTO = new OrderDetailDTO(txtItemCode.getText(), orderIdSelected);

                try {
                    if (orderBO.deleteItemFromOrder(orderDetailDTO)) {

                        if (orderBO.editQtyOnHand(itemSelected.getItemCode(), qtyToRestock)) {

                            costAfterRemove = currentOrderCost - itemSelected.getTotal();
                            listOfOrderedItems.remove(itemSelected);
                            tblManageOrder.setItems(FXCollections.observableArrayList(listOfOrderedItems));
                            tblManageOrder.refresh();

                            clearNewPaymentInfo();
                            clearOldPaymentInfo();

                            OrderDTO orderDTO = new OrderDTO(orderIdSelected);

                            if (tblManageOrder.getItems().isEmpty()) {
                                orderBO.deleteOrder(orderDTO);
                                orderListView.getItems().clear();
                            }

                            new Alert(Alert.AlertType.CONFIRMATION, "Item Deleted Successfully", ButtonType.OK).show();
                            clearFields();
                        }

                    } else {
                        new Alert(Alert.AlertType.WARNING, "Try Again...").show();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void confirmEditsOnAction(ActionEvent actionEvent) { //according to the ConfirmOrderOnAction of PlaceCustomerOrderFormController

        if (orderIdSelected == null) {
            new Alert(Alert.AlertType.WARNING, "No changes have been made...").show();
            return;
        }
        // To update the Orders table
        OrderDTO orderToBeUpdated = new OrderDTO(
                orderIdSelected,
                java.sql.Date.valueOf(lblDate.getText()),
                lblCustomerID.getText(),
                Double.parseDouble(lblOrderNewCost.getText())
        );

        // details to update OrderDetail table - should update only the details of the updated Item --> itemSelected
        ArrayList<OrderDetail> items = new ArrayList<>();
        // int newOrderQty = 0;

        double updatedDiscount = 0;
        for (OrderListTM otm : listOfOrderedItems) {
            if (itemSelected != null) {
                if (itemSelected.getItemCode().equals(otm.getItemCode())) {
                    newOrderQty = otm.getOrderQTY();
                    updatedDiscount = otm.getDiscount();
                }
            }
        }

        OrderDetailDTO orderDetailToBeUpdated = null;
        if (itemSelected == null) {
            //
        } else {
            orderDetailToBeUpdated = new OrderDetailDTO(
                    orderIdSelected,
                    itemSelected.getItemCode(),
                    newOrderQty,
                    updatedDiscount
            );
        }

        if (txtItemCode.getText().equals("")) {
            new Alert(Alert.AlertType.WARNING, "No changes have been made...").show();
            return;
        }

        int currentQtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        int newQtyOnHand = 0;

        try {
            if (chkBoxToStock.isSelected()) { //newOrderQty < oldOrderQty
                newQtyOnHand = currentQtyOnHand + (oldOrderQty - newOrderQty);

                if (orderBO.updateOrder(orderToBeUpdated, orderDetailToBeUpdated, newQtyOnHand)) { // return true if all 3 order table, order detail table and Item table gets updated.

                    if (confirmUpdate()) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Order Updated Successfully").show();
                    }
                    return;

                } else {
                    new Alert(Alert.AlertType.WARNING, "Update Failed...Try Again...").show();
                }

            } else if (chkBoxFromStock.isSelected()) { //newOrderQty > oldOrderQty
                newQtyOnHand = currentQtyOnHand - (newOrderQty - oldOrderQty);

                if (orderBO.updateOrder(orderToBeUpdated, orderDetailToBeUpdated, newQtyOnHand)) { // return true if all 3 order table, order detail table and Item table gets updated.
                    if (confirmUpdate()) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Order Updated Successfully").show();
                    }
                    return;
                } else {
                    new Alert(Alert.AlertType.WARNING, "Update Failed...Try Again...").show();
                }

            } else if (chkBoxToTrash.isSelected()) { //newOrderQty > oldOrderQty

                newQtyOnHand = currentQtyOnHand - (newOrderQty - oldOrderQty);
                if (orderBO.updateOrderAndOrderDetail(orderToBeUpdated, orderDetailToBeUpdated)) { // return true if both 2 orders table, order detail table gets updated.
                    if (confirmUpdate()) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Order Updated Successfully").show();
                    }
                    return;

                } else {
                    new Alert(Alert.AlertType.WARNING, "Update Failed...Try Again...").show();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Please choose the Transfer Mode of the Update Order", ButtonType.OK).show();
            }
            lblModifiedDate.setText(String.valueOf(lblDate.getText()));
            lblModifiedTime.setText(String.valueOf(lblTime.getText()));
            clearNewPaymentInfo();
            clearOldPaymentInfo();
            loadOldPaymentInfo(orderIdSelected);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean confirmUpdate() {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to continue Confirmation?", yes, no);
        alert.setTitle("Confirmation Alert");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no) == yes) {
            // return true;
        } else {
            return false;
        }
        return true;
    }

    private void clearDateAndTime() {
        lblModifiedDate.setText("");
        lblModifiedTime.setText("");
    }

    private void uncheckCheckBox() {
        chkBoxToTrash.setSelected(false);
        chkBoxToStock.setSelected(false);
        chkBoxFromStock.setSelected(false);
    }

    public void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/CashierDashBoardForm.fxml");
        NavigationUtil.navigateToPage(resource, contextManageOrder);
    }

    public void logoutOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/MainForm.fxml");
        NavigationUtil.logOutOnAction(resource, contextManageOrder);
    }
}
