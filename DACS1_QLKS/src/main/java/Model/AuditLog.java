package Model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AuditLog {
    private int logID;
    private String tableName;
    private String recordID;
    private String action;
    private String actionBy;
    private java.sql.Timestamp actionAt;

    // Default constructor with current date/time
    public AuditLog() {
        this.actionAt = Timestamp.valueOf(LocalDateTime.now());
    }

    // Convenience constructor that automatically sets current date/time
    public AuditLog( String tableName, String recordID, String action, String actionBy) {
        this.tableName = tableName;
        this.recordID = recordID;
        this.action = action;
        this.actionBy = actionBy;
        this.actionAt = Timestamp.valueOf(LocalDateTime.now());
    }

    // Full constructor for when specific date/time values are needed
    public AuditLog(int logID, String tableName, String recordID, String action, String actionBy,
                     java.sql.Timestamp actionAt) {
        this.logID = logID;
        this.tableName = tableName;
        this.recordID = recordID;
        this.action = action;
        this.actionBy = actionBy;
        this.actionAt = actionAt;
    }

    // Getters and Setters
    public int getLogID() { return logID; }
    public void setLogID(int logID) { this.logID = logID; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getRecordID() { return recordID; }
    public void setRecordID(String recordID) { this.recordID = recordID; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getActionBy() { return actionBy; }
    public void setActionBy(String actionBy) { this.actionBy = actionBy; }

    public java.sql.Timestamp getActionAt() { return actionAt; }
    public void setActionAt(java.sql.Timestamp actionAt) { this.actionAt = actionAt; }
}