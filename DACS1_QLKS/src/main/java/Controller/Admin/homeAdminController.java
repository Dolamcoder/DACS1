package Controller.Admin;

import Controller.Login.LoginController;
import Dao.Admin.AuditLogDAO;
import Model.AuditLog;
import Util.JDBC;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import Dao.Admin.AuditLogDAO;
import Model.AuditLog;
import Util.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class homeAdminController {
    // Add these fields to your class
    private AuditLogDAO auditLogDAO;
    private ObservableList<AuditLog> auditLogList = FXCollections.observableArrayList();
    @FXML
    private AnchorPane centerPane;
    @FXML
    private Button accountButton;

    @FXML
    private TableColumn<AuditLog, String> actionByColum;

    @FXML
    private TableColumn<AuditLog, String> actionColum;

    @FXML
    private Button avartarButton;

    @FXML
    private Button baoCaoButton;

    @FXML
    private FontAwesomeIcon exitButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label nameLabel;

    @FXML
    private TableColumn<AuditLog, String> nameTableColum;
    @FXML
    private TableColumn<AuditLog, String> recordColum;

    @FXML
    private Button roomButton;
    @FXML
    private Button serviceButton;

    @FXML
    private Button staffButton;

    @FXML
    private TableColumn<AuditLog, Integer> sttColum;

    @FXML
    private TableView<AuditLog> table;

    @FXML
    private TextField timKiemText;

    @FXML
    private TableColumn<?, ?> timeColum;

    private void setupTable() {
        // Set the type for TableView
        TableView<AuditLog> auditTable = (TableView<AuditLog>) table;

        // Configure columns
        sttColum.setCellValueFactory(new PropertyValueFactory<>("logID"));
        nameTableColum.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        recordColum.setCellValueFactory(new PropertyValueFactory<>("recordID"));
        actionColum.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionByColum.setCellValueFactory(new PropertyValueFactory<>("actionBy"));
        timeColum.setCellValueFactory(new PropertyValueFactory<>("actionAt"));

        // Setup search functionality
        timKiemText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAuditLogs(newValue);
        });
    }

    private void loadHoatDong() {
        // Initialize DAO with connection
        auditLogDAO = new AuditLogDAO();

        // Clear previous data
        auditLogList.clear();

        // Get all audit logs from database
        ArrayList<AuditLog> logs = auditLogDAO.selectAll();

        // Add to observable list
        auditLogList.addAll(logs);

        // Set items to table
        TableView<AuditLog> auditTable = (TableView<AuditLog>) table;
        auditTable.setItems(auditLogList);
    }

    private void filterAuditLogs(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            table.setItems(auditLogList);
            return;
        }

        String lowercaseKeyword = keyword.toLowerCase();

        ObservableList<AuditLog> filteredList = auditLogList.stream()
                .filter(log ->
                        String.valueOf(log.getLogID()).contains(lowercaseKeyword) ||
                                (log.getTableName() != null && log.getTableName().toLowerCase().contains(lowercaseKeyword)) ||
                                (log.getRecordID() != null && log.getRecordID().toLowerCase().contains(lowercaseKeyword)) ||
                                (log.getAction() != null && log.getAction().toLowerCase().contains(lowercaseKeyword)) ||
                                (log.getActionBy() != null && log.getActionBy().toLowerCase().contains(lowercaseKeyword))
                )
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        table.setItems(filteredList);
    }

    @FXML
    void handleAccount(ActionEvent event) {
        try {
            AnchorPane ListAccount= FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListAccount.fxml"));
            this.centerPane.getChildren().clear();
            this.centerPane.getChildren().add(ListAccount);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load: " + e.getMessage());
        }
    }

    @FXML
    void handleBaoCao(ActionEvent event) {
        try {
            AnchorPane ListAccount= FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/baoCao_thongKe.fxml"));
            this.centerPane.getChildren().clear();
            this.centerPane.getChildren().add(ListAccount);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load: " + e.getMessage());
        }
    }

    @FXML
    void handleRoom(ActionEvent event) {
        try {
            AnchorPane listRoom= FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListRoom.fxml"));
            this.centerPane.getChildren().clear();
            this.centerPane.getChildren().add(listRoom);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load: " + e.getMessage());
        }
    }

    @FXML
    void handleService(ActionEvent event) {
        try {
            AnchorPane listRoom= FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListService.fxml"));
            this.centerPane.getChildren().clear();
            this.centerPane.getChildren().add(listRoom);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load: " + e.getMessage());
        }
    }

    @FXML
    void handleStaff(ActionEvent event) {
        try{
            AnchorPane listStaff= FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListStaff.fxml"));
            this.centerPane.getChildren().clear();
            this.centerPane.getChildren().add(listStaff);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể load: " + e.getMessage());
        }
    }

    @FXML
    void home(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/Quan_Ly/HomeAdmin.fxml"));
        Parent root=loader.load();
        Stage stage=new Stage();
        stage.setScene((new Scene(root)));
        stage.show();
        Stage currentStage = (Stage) ((Node) this.logoutButton).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/FXML/DangNhap/Login.fxml"));
        Parent root=loader.load();
        Stage stage=new Stage();
        stage.setScene((new Scene(root)));
        stage.show();
        Stage currentStage = (Stage) ((Node) this.logoutButton).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void initialize() {
        this.nameLabel.setText(LoginController.account.getName());
        // Setup table structure
        setupTable();

        // Load audit log data
        loadHoatDong();
    }

}
