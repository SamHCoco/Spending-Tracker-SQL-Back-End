package sqlspending;
import java.sql.*;
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

    //  Opens database or creates on if one does not exist
    public void openDatabase(){
        try{
            connection = DriverManager.getConnection(CONNECTION);
        } catch(SQLException e){
            System.out.println("ERROR OPENING DATABASE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // creates table in database if one does not exist
    public void createTable(){
        openDatabase();
        try(Statement statement = connection.createStatement()){
//            statement.execute("DROP TABLE " + TABLE_NAME); // FOR DEBUGGING
            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID_COLUMN +
                " INTEGER PRIMARY KEY, " + DATE_COLUMN + " TEXT, " + MONTH_WEEK_COLUMN + " INTEGER, " + AMOUNT_COLUMN
                    + " FLOAT, " + CATEGORY_COLUMN + " TEXT)");

        } catch(SQLException e){
            System.out.println("ERROR CREATING TABLE " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inserts a record (row) into the spending = table
    public void insertRecord(double amount, String category){
        String date = Spending.findCurrentDate();
        openDatabase();

        try(Statement statement = connection.createStatement()){
            statement.execute("INSERT INTO " + TABLE_NAME + "(" + DATE_COLUMN + ", " + MONTH_WEEK_COLUMN + ", "
                + AMOUNT_COLUMN + ", " + CATEGORY_COLUMN + ") VALUES('" + date + "', " + Spending.weekOfCurrentMonth()
                + ", " + amount + ", '" + category + "')");

        } catch(SQLException e){
            System.out.println("ERROR INSERTING RECORD: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Fetches records to display to user
    public ArrayList<Spending> querySpending(){
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
                System.out.println("DATE = " + spent.getDate() + ", AMOUNT (£) = " +spent.getAmount() +
                        ", CATEGORY = " + spent.getCategory() + ", WEEK = " + spent.getWeekOfMonth());
            }
            return spendings;
        } catch(SQLException e){
            System.out.println("ERROR QUERYING RECORDS" + e.getMessage());
            return null;
        }
    }

    public void getMonthSpending(){
        double monthTotal;
        monthTotal = Spending.calculateMonthTotal(querySpending());
        System.out.println("MONTH TOTAL = £" + monthTotal);
    }

    public void getWeekSpending(){
        double weekTotal;
        weekTotal = Spending.calculateWeekTotal(querySpending());
        System.out.println("WEEK TOTAL = £" + weekTotal);
    }

}
