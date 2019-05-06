package sqlspending;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class Datasource {
    private Connection connection;
    private HashSet<String> categories;
    public static final String DB_NAME = "spending_record.db";
    public static final String CONNECTION = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\" + DB_NAME;
    public static final String TABLE_NAME = "spending";
    public static final String ID_COLUMN = "_id";
    public static final String DATE_COLUMN = "date";
    public static final String MONTH_WEEK_COLUMN = "month_week";
    public static final String AMOUNT_COLUMN = "amount";
    public static final String CATEGORY_COLUMN = "category";

    /**
     * Constructor for datasource.
     * Creates spending table if one does not exist and defines spending categories
     */
    public Datasource(){
        createTable();
        categories = new HashSet<>();
        categories.add("FOOD");
        categories.add("TRANSPORT");
        categories.add("LEISURE");
        categories.add("CLOTHING");
        categories.add("HOUSING");
        categories.add("MISC.");
    }

    /**
     * Opens database or creates on if one does not exist.
     */
    public void openDatabase(){
        try{
            connection = DriverManager.getConnection(CONNECTION);
        } catch(SQLException e){
            System.out.println("ERROR OPENING DATABASE: " + e.getMessage());
        }
    }

    /**
     * creates spending table in database if one does not exist.
     */
    public void createTable(){
        openDatabase();
        try(Statement statement = connection.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID_COLUMN +
                " INTEGER PRIMARY KEY, " + DATE_COLUMN + " TEXT, " + MONTH_WEEK_COLUMN + " INTEGER, " + AMOUNT_COLUMN
                    + " FLOAT, " + CATEGORY_COLUMN + " TEXT)");

        } catch(SQLException e){
            System.out.println("ERROR CREATING TABLE " + e.getMessage());
        }
    }

    /**
     * Inserts a single record (row) into the spending table.
     * The column values entered are the date, week of the month, amount and category.
     * @param amount The amount spent.
     * @param category The category of the spending.
     */
    public void insertRecord(double amount, String category){
        category = category.toUpperCase();
        if(amount > 0 && categories.contains(category)){
            String date = Spending.findCurrentDate();
            openDatabase();
            try(Statement statement = connection.createStatement()){
                statement.execute("INSERT INTO " + TABLE_NAME + "(" + DATE_COLUMN + ", " + MONTH_WEEK_COLUMN + ", "
                        + AMOUNT_COLUMN + ", " + CATEGORY_COLUMN + ") VALUES('" + date + "', " + Spending.weekOfCurrentMonth()
                        + ", " + amount + ", '" + category + "')");

            } catch(SQLException e){
                System.out.println("ERROR INSERTING RECORD: " + e.getMessage());
            }
        } else {
            System.out.println("insertRecord ERROR: invalid input");
        }
    }

    /**
     * Deletes a single record in the database spending table, specified by the _id parameter
     * @param _id The ID of the record to be deleted.
     */
    public void deleteRecord(int _id){
        if(_id > 0){
            openDatabase();
            try(Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + "=" + _id);
            } catch (SQLException e){
                System.out.println("deleteRecord ERROR: " + e.getMessage());
            }
            querySpending(true);
        } else {
            System.out.println("deleteRecord ERROR: invalid input");
        }
    }

    /**
     * Updates spending amount for a record entry by ID.
     * @param _id The ID of the record entry to be updated.
     * @param amount The new spending amount.
     */
    public void updateRecord(int _id,double amount){
        if(amount > 0 && _id > 0){
            openDatabase();
            try(Statement statement = connection.createStatement()) {
                statement.execute("UPDATE " + TABLE_NAME +
                                       " SET " + AMOUNT_COLUMN + "=" + amount +
                                       " WHERE " + ID_COLUMN + "=" + _id);
                querySpending(true);
            } catch (SQLException e){
                System.out.println("updateRecord ERROR: " + e.getMessage());
            }
        } else {
            System.out.println("updateRecord ERROR: invalid input");
        }
    }

    /**
     * Updates category for a record entry by ID.
     * @param _id The ID of the record entry to be updated.
     * @param category The new category.
     */
    public void updateRecord(int _id, String category){
        category = category.toUpperCase();
        if(_id > 0 && categories.contains(category)){
            openDatabase();
            try(Statement statement = connection.createStatement()) {
                statement.execute("UPDATE " + TABLE_NAME +
                                       " SET " + CATEGORY_COLUMN + "= " + "'" + category + "'" +
                                       " WHERE " + ID_COLUMN + "=" + _id);
                querySpending(true);
            } catch (SQLException e){
                System.out.println("updateRecord ERROR: " + e.getMessage());
            }
        } else {
            System.out.println("updateRecord ERROR: Invalid input");
        }
    }

    /**
     * Fetches all records in the spending table and returns them.
     * @param printQuery Boolean parameter that determines whether the record entries are printed or not.
     * @return An ArrayList of Spending objects, each of which correspond to a single record in spending table.
     */
    public ArrayList<Spending> querySpending(boolean printQuery){
        DecimalFormat df = new DecimalFormat("0.00");
        ArrayList<Spending> spendings = new ArrayList<>();
        openDatabase();
        try(Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM " + TABLE_NAME)){

            while(result.next()){
                Spending spent = new Spending();
                spent.setDate(result.getString(DATE_COLUMN));
                spent.setAmount(result.getDouble(AMOUNT_COLUMN));
                spent.setCategory(result.getString(CATEGORY_COLUMN));
                spent.setWeekOfMonth(result.getInt(MONTH_WEEK_COLUMN));
                spendings.add(spent);
                if(printQuery){
                    System.out.println( "ID " + result.getInt(ID_COLUMN) +
                                        "| DATE = " + spent.getDate() +
                                        ", AMOUNT = £" + df.format(spent.getAmount()) +
                                        ", CATEGORY = " + spent.getCategory());
                }
            }
            System.out.println("************************************************************************");
            return spendings;
        } catch(SQLException e){
            System.out.println("ERROR QUERYING RECORDS" + e.getMessage());
            return null;
        }
    }

    /**
     * Prints and returns how much was spent in the current month.
     * @return How much was spent in the current month.
     */
    public double getMonthSpending(){
        double monthTotal;
        monthTotal = Spending.calculateMonthTotal(querySpending(false));
        System.out.println("MONTH TOTAL = £" + monthTotal);
        System.out.println("************************************************************************");
        return monthTotal;
    }

    /**
     * Prints and returns how much was spent in the current week.
     * @return How much was spent in the current week.
     */
    public double getWeekSpending(){
        double weekTotal;
        weekTotal = Spending.calculateWeekTotal(querySpending(false));
        System.out.println("WEEK TOTAL = £" + weekTotal);
        System.out.println("************************************************************************");
        return weekTotal;
    }

    /**
     * Displays category spending as % of total month spending to user.
     */
    public void categorySpending(){
        double[] categoryPercentages = Spending.calculateStats(querySpending(false));
        Spending.displayStats(categoryPercentages);
        System.out.println("************************************************************************");
    }
}
