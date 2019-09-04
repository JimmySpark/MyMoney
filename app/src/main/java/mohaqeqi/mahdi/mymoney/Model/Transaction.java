package mohaqeqi.mahdi.mymoney.Model;

public class Transaction {

    int id;
    String title;
    long amount;
    int day;
    int month;
    int year;
    String description;
    String transactionType;

    public Transaction(int id, String title, long amount, int day, int month, int year, String description, String transactionType) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.day = day;
        this.month = month;
        this.year = year;
        this.description = description;
        this.transactionType = transactionType;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getAmount() {
        return amount;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionType() {
        return transactionType;
    }
}
