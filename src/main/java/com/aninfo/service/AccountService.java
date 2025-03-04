package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - sum);
        accountRepository.save(account);

        Transaction transaction = new Transaction(sum, Transaction.TransactionType.WITHDRAW);
        transaction.setAccount(account);
        transactionService.createTransaction(transaction);

        account.addTransaction(transaction);

        return account;
    }

    @Transactional
    public Account deposit(Long cbu, Double sum) {
        final int MAX_EXTRA_AMOUNT = 500;
        final int MIN_DEPOSIT_AMOUNT = 2000;

        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }

        if (sum >= MIN_DEPOSIT_AMOUNT) {
            var extra = sum * 0.1;
            sum += extra <= MAX_EXTRA_AMOUNT ? extra : MAX_EXTRA_AMOUNT;
        }

        Account account = accountRepository.findAccountByCbu(cbu);
        account.setBalance(account.getBalance() + sum);
        accountRepository.save(account);

        Transaction transaction = new Transaction(sum, Transaction.TransactionType.DEPOSIT);
        transaction.setAccount(account);
        transactionService.createTransaction(transaction);

        account.addTransaction(transaction);

        return account;
    }

}
