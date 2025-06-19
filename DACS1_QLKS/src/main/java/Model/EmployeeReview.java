package Model;
import java.time.LocalDate;
public class EmployeeReview {
    private int reviewID;
    private String employeeID;
    private LocalDate reviewDate;
    private int ratingScore;
    private String comments;
    private double bonusAmount;
    // Constructors
    public EmployeeReview() {}

    public EmployeeReview(int reviewID, String employeeID, LocalDate reviewDate, int ratingScore, String comments, double bonusAmount) {
        this.reviewID = reviewID;
        this.employeeID = employeeID;
        this.reviewDate = reviewDate;
        this.ratingScore = ratingScore;
        this.comments = comments;
        this.bonusAmount = bonusAmount;
    }

    // Getters and Setters
    public int getReviewID() { return reviewID; }
    public void setReviewID(int reviewID) { this.reviewID = reviewID; }

    public String getEmployeeID() { return employeeID; }
    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }

    public int getRatingScore() { return ratingScore; }
    public void setRatingScore(int ratingScore) { this.ratingScore = ratingScore; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public double getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(double bonusAmount) { this.bonusAmount = bonusAmount; }
}
