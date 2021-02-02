package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DatabaseHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    DatabaseHandler databaseHandler;

    public PersistentTransactionDAO(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // code to add transaction logs
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        databaseHandler.logTransaction(date,accountNo,expenseType,amount);
    }

    // code to get all transaction logs
    @Override
    public List<Transaction> getAllTransactionLogs() {
        return databaseHandler.getAllTransactionLogs();
    }

    // code to get all paginated transaction logs
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        return databaseHandler.getPaginatedTransactionLogs(limit);
    }
}
