module org {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires mysql.connector.j;
    requires java.mail;
    requires org.apache.poi.ooxml;
    requires itextpdf;
    requires fontawesomefx;

    opens View to javafx.fxml;
    exports View;
    opens Controller.Employee to javafx.fxml;
    exports Controller.Employee;
    opens Controller.Login to javafx.fxml;
    exports Controller.Login;
    opens Model to javafx.base;
    exports Model;
    opens Controller.Invoice to javafx.fxml;
    exports Controller.Invoice;
    opens Controller.Admin to javafx.fxml;
    exports Controller.Admin;
}