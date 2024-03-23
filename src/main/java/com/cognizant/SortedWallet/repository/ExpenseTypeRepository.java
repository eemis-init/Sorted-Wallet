package com.cognizant.SortedWallet.repository;

import com.cognizant.SortedWallet.model.ExpenseType;
import com.cognizant.SortedWallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {

    Iterable<ExpenseType> findByUserId(Long userId);

    boolean existsByUserAndExpenseCategoryIgnoreCase(User user, String expenseCategory);
    @Query("SELECT COUNT(et) > 0 FROM ExpenseType et WHERE LOWER(et.expenseCategory) = LOWER(:expenseCategory)")
    boolean existsByExpenseCategoryIgnoreCase(String expenseCategory);
}
