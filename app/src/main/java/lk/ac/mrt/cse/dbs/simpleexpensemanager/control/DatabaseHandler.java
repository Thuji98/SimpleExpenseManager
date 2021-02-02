package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "180482C";
    private static final String TABLE_ACCOUNTS = "accounts";
    private static final String TABLE_TRANSACTIONS = "transactions";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creating Tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS "+TABLE_ACCOUNTS+
                "(accountNo VARCHAR PRIMARY KEY, " +
                "bankName VARCHAR, " +
                "accountHolderName VARCHAR," +
                "balance REAL)";

        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS+
                "(Transaction_id INTEGER PRIMARY KEY," +
                "accountNo VARCHAR," +
                "expenseType INT," +
                "amount REAL," +
                "date DATE," +
                "FOREIGN KEY (accountNo) REFERENCES "+TABLE_ACCOUNTS+"(accountNo))";

        sqLiteDatabase.execSQL(CREATE_ACCOUNTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_TRANSACTIONS_TABLE);

    }

    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //Drop older tables if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);

        //Create tables again
        onCreate(sqLiteDatabase);
    }

    //Code to add the new account
    public void addAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("accountNo",account.getAccountNo());
        values.put("bankName",account.getBankName());
        values.put("accountHolderName",account.getAccountHolderName());
        values.put("balance",account.getBalance());

        database.insert(TABLE_ACCOUNTS,null,values);

        database.close();
    }

    //Code to get the single account
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database =this.getReadableDatabase();

        String account_query = "SELECT * FROM "+TABLE_ACCOUNTS+" where accountNo = "+accountNo;

        Cursor cursor = database.rawQuery(account_query, null);

        if (cursor != null)
            cursor.moveToFirst();

        Account account = new Account(
                cursor.getString(cursor.getColumnIndex("accountNo")),
                cursor.getString(cursor.getColumnIndex("bankName")),
                cursor.getString(cursor.getColumnIndex("accountHolderName")),
                cursor.getDouble(cursor.getColumnIndex("balance")));

        return account;

    }

    //Code to get all accounts in a list view
    public List<Account> getAccountsList() {
        List<Account> accountsList = new ArrayList<Account>();

        String accountListQuery = "SELECT * FROM "+TABLE_ACCOUNTS;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(accountListQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Account account=new Account(
                        cursor.getString(cursor.getColumnIndex("accountNo")),
                        cursor.getString(cursor.getColumnIndex("bankName")),
                        cursor.getString(cursor.getColumnIndex("accountHolderName")),
                        cursor.getDouble(cursor.getColumnIndex("balance")));
                accountsList.add(account);

            } while (cursor.moveToNext());
        }

        return accountsList;
    }

    //Code to get all account numbers in a list view
    public List<String> getAccountNumbersList() {
        List<String> accountNumbersList = new ArrayList<>();

        String accountNumberListQuery = "SELECT accountNo FROM "+TABLE_ACCOUNTS;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(accountNumberListQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String number=cursor.getString(cursor.getColumnIndex("accountNo"));
                accountNumbersList.add(number);
            } while (cursor.moveToNext());
        }

        return accountNumbersList;
    }

    //Code to remove an account
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_ACCOUNTS,"accountNo = ?",new String[] { accountNo});
        database.close();
    }

    //Code to update balance
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String updateBalanceQuery = "UPDATE "+TABLE_ACCOUNTS+" SET balance = balance + ?";
        SQLiteDatabase database = this.getWritableDatabase();
        SQLiteStatement statement = database.compileStatement(updateBalanceQuery);
        if(expenseType == ExpenseType.EXPENSE){
            statement.bindDouble(1,-amount);
        }else{
            statement.bindDouble(1,amount);
        }
        statement.executeUpdateDelete();
    }

    //Code to insert transaction logs
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String logTransactionQuery = "INSERT INTO "+TABLE_TRANSACTIONS+"(accountNo,expenseType,amount,date) VALUES (?,?,?,?)";
        SQLiteDatabase database = this.getWritableDatabase();
        SQLiteStatement statement = database.compileStatement(logTransactionQuery);
        statement.bindString(1,accountNo);
        statement.bindLong(2,(expenseType == ExpenseType.EXPENSE) ? 0 : 1);
        statement.bindDouble(3,amount);
        statement.bindLong(4,date.getTime());
        statement.executeInsert();
    }

    //Code to get all transaction logs
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactionLogs = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();

        String transactionLogsQuery = "SELECT * FROM "+TABLE_TRANSACTIONS;
        Cursor cursor = database.rawQuery(transactionLogsQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction trans=new Transaction(
                        new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                        cursor.getString(cursor.getColumnIndex("accountNo")),
                        (cursor.getInt(cursor.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        cursor.getDouble(cursor.getColumnIndex("amount")));
                transactionLogs.add(trans);
            } while (cursor.moveToNext());
        }
        return transactionLogs;
    }

    //Code to get all paginated transaction logs
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> paginatedTransactionLogs = new ArrayList<>();

        SQLiteDatabase database = this.getWritableDatabase();

        String paginatedTransactionLogsQuery = "SELECT * FROM "+TABLE_TRANSACTIONS+" LIMIT"+limit;
        Cursor cursor = database.rawQuery(paginatedTransactionLogsQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction trans=new Transaction(
                        new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                        cursor.getString(cursor.getColumnIndex("accountNo")),
                        (cursor.getInt(cursor.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        cursor.getDouble(cursor.getColumnIndex("amount")));
                paginatedTransactionLogs.add(trans);
            } while (cursor.moveToNext());
        }
        return  paginatedTransactionLogs;
    }
}
