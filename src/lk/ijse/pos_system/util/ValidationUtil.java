package lk.ijse.pos_system.util;

import javafx.scene.control.TextField;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ValidationUtil {

    public static Object validateFormFields(LinkedHashMap<TextField, Pattern> mapItemDetails) {
        for (TextField keyTextField : mapItemDetails.keySet()) {
            Pattern valuePatten = mapItemDetails.get(keyTextField);

            if (!valuePatten.matcher(keyTextField.getText()).matches()) { // if the inserted text doesn't match with pattern
                if (!keyTextField.getText().isEmpty()) {
                    keyTextField.setStyle("-fx-border-color: red; -fx-background-color: transparent; -fx-border-radius: 3;");
                }
                return keyTextField;

            }
            keyTextField.setStyle("-fx-border-color: green; -fx-background-color: transparent; -fx-border-radius: 3;");

        }
        return true;
    }
}
