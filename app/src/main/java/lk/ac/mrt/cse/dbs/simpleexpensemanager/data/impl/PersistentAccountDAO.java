package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DatabaseHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    DatabaseHandler databaseHandler;

    public PersistentAccountDAO(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    // code to get all account numbers list
    @Override
    public List<String> getAccountNumbersList() {
        return databaseHandler.getAccountNumbersList();
    }

    // code to get all accounts list
    @Override
    public List<Account> getAccountsList() {
        return databaseHandler.getAccountsList();
    }

    // code to get a specific single account
    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return databaseHandler.getAccount(accountNo);
    }

    // code to add an account
    @Override
    public void addAccount(Account account) {
        databaseHandler.addAccount(account);
    }

    // code to remove an account
    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        databaseHandler.removeAccount(accountNo);
    }

    // code to update balance
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        databaseHandler.updateBalance(accountNo, expenseType, amount);
    }

}
