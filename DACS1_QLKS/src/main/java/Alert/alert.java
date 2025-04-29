package Alert;

import javafx.scene.control.Alert;

public class alert {
    public void showErrorAlert( String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Horizon Bliss");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void showInfoAlert( String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Horizon Bliss");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
