package lk.ijse.pos_system.controller;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos_system.util.NavigationUtil;

import java.io.IOException;
import java.net.URL;

public class CashierDashBoardFormController {
    public AnchorPane contextCashierDashBoard;
    private URL resource;

    public void goToPlaceCustomerOrderFormOnAction(ActionEvent actionEvent) throws IOException {
        resource = getClass().getResource("../view/PlaceCustomerOrderForm.fxml");
        NavigationUtil.navigateToPage(resource, contextCashierDashBoard);
    }

    public void goToManageOrderFormOnAction(ActionEvent actionEvent) throws IOException {
        resource = getClass().getResource("../view/ManageOrdersForm.fxml");
        NavigationUtil.navigateToPage(resource, contextCashierDashBoard);
    }

    public void logoutOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/MainForm.fxml");
        NavigationUtil.logOutOnAction(resource, contextCashierDashBoard);
    }
}
