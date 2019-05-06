# Spending-Tracker SQL Back End
The source code provides a basic command line application for tracking weekly and monthly spending for *6 categories* of spending:
* Food
* Housing
* Transport
* Leisure
* Clothing
* Misc.

It uses a relational database and SQL to insert and maintain spending records, from a spending table with 5 columns:
* *_id* - Integer
* *date* - String - 'dd-mm-yyyy' date format
* *month_week* - Integer - Week in month (1 - 5)
* *amount* - Double - Amount spent
* *category* - String - Category of spending

#### User Guide
To create a new database and spending table:
```Java
Datasource spending = new Datasource()
```
Spending records may be queried as follows:
```Java
// Set argument to 'true' to print all records, false otherwise
spending.queryRecords(true)
```
#### C.R.U.D. Operations

Records are inserted by specifying the amount spent and its category:
```Java
// insertRecord(amount, "category")
spending.insertRecord(7.56, "transport")
```

Spending records may be deleted by calling *deleteRecord()* and specifying record ID:
```Java
// deleteRecord(_id) deletes single record entry
spending.deleteRecord(4);
```

The *amount* spent or *category* of spending for a record entry may be updated by specifying the ID of the record to update (a positive integer) followed by the new category (a string) or amount (double):
```Java
// updateRecord(_id, "newCategory") Updates category of spending
spending.updateRecord(2, "housing");
// updateRecord(_id, newAmount) Updates category of spending
spending.updateRecord(2, 5.50);
```
