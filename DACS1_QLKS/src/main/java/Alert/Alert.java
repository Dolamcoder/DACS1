package Alert;

public class Alert {
    public void showErrorAlert( String message){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Horizon Bliss");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void showInfoAlert( String message){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Horizon Bliss");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
