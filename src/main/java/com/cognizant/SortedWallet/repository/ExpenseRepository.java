package com.cognizant.SortedWallet.repository;

import com.cognizant.SortedWallet.model.Expense;
import com.cognizant.SortedWallet.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Iterable<Expense> findByUserId(Long userId);

    Page<Expense> findByUser(User user, Pageable pageable);

    Page<Expense> findByDateBetweenOrderByCreationDateDesc(LocalDate startDate, LocalDate endDate, Pageable page);


    Page<Expense> findByExpenseTypeOrderByCreationDateDesc(String expenseType, Pageable page);


    Page<Expense> findByDateBetweenAndExpenseTypeOrderByCreationDateDesc(LocalDate startDate, LocalDate endDate, String expenseType, Pageable page);
}

