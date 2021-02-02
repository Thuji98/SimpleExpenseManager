package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

import android.content.Context;


public class PersistentExpenseManager extends ExpenseManager{
    private final DatabaseHandler databaseHandler;
    private Context context;
    public PersistentExpenseManager(Context applicationContext) {
        this.context =applicationContext;
        this.databaseHandler = new DatabaseHandler(context);
        setup();
    }

    @Override
    public void setup() {
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(databaseHandler);
        setAccountsDAO(persistentAccountDAO);

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(databaseHandler);
        setTransactionsDAO(persistentTransactionDAO);
    }
}
