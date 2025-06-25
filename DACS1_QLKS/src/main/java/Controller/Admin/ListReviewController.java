package Controller.Admin;

import Controller.Login.LoginController;
import Dao.Admin.EmployeeDao;
import Dao.Admin.EmployeeReviewDAO;
import Model.EmployeeReview;
import Model.Salary;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListReviewController {

    @FXML
    private Button add;

    @FXML
    private TableColumn<EmployeeReview, Double> bonusColum;

    @FXML
    private TextField bonusText;

    @FXML
    private TableColumn<EmployeeReview, String> commentColum;

    @FXML
    private TextArea commentText;

    @FXML
    private DatePicker date;

    @FXML
    private TableColumn<EmployeeReview, String> employeeIDColum;

    @FXML
    private ComboBox<String> employeeIDText;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TableColumn<EmployeeReview, Integer> ratingCoreColum;

    @FXML
    private TextField ratingCoreText;

    @FXML
    private Button remove;

    @FXML
    private DatePicker reviewDate;

    @FXML
    private TableColumn<EmployeeReview, LocalDate> reviewDateColum;

    @FXML
    private TableColumn<EmployeeReview, Integer> reviewIDColum;

    @FXML
    private TextField reviewIDText;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField searchText;

    @FXML
    private ComboBox<String> select;

    @FXML
    private Button update;

    @FXML
    private TableView<EmployeeReview> reviewTable;

    private EmployeeReviewDAO reviewDAO;
    private ObservableList<EmployeeReview> reviewList;
    private ObservableList<String> employeeIDs;
    private EmployeeDao empDao=new EmployeeDao();
    @FXML
    public void initialize() {
        reviewDAO = new EmployeeReviewDAO();
        reviewList = FXCollections.observableArrayList();
        employeeIDs = FXCollections.observableArrayList();
        // Initialize DatePicker with current date
        date.setValue(LocalDate.now());
        setUpTable();
        setupSearchFunction();
    }
    public void setUpTable(){
        // Setup table columns
        reviewIDColum.setCellValueFactory(new PropertyValueFactory<>("reviewID"));
        employeeIDColum.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        reviewDateColum.setCellValueFactory(new PropertyValueFactory<>("reviewDate"));
        ratingCoreColum.setCellValueFactory(new PropertyValueFactory<>("ratingScore"));
        commentColum.setCellValueFactory(new PropertyValueFactory<>("comments"));
        bonusColum.setCellValueFactory(new PropertyValueFactory<>("bonusAmount"));

        employeeIDs.addAll(empDao.getAllEmployeeIDs());
        employeeIDText.setItems(employeeIDs);

        // Load initial data
        loadReviews();
        select.setItems(FXCollections.observableArrayList("Đánh giá nhân viên", "Danh sách nhân viên", "Lương nhân viên"));
        select.setValue("Đánh giá nhân viên");
        // Add listener to date picker
        date.valueProperty().addListener((obs, oldDate, newDate) -> loadReviews());

        // Add selection listener to table
        reviewTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showReviewDetail(newSelection);
            }
        });
    }
    private void loadReviews() {
        reviewList.clear();
        if (date.getValue() != null) {
            int month = date.getValue().getMonthValue();
            int year = date.getValue().getYear();
            List<EmployeeReview> reviews = reviewDAO.getReviewsByDate(month, year);
            if (reviews != null) {
                reviewList.addAll(reviews);
                reviewTable.setItems(reviewList);
            }
        }
        progressBar.setProgress(reviewList.isEmpty() ? 0 : 1);
    }
    private void setupSearchFunction() {
        // Add listener to search text field to filter results as user types
        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEmployees(newValue);
        });
    }

    private void filterEmployees(String keyword) {
        if (keyword == null) {
            keyword = "";
        }

        String searchTerm = keyword.trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            reviewTable.setItems(reviewList);
            return;
        }

        List<EmployeeReview> filtered = reviewList.stream()
                .filter(x -> {
                    String reviewID = String.valueOf(x.getReviewID());
                    String employeeID = x.getEmployeeID() != null ? x.getEmployeeID() : "";
                    String date = x.getReviewDate() != null ? x.getReviewDate().toString() : "";
                    String ratingScore = String.valueOf(x.getRatingScore());
                    String comments = x.getComments() != null ? x.getComments() : "";
                    String bonusAmount = String.valueOf(x.getBonusAmount());

                    return reviewID.toLowerCase().contains(searchTerm) ||
                            employeeID.toLowerCase().contains(searchTerm) ||
                            date.toLowerCase().contains(searchTerm) ||
                            ratingScore.toLowerCase().contains(searchTerm) ||
                            comments.toLowerCase().contains(searchTerm) ||
                            bonusAmount.toLowerCase().contains(searchTerm);
                })
                .collect(Collectors.toList());

        reviewTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void showReviewDetail(EmployeeReview review) {
        reviewIDText.setText(String.valueOf(review.getReviewID()));
        employeeIDText.setValue(review.getEmployeeID());
        reviewDate.setValue(review.getReviewDate());
        ratingCoreText.setText(String.valueOf(review.getRatingScore()));
        commentText.setText(review.getComments());
        bonusText.setText(String.valueOf(review.getBonusAmount()));
    }

    private void clearFields() {
        reviewIDText.clear();
        employeeIDText.setValue(null);
        reviewDate.setValue(LocalDate.now());
        ratingCoreText.clear();
        commentText.clear();
        bonusText.clear();
    }

    @FXML
    void add(ActionEvent event) {
        try {
            EmployeeReview review = getReview();
            if (review == null) return;

            if (reviewDAO.insertReview(review)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Thêm thành công");
                reviewList.add(review);
                AuditLogController.getAuditLog("Employee_Review", review.getReviewID()+"","Thêm đánh giá", LoginController.account.getName());
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Thêm thất bại");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    void update(ActionEvent event) {
        try {
            EmployeeReview review = getReview();
            if (review == null) return;

            if (reviewDAO.updateReview(review)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cập nhật đánh giá thành công.");
                loadReviews();
                AuditLogController.getAuditLog("Employee_Review", review.getReviewID()+"","Cập nhật đánh giá", LoginController.account.getName());
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Cập nhật đánh giá thất bại.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",  e.getMessage());
        }
    }

    @FXML
    void remove(ActionEvent event) {
        EmployeeReview selected = reviewTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a review to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xoá đánh giá này?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (reviewDAO.deleteReview(selected.getReviewID())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Xoá đánh giá thành công.");
                reviewList.remove(selected);
                AuditLogController.getAuditLog("Employee_Review", selected.getReviewID()+"","Xoá đánh giá", LoginController.account.getName());
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Xoá đánh giá thất bại.");
            }
        }
    }

    @FXML
    void clickSelect(ActionEvent event) throws IOException {
        String selectedOption = select.getValue();
        if (selectedOption.equals("Lương nhân viên")) {
            AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListSalary.fxml"));
            this.rootPane.getChildren().clear();
            this.rootPane.getChildren().add(selectPane);
        } else if (selectedOption.equals("Danh sách nhân viên")) {
            AnchorPane selectPane = FXMLLoader.load(getClass().getResource("/org/FXML/Quan_ly/ListStaff.fxml"));
            this.rootPane.getChildren().clear();
            this.rootPane.getChildren().add(selectPane);
        }
    }

    @FXML
    void selectEmployee(ActionEvent event) {
        String employeeID = employeeIDText.getValue();
        EmployeeReview empReview= reviewDAO.getEmployeeReviewByEmployeeID(employeeID);
        if(empReview != null) {
            showReviewDetail(empReview);
        }
        else{
            reviewIDText.clear();
            ratingCoreText.clear();
            commentText.clear();
            bonusText.clear();
        }
    }

    private EmployeeReview getReview() throws Exception {
        if (employeeIDText.getValue() == null || reviewDate.getValue() == null ||
                ratingCoreText.getText().isEmpty() || bonusText.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all required fields.");
            return null;
        }

        EmployeeReview review = new EmployeeReview();
            try {
                review.setReviewID(Integer.parseInt(reviewIDText.getText()));
            } catch (NumberFormatException e) {
                throw new Exception("Invalid Review ID.");
            }

        review.setEmployeeID(employeeIDText.getValue());
        review.setReviewDate(reviewDate.getValue());
        try {
            review.setRatingScore(Integer.parseInt(ratingCoreText.getText()));
        } catch (NumberFormatException e) {
            throw new Exception("Rating Score must be a number.");
        }
        review.setComments(commentText.getText());
        try {
            review.setBonusAmount(Double.parseDouble(bonusText.getText()));
        } catch (NumberFormatException e) {
            throw new Exception("Bonus Amount must be a number.");
        }

        return review;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}