module carter.stech.librarysystemv2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens carter.stech.librarysystemv2 to javafx.fxml;
    exports carter.stech.librarysystemv2;
}