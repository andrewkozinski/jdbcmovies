module org.example.andrew_kozinski_assignment4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires com.healthmarketscience.jackcess;
    requires com.google.gson;


    opens org.example.andrew_kozinski_assignment4 to javafx.fxml, com.google.gson;
    exports org.example.andrew_kozinski_assignment4;
}