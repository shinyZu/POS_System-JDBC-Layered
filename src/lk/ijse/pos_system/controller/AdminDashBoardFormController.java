package lk.ijse.pos_system.controller;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos_system.util.NavigationUtil;

import java.io.IOException;
import java.net.URL;

public class AdminDashBoardFormController {
    public AnchorPane contextAdminDashBoard;
    private URL resource;

    public void goToManageItemsFormOnAction(ActionEvent actionEvent) throws IOException {
        resource = getClass().getResource("../view/ManageItemsForm.fxml");
        NavigationUtil.navigateToPage(resource, contextAdminDashBoard);
    }

    public void goToSystemReportsOnAction(ActionEvent actionEvent) throws IOException {
        resource = getClass().getResource("../view/SystemReportForm.fxml");
        NavigationUtil.navigateToPage(resource, contextAdminDashBoard);
    }

    public void logoutOnAction(MouseEvent mouseEvent) throws IOException {
        resource = getClass().getResource("../view/MainForm.fxml");
        NavigationUtil.logOutOnAction(resource, contextAdminDashBoard);
    }

}