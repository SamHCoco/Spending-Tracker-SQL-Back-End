package sqlspending;


public class Main {

    public static void main(String[] args) {

        Datasource data = new Datasource();
//        data.createTable();
//        data.insertRecord(12.43, "FOOD");
//        data.insertRecord(5.45, "TRANSPORT");
//        data.insertRecord(56.60, "HOUSING");
//        data.insertRecord(25.56, "TRANSPORT");
//        data.querySpending();
        data.getMonthSpending();


    }
}
