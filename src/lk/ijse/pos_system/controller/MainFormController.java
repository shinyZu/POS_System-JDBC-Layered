package lk.ijse.pos_system.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.pos_system.business.BOFactory;
import lk.ijse.pos_system.business.custom.VerifyUserBO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class MainFormController {

    public Label lblRegistered;
    public Label lblLogin;
    public Label lblNewUser;
    public Label lblSignUp;
    public JFXButton btnAdmin;
    public JFXButton btnCashier;
    public JFXButton btnSignUp;
    public JFXButton btnLogin;
    public JFXTextField txtUsername;
    public JFXTextField txtPassword;
    public JFXPasswordField fieldPassword;
    public MaterialDesignIconView iconHidePassword;
    public MaterialDesignIconView iconShowPassword;
    public MaterialDesignIconView btnClose;

    private String userRole = "Admin";
    private final VerifyUserBO verifyUserBO = (VerifyUserBO) BOFactory.getBOFactoryInstance().getBO(BOFactory.BOTypes.VERIFY_USER);
    private KeyEvent event;

    LinkedHashMap<TextField, Pattern> mapLoginDetails = new LinkedHashMap<>();
    String userNameRegEx = "^[A-Z][a-z]{4,}$"; //Admin, Cashier
    String userPwdRegEx = "^[a-z]*[0-9]{3,}$"; // admin123, cashier123
    Pattern userNamePtn = Pattern.compile(userNameRegEx);
    Pattern userPwdPtn = Pattern.compile(userPwdRegEx);

    public void initialize() {
        txtPassword.setOnKeyReleased(event -> {
            this.event = event;
            fieldPassword.setText(txtPassword.getText());
            validateFieldOnKeyRelease(event);
            validateHiddenPwdTextField();
        });
        mapValidations();
    }

    private void mapValidations() {
        mapLoginDetails.put(txtUsername,userNamePtn);
        mapLoginDetails.put(fieldPassword,userPwdPtn);
    }

    public void validateFieldOnKeyRelease(KeyEvent keyEvent) {
        Object response = null;
        response = validate();

        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (response instanceof TextField) {
                ((TextField) response).requestFocus();

            } else if (response instanceof Boolean) {
                //
            }
        }
    }

    private Object validate() {
        for (TextField keyTextField: mapLoginDetails.keySet()) {
            Pattern valuePattern = mapLoginDetails.get(keyTextField);

            if (!valuePattern.matcher(keyTextField.getText()).matches()) {
                if (!keyTextField.getText().isEmpty()) {
                    keyTextField.getStylesheets().clear();
                    keyTextField.getStylesheets().add(getClass().getResource("../view/assets/styles/invalidInput.css").toString());
                }
                return keyTextField;
            }
            keyTextField.getStylesheets().clear();
            keyTextField.getStylesheets().add(getClass().getResource("../view/assets/styles/validInput.css").toString());
        }
        return true;
    }

    private void validateHiddenPwdTextField() {
        boolean pwdMatches = Pattern.matches(userPwdRegEx, txtPassword.getText());
        txtPassword.getStylesheets().clear();
        if (pwdMatches) {
            txtPassword.getStylesheets().add("lk/ijse/pos_system/view/assets/styles/validInput.css");
            //btnLogin.requestFocus();
        } else {
            txtPassword.getStylesheets().add("lk/ijse/pos_system/view/assets/styles/invalidInput.css");
            fieldPassword.getStylesheets().add("lk/ijse/pos_system/view/assets/styles/validInput.css");
        }
    }

    public void goToAdminLoginFormOnAction(ActionEvent actionEvent) {
        userRole = btnAdmin.getText();
        btnAdmin.setStyle("-fx-background-color: #297F87; -fx-background-radius: 10 0 0 10;");
        btnCashier.setStyle("-fx-background-color: #999 ; -fx-background-radius: 0 10 10 0;");
        clearFields();
        setFieldsToInitialState();
    }

    public void goToCashierLoginFormOnAction(ActionEvent actionEvent) {
        userRole = btnCashier.getText();
        btnAdmin.setStyle("-fx-background-color: #999; -fx-background-radius: 10 0 0 10;");
        btnCashier.setStyle("-fx-background-color:  #297f87; -fx-background-radius: 0 10 10 0;");
        clearFields();
        setFieldsToInitialState();
    }

    private void setFieldsToInitialState() {
        txtUsername.getStylesheets().clear();
        txtPassword.getStylesheets().clear();
        fieldPassword.getStylesheets().clear();
    }

    private void clearFields() {
        txtUsername.clear();
        txtPassword.clear();
        fieldPassword.clear();
    }

    public void hidePasswordOnAction(MouseEvent mouseEvent) {
        iconHidePassword.setVisible(false);
        iconShowPassword.setVisible(true);

        fieldPassword.setVisible(true);
        fieldPassword.setText(txtPassword.getText());
        validateFieldOnKeyRelease(event);
        fieldPassword.setLabelFloat(true);

        txtPassword.setVisible(false);
    }

    String fPassword;
    public void showPasswordOnAction(MouseEvent mouseEvent) {
        iconShowPassword.setVisible(false);
        iconHidePassword.setVisible(true);

        txtPassword.setText(fieldPassword.getText());
        txtPassword.setVisible(true);
        txtPassword.setLabelFloat(true);

        fieldPassword.setLabelFloat(false);
        validateHiddenPwdTextField();
        fieldPassword.setVisible(false);
        fieldPassword.toBack();
        this.fPassword = fieldPassword.getText();
        fieldPassword.clear();
    }

    public void displayLoginBtnOnAction(MouseEvent mouseEvent) {
        txtUsername.requestFocus();

        btnLogin.setVisible(true);
        btnSignUp.setVisible(false);

        lblNewUser.setVisible(true);
        lblSignUp.setVisible(true);
        lblLogin.setVisible(false);
        lblRegistered.setVisible(false);

    }

    public void displaySignUpBtnOnAction(MouseEvent mouseEvent) {
        txtUsername.requestFocus();

        btnSignUp.setVisible(true);
        btnLogin.setVisible(false);

        lblRegistered.setVisible(true);
        lblLogin.setVisible(true);
        lblNewUser.setVisible(false);
        lblSignUp.setVisible(false);

    }

    public void signUpOnAction(ActionEvent actionEvent) {
    }

    public void loginOnAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        Stage window = new Stage();
        //window.initStyle(StageStyle.UNDECORATED);
        String username = txtUsername.getText();
        String tPassword = txtPassword.getText();
        //String fPassword = fieldPassword.getText();
        if (this.fPassword == null) {
            this.fPassword = fieldPassword.getText();
        }

        if (userRole.equals("Admin")) {
            if (verifyUserBO.verifyUser(userRole, username, tPassword, this.fPassword)) {
                window.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/AdminDashBoardForm.fxml"))));
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.close();

            } else {
                new Alert(Alert.AlertType.WARNING, "Invalid UserName or Password...").show();
                return;
            }

        } else if (userRole.equals("Cashier")) {
            if (verifyUserBO.verifyUser(userRole,username, tPassword, fPassword)) {
                window.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/CashierDashBoardForm.fxml"))));
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.close();

            } else {
                new Alert(Alert.AlertType.WARNING, "Invalid UserName or Password...").show();
                return;
            }
        }
        window.show();
    }

    public void closeLoginPageOnClick(MouseEvent mouseEvent) throws IOException {
        new Stage().setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/MainForm.fxml"))));
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
