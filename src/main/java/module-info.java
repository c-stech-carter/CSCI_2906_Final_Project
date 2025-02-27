module carter.stech.librarysystemv2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;

    opens carter.stech.librarysystemv2 to javafx.fxml;
    exports carter.stech.librarysystemv2;
}