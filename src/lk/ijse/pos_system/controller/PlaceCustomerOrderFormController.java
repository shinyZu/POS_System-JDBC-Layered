package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lk.ijse.pos_system.business.BOFactory;
import lk.ijse.pos_system.business.custom.PurchaseOrderBO;
import lk.ijse.pos_system.db.DBConnection;
import lk.ijse.pos_system.dto.CustomerDTO;
import lk.ijse.pos_system.dto.ItemDTO;
import lk.ijse.pos_system.dto.OrderDTO;
import lk.ijse.pos_system.dto.OrderDetailDTO;
import lk.ijse.pos_system.repository.custom.impl.DiscountRepoImpl;
import lk.ijse.pos_system.util.NavigationUtil;
import lk.ijse.pos_system.util.ValidationUtil;
import lk.ijse.pos_system.view.tm.OrderListTM;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class PlaceCustomerOrderFormController {

    public AnchorPane contextPlaceOrder;
    public Label lblOrderID;
    public Label lblDate;
    public Label lblTime;
    public ComboBox<String> cmbCustIDs;
    public TextField txtCustId;
    public TextField txtCustTitle;
    public TextField txtCustName;
    public TextField txtCustAddress;
    public TextField txtCustCity;
    public TextField txtCustProvince;
    public TextField txtCustPostalCode;
    public JFXButton btnAddNewCustomer;
    public JFXButton btnClearFields;
    public ComboBox<String> cmbItemCode;
    public ComboBox<String> cmbItemDescription;
    public TextField txtPackSize;
    public TextField txtQtyOnHand;
    public TextField txtUnitPrice;
    public TextField txtDiscount;
    public TextField txtOrderQTY;

    public TableView<OrderListTM> tblOrderItem;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colOrderQTY;
    public TableColumn colSubTotal;
    public TableColumn colDiscount;
    public TableColumn colTotal;

    public Label lblOrderSubTotal;
    public Label lblOrderTotalDiscount;
    public Label lblOrderCost;
    public Label lblAmountPaid;
    public Label lblBalance;
    public Label lblPaymentDate;
    public Label lblPaymentTime;
    public TextField txtPayment;
    public Button btnPayNow;
    public JFXButton btnAddToList;
    public JFXButton btnConfirmOrder;
    public JFXButton btnCancelOrder;

    private final PurchaseOrderBO purchaseOrderBO = (PurchaseOrderBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.PURCHASE_ORDER);
    private List<String> customerIdList;
    private List<String> itemCodeList = null;
    private List<String> descriptionList = null;
    private ObservableList<OrderListTM> orderList = FXCollections.observableArrayList();
    private String custIdForSearch = null;
    private OrderListTM itemSelected = null;
    private URL resource;

    public void initialize() {
        loadDateAndTime();
        try {
            setOrderId();
            setCustomerId();
            loadCustomerIds();
            loadItemCodes();
            loadItemDescriptions();
            initTable();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        cmbCustIDs.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    custIdForSearch = newValue;
                });

        cmbItemCode.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    try {
                        setItemDataOnCode(newValue);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });

        cmbItemDescription.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    try {
                        setItemDataOnDescription(newValue);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });

        btnConfirmOrder.setDisable(true);

        tblOrderItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null) {
                //
            } else {
                itemSelected = newValue;
            }
        });

        mapCustomerValidations();
        mapItemValidations();
    }

    private void initTable() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colOrderQTY.setCellValueFactory(new PropertyValueFactory<>("orderQTY"));
        colSubTotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
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

    private void setOrderId() throws SQLException, ClassNotFoundException {
        lblOrderID.setText(purchaseOrderBO.getOrderId());
    }

    private void setCustomerId() throws SQLException, ClassNotFoundException {
        txtCustId.setText(purchaseOrderBO.generateCustomerID());
    }

    private void loadCustomerIds() throws SQLException, ClassNotFoundException { //load custIDs to combo box
        customerIdList = purchaseOrderBO.getCustomerIds();
        cmbCustIDs.getItems().clear();
        cmbCustIDs.getItems().addAll(customerIdList);
    }

    private void loadItemCodes() throws SQLException, ClassNotFoundException {
        itemCodeList = purchaseOrderBO.getItemCodes();
        cmbItemCode.getItems().addAll(itemCodeList);
    }

    private void loadItemDescriptions() throws SQLException, ClassNotFoundException {
        descriptionList = purchaseOrderBO.getItemDescriptions();
        cmbItemDescription.getItems().addAll(descriptionList);
    }

    private void setItemDataOnCode(String itemCode) throws SQLException, ClassNotFoundException { //load item details to fields when itemCode is selected

        if (itemCode != null) {
            ItemDTO item = purchaseOrderBO.getItem(itemCode);

            if (item == null) {
                new Alert(Alert.AlertType.WARNING, "Empty Result Set");

            } else {
                cmbItemDescription.setValue(item.getDescription());
                txtPackSize.setText(item.getPackSize());
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
                txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));

                String discount = purchaseOrderBO.getDiscount(itemCode);
                txtDiscount.setText(discount);
            }
        }
    }

    private void setItemDataOnDescription(String description) throws SQLException, ClassNotFoundException {
        String itemCode = purchaseOrderBO.getItemCode(description);

        if (description != null) {
            ItemDTO item = purchaseOrderBO.getItem(itemCode);

            if (item == null) {
                new Alert(Alert.AlertType.WARNING, "Empty Result Set");
            } else {
                cmbItemCode.setValue(itemCode);
                txtPackSize.setText(item.getPackSize());
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
                txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));

                String discount = purchaseOrderBO.getDiscount(itemCode);
                txtDiscount.setText(discount);
            }
        }
    }

    LinkedHashMap<TextField, Pattern> mapItemDetails = new LinkedHashMap<>();
    String orderQtyRegEx = "^[0-9]{1,6}$";
    Pattern orderQtyPtn = Pattern.compile(orderQtyRegEx);

    LinkedHashMap<TextField, Pattern> mapCustomerDetails = new LinkedHashMap<>();
    String custTitleRegEx = "^(Mr|Mrs|Ms)$";
    String custNameRegEx = "^[A-Z][a-z/ ]{3,}[A-Z][a-z]{3,}|[A-Z][a-z]{3,}$";
    String custAddressRegEx = "^[A-Z0-9\\-/]+$";
    String custCityRegEx = "^[A-Z][a-z]*|[\\s0-9]+$";
    String custProvinceRegEx = "^[A-Z][a-z]+$";
    String custPostalCodeRegEx = "^[0-9]+$";

    Pattern custTitlePtn = Pattern.compile(custTitleRegEx);
    Pattern custNamePtn = Pattern.compile(custNameRegEx);
    Pattern custAddressPtn = Pattern.compile(custAddressRegEx);
    Pattern custCityPtn = Pattern.compile(custCityRegEx);
    Pattern custProvincePtn = Pattern.compile(custProvinceRegEx);
    Pattern custPostalCodePtn = Pattern.compile(custPostalCodeRegEx);

    private void mapItemValidations() {
        mapItemDetails.put(txtOrderQTY,orderQtyPtn);
    }

    private void mapCustomerValidations() {
        mapCustomerDetails.put(txtCustTitle,custTitlePtn);
        mapCustomerDetails.put(txtCustName,custNamePtn);
        mapCustomerDetails.put(txtCustAddress,custAddressPtn);
        mapCustomerDetails.put(txtCustCity,custCityPtn);
        mapCustomerDetails.put(txtCustProvince,custProvincePtn);
        mapCustomerDetails.put(txtCustPostalCode,custPostalCodePtn);
    }

    public void validateFieldOnKeyRelease(KeyEvent keyEvent) {
        Object response = null;

        if (mapCustomerDetails != null) {
            response = ValidationUtil.validateFormFields(mapCustomerDetails);

        } else if (mapItemDetails != null) {
            response = ValidationUtil.validateFormFields(mapItemDetails);

        }

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
        for (TextField keyTextField : mapCustomerDetails.keySet()) {
            keyTextField.setStyle("");
        }
    }

    public void searchCustomerOnAction(ActionEvent actionEvent) {
        btnAddNewCustomer.setDisable(true);

        if (cmbCustIDs.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a Customer ID...").show();
            return;
        }

        CustomerDTO customer = null;

        try {
            customer = purchaseOrderBO.getCustomer(custIdForSearch);

            if (customer == null) {
                new Alert(Alert.AlertType.WARNING, "Empty Result Set").show();
            } else {
                if (customer.getCustName() != null) {
                    txtCustId.setText(customer.getCustID());
                    txtCustTitle.setText(customer.getCustTitle());
                    txtCustName.setText(customer.getCustName());
                    txtCustAddress.setText(customer.getCustAddress());
                    txtCustCity.setText(customer.getCity());
                    txtCustProvince.setText(customer.getProvince());
                    txtCustPostalCode.setText(customer.getPostalCode());
                } else {
                    txtCustId.setText(customer.getCustID());
                    txtCustTitle.setText("Unknown");
                    txtCustName.setText("Unknown");
                    txtCustAddress.setText("Unknown");
                    txtCustCity.setText("Unknown");
                    txtCustProvince.setText("Unknown");
                    txtCustPostalCode.setText("Unknown");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addNewCustomerOnAction(ActionEvent actionEvent) {

        if (txtCustId.getText() == "" || txtCustTitle.getText() == "" || txtCustName.getText() == "" || txtCustAddress.getText() == ""
                || txtCustCity.getText() == "" || txtCustProvince.getText() == "" || txtCustPostalCode.getText() == "") {
            new Alert(Alert.AlertType.WARNING, "Please fill all required data", ButtonType.OK).show();
        }

        CustomerDTO newCust = new CustomerDTO(
                txtCustId.getText(),
                txtCustTitle.getText(),
                txtCustName.getText(),
                txtCustAddress.getText(),
                txtCustCity.getText(),
                txtCustProvince.getText(),
                txtCustPostalCode.getText()
        );

        String customerName = null;

        for (String id : customerIdList) {
            if (newCust.getCustID().equals(id)) {
                new Alert(Alert.AlertType.WARNING, "Duplicate Customer ID").show();
                return;

            } else {
                if (newCust.getCustName().isEmpty()) {
                    customerName = "Unknown";

                } else {
                    customerName = newCust.getCustName();
                }
            }
        }
        newCust.setCustName(customerName);

        try {
            if (purchaseOrderBO.addCustomer(newCust)) {
                new Alert(Alert.AlertType.CONFIRMATION, "Customer Added Successfully...").show();

                cmbCustIDs.getItems().clear();
                loadCustomerIds();
                clearCustomerFields();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void enableBtnAddNewCustomerOnClick(MouseEvent mouseEvent) {
        btnAddNewCustomer.setDisable(false);
    }

    public void clearFieldsOnAction(ActionEvent actionEvent) {
        try {
            clearCustomerFields();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        setStyleToInitial();
    }

    private void clearCustomerFields() throws SQLException, ClassNotFoundException {
        setStyleToInitial();

        cmbCustIDs.getSelectionModel().clearSelection();
        cmbCustIDs.setPromptText("Customer ID");

        txtCustId.clear();
        txtCustTitle.clear();
        txtCustName.clear();
        txtCustAddress.clear();
        txtCustCity.clear();
        txtCustProvince.clear();
        txtCustPostalCode.clear();

        btnAddNewCustomer.setDisable(false);

        txtCustId.setText(purchaseOrderBO.generateCustomerID());
    }

    private void clearItemFields() {
        txtOrderQTY.setStyle("");

        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.setPromptText("Item Code");

        cmbItemDescription.getSelectionModel().clearSelection();
        cmbItemDescription.setPromptText("Description");

        txtPackSize.clear();
        txtQtyOnHand.clear();
        txtOrderQTY.clear();
        txtUnitPrice.clear();
        txtDiscount.clear();
    }

    public void addToListOnAction(ActionEvent actionEvent) {

        if (txtOrderQTY.getText().equals("")) {
            new Alert(Alert.AlertType.INFORMATION, "Please enter Order Quantity", ButtonType.OK).show();
            return;
        }

        btnAddToList.setDisable(false);
        int packSize = 0;

        try {
            packSize = purchaseOrderBO.splitPackSize(cmbItemCode.getValue(), txtPackSize.getText());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        int orderQty = Integer.parseInt(txtOrderQTY.getText());

        double discountPerUnit = 0;
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        double subTotal = unitPrice * packSize * orderQty;
        double discount = 0;
        double total = 0;

        if (txtDiscount.getText() != "00") {
            discountPerUnit = Double.parseDouble(txtDiscount.getText());
            discount = subTotal * discountPerUnit / 100;
        }

        total = subTotal - discount;

        if (qtyOnHand < orderQty) {
            new Alert(Alert.AlertType.WARNING, "Invalid Order QTY...").show();
            return;
        }

        OrderListTM tm = new OrderListTM(
                cmbItemCode.getValue(),
                cmbItemDescription.getValue(),
                txtPackSize.getText(),
                unitPrice,
                orderQty,
                subTotal,
                discount,
                total
        );

        int rowNumber = isExists(tm);

        if (rowNumber == -1) { // adds a new record
            orderList.add(tm);

        } else { // updates the existing record

            OrderListTM tmItemForUpdate = orderList.get(rowNumber); // get the OrderListTM object / element in this particular rowNumber
            OrderListTM tmUpdatedItem = new OrderListTM(
                    tmItemForUpdate.getItemCode(),
                    tmItemForUpdate.getDescription(),
                    tmItemForUpdate.getPackSize(),
                    unitPrice,
                    orderQty + tmItemForUpdate.getOrderQTY(), // new + old
                    subTotal + tmItemForUpdate.getSubTotal(),
                    discount + tmItemForUpdate.getDiscount(),
                    total + tmItemForUpdate.getTotal()
            );

            orderList.remove(rowNumber);
            orderList.add(tmUpdatedItem);
        }
        tblOrderItem.setItems(orderList);
        btnConfirmOrder.setDisable(false);
        calculateOrderTotalCost();
        clearItemFields();
    }

    public void calculateOrderTotalCost() {
        double orderSubTotal = 0;
        double orderDiscount = 0;
        double orderCost = 0;

        for (OrderListTM otm : orderList) {
            orderSubTotal += otm.getSubTotal();
            orderDiscount += otm.getDiscount();
            orderCost += otm.getTotal();
        }

        lblOrderSubTotal.setText(orderSubTotal + "");
        lblOrderTotalDiscount.setText(orderDiscount + "");
        lblOrderCost.setText(orderCost + "");
        lblAmountPaid.setText("0.00");
        lblBalance.setText("0.00");
    }

    private int isExists(OrderListTM tm) { // checks whether already there has been placed an order from the given itemCode by the particular customer
        for (int i = 0; i < orderList.size(); i++) {
            if (tm.getItemCode().equals(orderList.get(i).getItemCode())) {
                return i;
            }
        }
        return -1;
    }

    public void payOrderOnAction(ActionEvent actionEvent) {
        if (txtPayment.getText().equals("")) {
            new Alert(Alert.AlertType.INFORMATION, "Please do the Payment to Place the Order... ", ButtonType.OK).show();
            return;
        }

        double payment = Double.parseDouble(txtPayment.getText());
        double balance = payment - Double.parseDouble(lblOrderCost.getText());

        if (payment < Double.parseDouble(lblOrderCost.getText())) {
            new Alert(Alert.AlertType.WARNING, "Insufficient Amount.\nOrder Cost is " + lblOrderCost.getText(), ButtonType.OK).show();
            return;
        }

        String balanceFormatted = String.format("%.1f", balance);
        lblAmountPaid.setText(String.valueOf(payment));
        //lblBalance.setText(String.format("%.1f",balance));
        lblBalance.setText(balanceFormatted);
        lblPaymentDate.setText(lblDate.getText());
        lblPaymentTime.setText(lblTime.getText());

        if (!lblAmountPaid.getText().equals("0.00")) {
            new Alert(Alert.AlertType.INFORMATION, "Payment Successful.\nYour Balance is Rs " + balanceFormatted, ButtonType.OK).show();
            txtPayment.clear();

        } else {
            new Alert(Alert.AlertType.WARNING, "Payment Unsuccessful.\nPlease Insert the Amount to make the Payment.", ButtonType.OK).show();
        }
    }

    public void editItemOnAction(ActionEvent actionEvent) {

        ItemDTO item = null;
        try {
            item = purchaseOrderBO.getItem(itemSelected.getItemCode());
            cmbItemCode.setValue(itemSelected.getItemCode());
            cmbItemDescription.setValue(itemSelected.getDescription());
            txtPackSize.setText(itemSelected.getPackSize());
            txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
            txtOrderQTY.setText(String.valueOf(itemSelected.getOrderQTY()));
            txtUnitPrice.setText(String.valueOf(itemSelected.getUnitPrice()));
            txtDiscount.setText(new DiscountRepoImpl().getDiscount(itemSelected.getItemCode()));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeItemFromListOnAction(ActionEvent actionEvent) {
        orderList.remove(itemSelected);
        tblOrderItem.setItems(orderList);
        tblOrderItem.refresh();

        calculateOrderTotalCost();
        clearItemFields();
        clearPaymentInfo();
    }

    public void confirmOrderOnAction(ActionEvent actionEvent) {

        //--selected Customer to place the Order
        CustomerDTO placeOrderCustomer = new CustomerDTO(
                txtCustId.getText(),
                txtCustTitle.getText(),
                txtCustName.getText(),
                txtCustAddress.getText(),
                txtCustCity.getText(),
                txtCustProvince.getText(),
                txtCustPostalCode.getText()
        );

        String custID = null;
        try {
            if (txtCustId.getText().equals("")) {
                custID = purchaseOrderBO.generateCustomerID();

            } else {
                custID = txtCustId.getText();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        OrderDTO orderDTO = new OrderDTO(
                lblOrderID.getText(),
                java.sql.Date.valueOf(lblDate.getText()),
                custID,
                Double.parseDouble(lblOrderCost.getText())
        );

        ArrayList<OrderDetailDTO> items = new ArrayList<>();

        for (OrderListTM otm : orderList) {
            items.add(new OrderDetailDTO(
                    lblOrderID.getText(),
                    otm.getItemCode(),
                    otm.getOrderQTY(),
                    otm.getDiscount()
            ));
        }

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        try {
            if (lblAmountPaid.getText().equals("0.00")) {
                new Alert(Alert.AlertType.WARNING, "No Payment done.\nPlease make the Payment to Place Order.", ButtonType.OK).show();

            } else if (txtCustName.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No Customer Selected.\nDo you want to continue Confirmation?", yes, no);
                alert.setTitle("Confirmation Alert");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.orElse(no) == yes) {
                    CustomerDTO custNoDetails = new CustomerDTO(custID);

                    placeOrderWithoutCustomer(custNoDetails, orderDTO, items);
                }

            } else if (!txtCustName.getText().equals("")) {
                placeOrderWithCustomer(placeOrderCustomer, orderDTO, items);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void placeOrderWithCustomer(CustomerDTO placeOrderCustomer, OrderDTO newOrder, ArrayList<OrderDetailDTO> items) throws SQLException, ClassNotFoundException {
        if (!lblAmountPaid.getText().equals("0.00")) {

            if (purchaseOrderBO.purchaseOrder(newOrder, items)) { // return true if both order table and order detail table gets updated.
                new Alert(Alert.AlertType.CONFIRMATION, "Order Confirmation Successful").show();
                generateInvoice();
                setOrderId();
                clearCustomerFields();
                clearItemFields();
                clearTable();
                clearPaymentInfo();

            } else {
                new Alert(Alert.AlertType.WARNING, "Error Occurred During Order Confirmation.\nTry Again...").show();
            }
        } else if (lblAmountPaid.getText().equals("0.00")) {
            new Alert(Alert.AlertType.WARNING, "No Payment done.\nPlease make the Payment to Place Order.", ButtonType.OK).show();
        }

    }

    private void placeOrderWithoutCustomer(CustomerDTO custNoDetails, OrderDTO newOrder, ArrayList<OrderDetailDTO> items) throws SQLException, ClassNotFoundException {
        String custID = purchaseOrderBO.generateCustomerID();
        custNoDetails.setCustID(custID);
        purchaseOrderBO.addCustomer(custNoDetails);
        loadCustomerIds();

        if (!lblAmountPaid.getText().equals("0.00")) {

            if (purchaseOrderBO.purchaseOrder(newOrder, items)) { // return true if both order table and order detail table gets updated.
                new Alert(Alert.AlertType.CONFIRMATION, "Order Confirmation Successful").show();
                generateInvoice();
                setOrderId();
                clearCustomerFields();
                clearItemFields();
                clearTable();
                clearPaymentInfo();

            } else {
                new Alert(Alert.AlertType.WARNING, "Error Occurred During Order Confirmation.\nTry Again...").show();
            }
        } else if (lblAmountPaid.getText().equals("0.00")) {
            new Alert(Alert.AlertType.WARNING, "No Payment done.\nPlease make the Payment to Place Order.", ButtonType.OK).show();
        }
    }

    public void clearTable() {
        tblOrderItem.getItems().clear();
        tblOrderItem.refresh();
    }

    private void clearPaymentInfo() {
        if (orderList.isEmpty()) {
            btnConfirmOrder.setDisable(true);

            lblOrderSubTotal.setText("0.00");
            lblOrderTotalDiscount.setText("0.00");
            lblOrderCost.setText("0.00");

            lblAmountPaid.setText("0.00");
            lblBalance.setText("0.00");

            lblPaymentDate.setText("");
            lblPaymentTime.setText("");
        }
    }

    public void cancelOrderOnAction(ActionEvent actionEvent) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to Cancel this Order?", yes, no);
        alert.setTitle("Confirmation Alert");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(no) == yes) {
            try {
                clearCustomerFields();
                clearItemFields();
                clearTable();
                clearPaymentInfo();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateInvoice() {
        try {
            JasperDesign design = JRXmlLoader.load(this.getClass().getResourceAsStream("../view/reports/Invoice.jrxml"));
            JasperReport compileReport = JasperCompileManager.compileReport(design);

            /*setting values for parameters*/
            String orderId = lblOrderID.getText();
            String invoiceNo = purchaseOrderBO.generateInvoiceId(orderId);
            CustomerDTO customerDTO = purchaseOrderBO.getCustomerOfOrder(orderId);
            String custId = customerDTO.getCustID();
            String custName = customerDTO.getCustTitle() + "." + customerDTO.getCustName();
            String date = lblDate.getText();
            double subTotal = Double.parseDouble(lblOrderSubTotal.getText());
            double discount = Double.parseDouble(lblOrderTotalDiscount.getText());
            double totalInvoice = Double.parseDouble(lblOrderCost.getText());
            double amountPaid = Double.parseDouble(lblAmountPaid.getText());
            double balance = Double.parseDouble(lblBalance.getText());

            /*mapping parameter values*/
            HashMap map = new HashMap();
            map.put("invoiceNo", invoiceNo);
            map.put("date", date);
            map.put("custID", custId);
            map.put("custName", custName);
            map.put("orderId", orderId);

            map.put("subTotal", subTotal);
            map.put("discount", discount);
            map.put("totalInvoice", totalInvoice);
            map.put("amountPaid", amountPaid);
            map.put("balance", balance);

            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, map, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void goToPreviousPageOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/CashierDashBoardForm.fxml");
        NavigationUtil.navigateToPage(resource, contextPlaceOrder);
    }

    public void logoutOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/MainForm.fxml");
        NavigationUtil.logOutOnAction(resource, contextPlaceOrder);
    }
}















