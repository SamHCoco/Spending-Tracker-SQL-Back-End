package sqlspending;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Datasource {
    private Connection connection;

    public static final String DB_NAME = "spending_record.db";
    public static final String CONNECTION = "jdbc:sqlite:C:\\Users\\User_2\\Desktop\\Java Programs 2" +
                                                            "\\SQLSpending\\" + DB_NAME;
    public static final String TABLE_NAME = "spending";
    public static final String ID_COLUMN = "_id";
    public static final String DATE_COLUMN = "date";
    public static final String MONTH_WEEK_COLUMN = "month_week";
    public static final String AMOUNT_COLUMN = "amount";
    public static final String CATEGORY_COLUMN = "category";

    /**
     * Constructor for datasource. Creates spending table if one does not exist.
     */
    public Datasource(){
        createTable();
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
        String date = Spending.findCurrentDate();
        openDatabase();

        try(Statement statement = connection.createStatement()){
            statement.execute("INSERT INTO " + TABLE_NAME + "(" + DATE_COLUMN + ", " + MONTH_WEEK_COLUMN + ", "
                + AMOUNT_COLUMN + ", " + CATEGORY_COLUMN + ") VALUES('" + date + "', " + Spending.weekOfCurrentMonth()
                + ", " + amount + ", '" + category + "')");

        } catch(SQLException e){
            System.out.println("ERROR INSERTING RECORD: " + e.getMessage());
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
                    System.out.println("DATE = " + spent.getDate() + ", AMOUNT = £" + df.format(spent.getAmount()) +
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
        System.out.println("************************************************************************");
    }
}
