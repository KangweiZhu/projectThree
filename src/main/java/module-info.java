module com.example.projectthree {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.projectthree to javafx.fxml;
    exports com.example.projectthree;
}