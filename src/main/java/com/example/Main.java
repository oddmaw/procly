package com.example;

import com.example.dao.*;
import com.example.database.DatabaseConnection;
import com.example.services.*;
import com.example.ui.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    private static final String DB_URL = "";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    // fields to track the root node and current theme
    private VBox root;
    private String currentTheme = "light-theme";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DatabaseConnection dbConnection = new DatabaseConnection(DB_URL, DB_USER, DB_PASSWORD);

        ClientDAO clientDAO = new ClientDAO(dbConnection);
        ProductDAO productDAO = new ProductDAO(dbConnection);
        InvoiceDAO invoiceDAO = new InvoiceDAO(dbConnection);
        CommandDAO commandDAO = new CommandDAO(dbConnection);

        ClientService clientService = new ClientService(clientDAO);
        ProductService productService = new ProductService(productDAO);
        InvoiceService invoiceService = new InvoiceService(invoiceDAO);
        CommandService commandService = new CommandService(commandDAO);

        // Theme switcher
        ToggleGroup themeGroup = new ToggleGroup();

        RadioButton lightTheme = new RadioButton("Light");
        lightTheme.setToggleGroup(themeGroup);
        lightTheme.setSelected(true);

        RadioButton darkTheme1 = new RadioButton("Dark Purple");
        darkTheme1.setToggleGroup(themeGroup);

        RadioButton darkTheme2 = new RadioButton("Dark Slate");
        darkTheme2.setToggleGroup(themeGroup);

        HBox themeControls = new HBox(10, new Label("Theme:"), lightTheme, darkTheme1, darkTheme2);
        themeControls.setPadding(new Insets(5, 10, 5, 10));

        TabPane tabPane = new TabPane();

        Tab productTab = new Tab("Product");
        ProductManagementUI.setProductService(productService);
        productTab.setContent(ProductManagementUI.getUI());
        tabPane.getTabs().add(productTab);

        Tab clientTab = new Tab("Client");
        ClientManagementUI.setClientService(clientService);
        clientTab.setContent(ClientManagementUI.getUI());
        tabPane.getTabs().add(clientTab);

        Tab invoiceTab = new Tab("Invoice");
        InvoiceManagementUI.setInvoiceService(invoiceService);
        InvoiceManagementUI.setClientService(clientService);
        InvoiceManagementUI.setProductService(productService);
        invoiceTab.setContent(InvoiceManagementUI.getUI(invoiceTab));
        tabPane.getTabs().add(invoiceTab);

        Tab commandTab = new Tab("Command");
        CommandManagementUI.setCommandService(commandService);
        CommandManagementUI.setClientService(clientService);
        CommandManagementUI.setProductService(productService);
        commandTab.setContent(CommandManagementUI.getUI(commandTab));
        tabPane.getTabs().add(commandTab);

        // Create root container
        root = new VBox(themeControls, tabPane);
        root.getStyleClass().add(currentTheme);

        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();

        double width = Math.max(1920, screenWidth);
        double height = Math.max(1080, screenHeight);

        Scene scene = new Scene(root, width, height);

        // Theme switching Setup
        lightTheme.setOnAction(e -> switchTheme("light-theme"));
        darkTheme1.setOnAction(e -> switchTheme("dark-theme-1"));
        darkTheme2.setOnAction(e -> switchTheme("dark-theme-2"));

        // Load CSS
        InputStream cssStream = getClass().getResourceAsStream("/style.css");
        if (cssStream != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }

        primaryStage.setTitle("Order Management System");
        primaryStage.setScene(scene);

        primaryStage.setFullScreen(true);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                primaryStage.setFullScreen(false);
                primaryStage.setWidth(width);
                primaryStage.setHeight(height);
            }
        });
        primaryStage.show();
    }

    private void switchTheme(String newTheme) {
        root.getStyleClass().remove(currentTheme);
        currentTheme = newTheme;
        root.getStyleClass().add(currentTheme);
    }
}