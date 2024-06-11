package org.example.andrew_kozinski_assignment4;

/**
 * Class which contains static methods to validate a movie's title, year and sales
 * @author Andrew Kozinski
 */
public class Validation {

    /**
     * Validates a movie's title.
     * Title cannot be empty, and can contain numbers, hyphens, colons, periods and spaces.
     * First letter must be capitalized.
     * @return Empty string if title is valid, otherwise return a string describing validation
     */
    public static String validateTitle(String inputTitle) {

        if(inputTitle.matches("")) {
            return "Title cannot be empty.";
        }
        else if(inputTitle.matches("[A-Z][A-Za-z\\d:\\s-.]*")) {
            return "";
        }
        else {
            return "Title can’t be empty and can contain letters, numbers, hyphens, colons, periods, and spaces. The first letter must be capitalized.";
        }

    }

    /**
     * Validates a movie's year.
     * Year cannot be empty and can only contain digits.
     * @return Empty string if the year is valid, otherwise return a string describing validation
     */
    public static String validateYear(String inputYear) {

        if(inputYear.matches("\\d{4}")) {
            return "";
        }

        if(inputYear.matches("")) {
            return "Year cannot be empty.";
        }

        return "Year must contain 4 digits.";

    }

    /**
     * Validates a movie's sales.
     * Sales cannot be empty and can only contain digits.
     * @return Empty string if sales is valid, otherwise return a string describing validation
     */
    public static String validateSales(String inputSales) {

        //If input was either just digits or digits with exactly 1 decimal period between 2 digits then valid input
        if(inputSales.matches("\\d+") || inputSales.matches("\\d*\\.{1}\\d+")) {
            return "";
        }
        else if(inputSales.isEmpty()) {
            return "Sales cannot be empty.";
        }
        else {
            return "Sales can only contain digits. The decimal point is optional. If the decimal point is included there must be at least one number before and at least one number after it.";
        }
    }

}
