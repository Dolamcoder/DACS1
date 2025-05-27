package Controller.Admin;

import Dao.Admin.AuditLogDAO;
import Model.AuditLog;

public class AuditLogController {
    public static void getAuditLog(String tableName, String recordID, String action, String actionBy) {
        AuditLog auditLog=new AuditLog(tableName, recordID, action, actionBy);
        AuditLogDAO auditLogDAO=new AuditLogDAO();
        if (auditLogDAO.insert(auditLog)) {
            System.out.println("Audit log inserted successfully.");
        } else {
            System.out.println("Failed to insert audit log.");
        }
    }
}
