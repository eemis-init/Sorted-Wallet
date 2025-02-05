package com.cognizant.SortedWallet.service;

import com.cognizant.SortedWallet.model.Expense;
import com.cognizant.SortedWallet.model.ExpenseType;
import com.cognizant.SortedWallet.model.User;
import com.cognizant.SortedWallet.repository.ExpenseRepository;
import com.cognizant.SortedWallet.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense saveIt(Expense entity) {
        return expenseRepository.save(entity);
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sorry, the content you are looking for does not exist."));
    }

    public Iterable<Expense> findByUserId(Long userId){
        return expenseRepository.findByUserId(userId);
    }


    public Page<Expense> findAll(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("creationDate").descending());

        return expenseRepository.findAll(sortedPageable);
    }

    public Page<Expense> findByUser(User user, Pageable pageable){
        return expenseRepository.findByUser(user,pageable);
    }


//    public Iterable<Expense> findAll() {
//        return expenseRepository.findAll();
//    }

    public void deleteById(Long id) {
        Expense expenseToBeDeleted = findById(id);
        expenseRepository.delete(expenseToBeDeleted);
    }

    public BigDecimal getTotalAmount(Iterable<Expense> expenses){
        return StreamSupport.
                stream(expenses.spliterator(), false)
                .toList()
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Page<Expense> getExpensesByUserYearMonthAndType(User user,int year, Month month, String expenseType, Pageable page) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return expenseRepository.findByUserAndDateBetweenAndExpenseTypeOrderByCreationDateDesc(user, startDate, endDate, expenseType, page);
    }

    public Page<Expense> getExpensesByUserYearMonth(User user, int year, Month month, Pageable page) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return expenseRepository.findByUserAndDateBetweenOrderByCreationDateDesc(user, startDate, endDate, page);
    }

    public Page<Expense> getExpensesByUserType(User user, String expenseType, Pageable page) {
        return expenseRepository.findByUserAndExpenseTypeOrderByCreationDateDesc(user, expenseType, page);
    }


    public String convertToCSV(Iterable<Expense> expenses) {
        StringBuilder expensesAsCSV = new StringBuilder();
        expensesAsCSV.append("Id,Name of Expense,Type of expense,Amount,Date,Creation Timestamp\n");

        for (Expense expense: expenses) {
            expensesAsCSV.append(expense.getId()).append(",")
                    .append(expense.getName()).append(",")
                    .append(expense.getExpenseType()).append(",")
                    .append(expense.getAmount()).append(",")
                    .append(expense.getDate()).append(",")
                    .append(expense.getCreationDate()).append("\n");
        }

        return expensesAsCSV.toString();
    }

}
