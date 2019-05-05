package sqlspending;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Spending {
    private String category;  // category of spending (6 in total)
    private String date;  // date of spending
    private int weekOfMonth;  // week in month (1 - 5)
    private double amount;  // amount spent

    // setters
    public void setCategory(String category){
        this.category = category;
    }
    public void setAmount(double amount){
        this.amount = amount;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setWeekOfMonth(int weekOfMonth){ this.weekOfMonth = weekOfMonth; }

    // getters
    public String getCategory(){
        return category;
    }
    public double getAmount(){
        return amount;
    }
    public String getDate(){
        return date;
    }
    public int getWeekOfMonth(){return weekOfMonth; }

    /**
     * Determines the current date and returns result in 'dd-mm-yyyy' format.
     * @return The current data in 'dd-mm-yyyy' format.
     */
    public static String findCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        String date = String.valueOf(calendar.get(Calendar.DATE));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1); // + 1 Since Jan  = 0 in Calender class
        int year = calendar.get(Calendar.YEAR);
        // formats date and month strings to have ## format e.g Monday 1st = 01
        if(date.length() == 1){
            date = "0" + date;
        }
        if(month.length() == 1){
            month = "0" + month;
        }
        return(date + "-" + month + "-" + year);
    }

    /**
     * Finds the current month by slicing the date string, e.g 20-04-2018 returns '08'.
     * @return The month, in 'mm' format, from a date of format 'dd-mm-yyyy'.
     */
    public static String getCurrentMonth(){
        String month = findCurrentDate();
        return month.substring(3, 5);
    }

    /**
     * Determines the week of the current month (int: 1 - 5)
     * @return Week of the current month
     */
    public static int weekOfCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }


    /**
     * calculates the total spent so far in the current month
     * @param records ArrayList of all records in the spending table.
     * @return The total amount spent in the current month.
     */
    public static double calculateMonthTotal(ArrayList<Spending> records){
        String month;
        String currentMonth = getCurrentMonth();
        double monthSpendingTotal = 0;
        for(int i = 0; i < records.size(); i++){
            month = records.get(i).getDate();
            if(currentMonth.equals(month.substring(3, 5))){
                monthSpendingTotal += records.get(i).amount;
            }
        }
        return monthSpendingTotal;
    }

    /**
     * Calculates total spending for the current week.
     * @param spending ArrayList of spending objects, with each object being a single record entry from spending table.
     * @return The total amount spent in the current week.
     */
    public static double calculateWeekTotal(ArrayList<Spending> spending){
        String currentMonth = getCurrentMonth();
        String month;
        int weekOfCurrentMonth = Spending.weekOfCurrentMonth();
        int weekOfMonth;
        double weekTotal = 0;
        for(int i = 0; i < spending.size(); i++){
            month = spending.get(i).getDate();
            weekOfMonth = spending.get(i).getWeekOfMonth();
            if(currentMonth.equals(month.substring(3, 5)) && weekOfCurrentMonth == weekOfMonth){
                    weekTotal += spending.get(i).getAmount();
            }
        }
        return weekTotal;
    }

    /**
     * Calculates summary statistics for spending.
     * Category totals and category spending as % of total month spending are calculated.
     * @param spending ArrayList of spending objects, with each object being a single record entry from spending table.
     * @return Array of category spending as % of total month spending, for every category
     */
    public static double[] calculateStats(ArrayList<Spending> spending){
        double[] categories = new double[6];
        String currentMonth =  getCurrentMonth();
        double monthTotal = calculateMonthTotal(spending);
        for(int i = 0; i < spending.size(); i++){
            if(spending.get(i).getDate().substring(3,5).equals(currentMonth)){
                if(spending.get(i).getCategory().equals("FOOD")){
                    categories[0] += spending.get(i).getAmount();

                } else if(spending.get(i).getCategory().equals("TRANSPORT")){
                    categories[1] += spending.get(i).getAmount();

                } else if(spending.get(i).getCategory().equals("LEISURE")){
                    categories[2] += spending.get(i).getAmount();

                } else if(spending.get(i).getCategory().equals("CLOTHING")){
                    categories[3] += spending.get(i).getAmount();

                } else if(spending.get(i).getCategory().equals("HOUSING")){
                    categories[4] += spending.get(i).getAmount();

                } else if(spending.get(i).getCategory().equals("MISC.")){
                    categories[5] += spending.get(i).getAmount();
                }
            }
        }
        // calculates category spending as a % of total monthly spending
        for(int i = 0; i < 6; i++){
            categories[i] = (categories[i] / monthTotal) * 100;
        }
        return categories;
    }

    /**
     * Displays category spending as % of total month spending to user.
     * @param categories Array of total spending for each category.
     */
    public static void displayStats(double[] categories){
        DecimalFormat df = new DecimalFormat("#0");
        System.out.println("CATEGORY SPENDING SUMMARY STATISTICS");
        for(int i = 0; i < categories.length; i++){
            switch(i){
                case 0:
                    System.out.println("FOOD = " + df.format(categories[0]) + "%");
                    break;
                case 1:
                    System.out.println("TRANSPORT = " + df.format(categories[1]) + "%");
                    break;
                case 2:
                    System.out.println("LEISURE = " + df.format(categories[2]) + "%");
                    break;
                case 3:
                    System.out.println("CLOTHING = " + df.format(categories[3]) + "%");
                    break;
                case 4:
                    System.out.println("HOUSING = " + df.format(categories[4]) + "%");
                    break;
                case 5:
                    System.out.println("MISC. = " + df.format(categories[5]) + "%");
                    break;

            }
        }
    }
}
