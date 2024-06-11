package org.example.andrew_kozinski_assignment4;

import com.google.gson.annotations.SerializedName;

/**
 * This class creates Movie objects which store title, year and sales information
 * @author Andrew Kozinski
 */
public class Movie {

    @SerializedName("title")
    private String title;
    @SerializedName("year")
    private String year;
    @SerializedName("sales")
    private double sales;

    /**
     * The default constructor for Movie, sets object variables to default
     */
    public Movie() {
        title = "Null";
        year = "Null";
        sales = 0;
    }

    /**
     * Parameterized constructor for Movie, takes in Strings for title and year and an int for sales
     * @param inputTitle The movie title
     * @param inputYear The movie year
     * @param inputSales The movie sales
     */
    public Movie(String inputTitle, String inputYear, double inputSales) {
        title = inputTitle;
        year = inputYear;
        sales = inputSales;
    }

    /**
     * Returns the movie's title
     * @return Movie title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the movie's title
     * @param inputTitle The inputted title
     */
    public void setTitle(String inputTitle) {
        title = inputTitle;
    }

    /**
     * Returns the movie's year
     * @return The movie's year
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the movie's year
     * @param inputYear The inputted year
     */
    public void setYear(String inputYear) {
        year = inputYear;
    }

    /**
     * Returns the movie's sales
     * @return Movie sales
     */
    public double getSales() {
        return sales;
    }

    /**
     * Sets the movie's sales
     * @param inputSales The inputted sales
     */
    public void setSales(double inputSales) {
        sales = inputSales;
    }
}
