package sqlspending;
import java.util.ArrayList;
import java.util.Calendar;

public class Spending {
    private String category;
    private String date;
    private double amount;
    private int id;

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

    // finds the current month
    public static String getCurrentMonth(){
        String month = findCurrentDate();
        return month.substring(3, 5);
    }

    // calculates the total spent so far in the current month
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
}