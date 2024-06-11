package org.example.andrew_kozinski_assignment4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.andrew_kozinski_assignment4.Validation.*;

/**
 * Controller of the program, GUI event handlers are defined within this class.
 * @author Andrew Kozinski
 */
public class HelloController {

    //Label where status of program is shown
    @FXML
    private Label statusText;

    //TableView where movie data is shown
    @FXML
    private TableView<Movie> moviesTable;

    //Column where the movie's title is displayed in the TableView
    @FXML
    private TableColumn<Movie,String> columnTitle;

    //Column where the movie's year is displayed in the TableView
    @FXML
    private TableColumn<Movie,String> columnYear;

    //Column where hte movie's sales is displayed in the TableView
    @FXML
    private TableColumn<Movie,Double> columnSales;

    //TextField where movie title is inputted by the user
    @FXML
    private TextField titleInput;

    //TextField where movie year is inputted by the user
    @FXML
    private TextField yearInput;

    //TextField where movie sales is inputted by the user
    @FXML
    private TextField salesInput;

    //Used in deletion method
    //selectedItem is the currently selected item from the TableView
    //Updated when the user selects an item in the table view
    private Movie selectedItem;

    /**
     * Method will run after program is started.
     * This method will associate each column in the TableView with a variable in the Movie class.
     */
    public void initialize() {
        //Each column will be associated with a variable on the Movie class
        columnTitle.setCellValueFactory(new PropertyValueFactory<Movie,String>("title"));
        columnYear.setCellValueFactory(new PropertyValueFactory<Movie,String>("year"));
        columnSales.setCellValueFactory(new PropertyValueFactory<Movie,Double>("sales"));
    }

    /**
     * When called the program will close
     */
    public void handleExitProgram() {
        Platform.exit();
    }

    /**
     * When called will create a table if the database doesn't exist.
     * If the database exists, database will be dropped and a new database will be created.
     */
    public void handleCreateTableButton() {

        String dbFilePath = ".//MovieDB.accdb";
        String databaseURL = "jdbc:ucanaccess://" + dbFilePath;

        File dbFile = new File(dbFilePath);

        //If the DB file exists, drop the table and then a new one will be created
        if(dbFile.exists()) {
            try {
                Connection conn = DriverManager.getConnection(databaseURL);
                String sql;
                sql = "DROP TABLE Movies";
                Statement createTableStatement = conn.createStatement();
                createTableStatement.execute(sql);
                conn.commit();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        //Create a DB file if it does not exist
        else {
            try (Database db = DatabaseBuilder.create(Database.FileFormat.V2010, new File(dbFilePath))) {
                System.out.println("The database file has been created.");
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        }

        //Create movies table
        try {
            Connection conn = DriverManager.getConnection(databaseURL);
            String sql;
            sql = "CREATE TABLE Movies (Title nvarchar(255), Year nvarchar(255), Sales DOUBLE)";
            Statement createTableStatement = conn.createStatement();
            createTableStatement.execute(sql);
            conn.commit();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        statusText.setText("New table created");

    }

    /**
     * When called, will connect to the MovieDB file and display all the movies from DB in the TableView
     */
    public void handleListRecordsButton() {

        //Check if a DB file exists, if it doesn't one will be created
        String dbFilePath = ".//MovieDB.accdb";
        String databaseURL = "jdbc:ucanaccess://" + dbFilePath;

        File dbFile = new File(dbFilePath);

        //Will just call the same method used when clicking the create table menu item
        if(!dbFile.exists()) {
            handleCreateTableButton();
        }


        //JDBC created
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            Logger.getLogger(HelloApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        //ObservableList will be used to add items into the TableView
        ObservableList<Movie> items = moviesTable.getItems();

        //Clear TableView first
        items.clear();

        //Query the DB and display entries in the TableView
        try {
            String tableName = "Movies";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("select * from " + tableName);
            while (result.next()) {
                String title = result.getString("Title");
                String year = result.getString("Year");
                double sales = result.getDouble("Sales");

                //New movie created
                Movie insert = new Movie(title, year, sales);
                //Insert movie
                items.add(insert);

            }

            //Update status
            statusText.setText("Movie table displayed");

        } catch (SQLException except) {
            except.printStackTrace();
        }


    }

    /**
     * Helper method to insert a new movie into the DB, takes in a movie object and inserts it into DB
     * @param m Movie to be inserted into DB
     */
    private void handleInsertIntoDB(Movie m) {
        String dbFilePath = ".//MovieDB.accdb";
        String databaseURL = "jdbc:ucanaccess://" + dbFilePath;

        File dbFile = new File(dbFilePath);

        //Will just call the same method used when clicking the create table menu item to create a new table
        if(!dbFile.exists()) {
            handleCreateTableButton();
        }

        //JDBC created
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            Logger.getLogger(HelloApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sql = "INSERT INTO Movies (Title, Year, Sales) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, m.getTitle());
            preparedStatement.setString(2, m.getYear());
            preparedStatement.setDouble(3, m.getSales());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read in inputs from the user via TextFields, validates the inputs and if validation is successful add a new movie to the DB
     */
    public void handleAddButton() {

        //Insert values into DB

        //First validate the inputs, then allow an insertion
        String inTitle = titleInput.getText();
        String inYear = yearInput.getText();
        String inSales = salesInput.getText();

        String titleValidation = validateTitle(inTitle);
        String yearValidation = validateYear(inYear);
        String salesValidation = validateSales(inSales);

        if(titleValidation.equals("") && yearValidation.equals("") && salesValidation.equals("")) {
            //Validation successful, so we now insert

            //Create a new movie and call insert helper method
            Movie insert = new Movie(inTitle, inYear, Double.parseDouble(inSales));
            handleInsertIntoDB(insert);

            //Clear textFields
            titleInput.clear();
            yearInput.clear();
            salesInput.clear();

            //Update tableview
            handleListRecordsButton();

            //Update status
            statusText.setText("A movie has been inserted: " + inTitle);

        }
        else {
            //Either title, year or sales is invalid, show error message to user and don't insert.
            String errorText = "";
            if(!titleValidation.equals("")) {
                errorText += titleValidation + "\n";
            }
            if(!yearValidation.equals("")) {
                errorText += yearValidation + "\n";
            }
            if(!salesValidation.equals("")) {
                errorText += salesValidation + "\n";
            }

            Alert error = new Alert(Alert.AlertType.WARNING);
            error.setContentText(errorText);
            error.show();

        }

    }

    /**
     * Opens a FileChooser for the user to choose a JSON to read from and imports data into the DB
     */
    public void handleImportJson() {

        //Open a file chooser into the current directory
        FileChooser fileChooser = new FileChooser();
        File current = null;
        try {
            current = new File(new File(".").getCanonicalPath());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //Sets the FileChooser to filter on JSON files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(current);
        File selectedFile = fileChooser.showOpenDialog(null);

        //If selectedFile is not null a file was selected
        if (selectedFile != null) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            FileReader fr = null;
            try {

                //Drops the table and recreates it
                //Effectively clears the table
                handleCreateTableButton();

                fr = new FileReader(selectedFile);

                Movie[] movies = gson.fromJson(fr, Movie[].class);

                for (Movie movie : movies) {
                    //Helper method to insert into DB
                    handleInsertIntoDB(movie);
                }

                //Update TableView
                handleListRecordsButton();
                statusText.setText("Import data from " + selectedFile);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        }

    }

    /**
     * Opens a FileChooser for the user to export the movie database into a JSON file
     */
    public void handleExportJson() {

        //Following code will open a save file dialogue for the user
        FileChooser fileChooser = new FileChooser();
        File current = null;

        try {
            current = new File(new File(".").getCanonicalPath());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //Filter files to JSON
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialDirectory(current);
        File selectedFile = fileChooser.showSaveDialog(null);

        if(selectedFile != null) {

            String dbFilePath = ".//MovieDB.accdb";
            String databaseURL = "jdbc:ucanaccess://" + dbFilePath;

            File dbFile = new File(dbFilePath);

            //If dbFile does not exist, one will be created
            //This will just result in the exported json file having no entries
            if(!dbFile.exists()) {
                handleCreateTableButton();
            }

            //Create a JDBC connection
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(databaseURL);
            } catch (SQLException ex) {
                Logger.getLogger(HelloApplication.class.getName()).log(Level.SEVERE, null, ex);
            }

            //ArrayList where movies from the DB will be stored
            ArrayList<Movie> moviesList = new ArrayList<>();

            //Query the database
            try {
                String tableName = "Movies";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery("select * from " + tableName);

                //For each movie in the DB, make a new movie object and add it to the arraylist
                while (result.next()) {
                    String getTitle = result.getString("Title");
                    String getYear = result.getString("Year");
                    double getSales = result.getDouble("Sales");
                    moviesList.add(new Movie(getTitle, getYear, getSales));
                }


                //Print to JSON file
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();
                String jsonString = gson.toJson(moviesList);

                PrintStream ps = null;
                try {
                    ps = new PrintStream(selectedFile);
                    ps.println(jsonString);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                statusText.setText("Exported to " + selectedFile);

            } catch (SQLException except) {
                except.printStackTrace();
            }

        }

    }

    /**
     * Displays a dialog with the integrity statement to the user
     */
    public void handleAboutButton() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Movie Database");
        alert.setHeaderText("Name and Integrity Statement");
        alert.setContentText("Andrew Kozinski\n\nI certify that this submission is my original work");
        alert.show();

    }

    /**
     * Handles the user selecting an item in the TableView
     * Updates the selectedItem variable
     * @param event
     */
    public void handleTableViewItemsMouseClick(MouseEvent event) {
        ObservableList<Movie> observableList = moviesTable.getItems();
        int selectedIndex = moviesTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < observableList.size()) {
            selectedItem = observableList.get(selectedIndex);
        }
    }

    /**
     * When called, deletes the currently selected movie from the DB
     * If a user has not selected an item, alert will be displayed and no deletion will occur
     */
    public void handleDeleteButton() {
        //No item has been selected, so nothing will be deleted
        if(selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("No item selected, so no deletion has been performed");
            alert.show();
        }
        else {
            //selectedItem is not null, so a deletion can be performed

            //Create a JDBC
            String databaseURL = "";
            Connection conn = null;
            try {
                databaseURL = "jdbc:ucanaccess://.//MovieDB.accdb";
                conn = DriverManager.getConnection(databaseURL);
            } catch (SQLException ex) {
                Logger.getLogger(HelloApplication.class.getName()).log(Level.SEVERE, null, ex);
            }

            String movieName = selectedItem.getTitle();
            String movieYear = selectedItem.getYear();
            double movieSales = selectedItem.getSales();

            //Deletion performed below
            String sql = "DELETE FROM Movies WHERE title=? AND year=? AND sales=?";
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, movieName);
                preparedStatement.setString(2, movieYear);
                preparedStatement.setDouble(3, movieSales);
                int rowsDeleted = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            //Updates tableView
            handleListRecordsButton();

            //Status updated
            statusText.setText("A movie has been deleted: " + movieName);
        }
    }

}