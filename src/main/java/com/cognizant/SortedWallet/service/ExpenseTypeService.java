package com.cognizant.SortedWallet.service;


import com.cognizant.SortedWallet.exceptions.ExpenseTypeAlreadyExistsException;
import com.cognizant.SortedWallet.model.ExpenseType;
import com.cognizant.SortedWallet.model.User;
import com.cognizant.SortedWallet.repository.ExpenseTypeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ExpenseTypeService {
    private final ExpenseTypeRepository expenseTypeRepository;


    public ExpenseTypeService(ExpenseTypeRepository expenseTypeRepository, UserService userService) {
        this.expenseTypeRepository = expenseTypeRepository;
    }

    public ExpenseType findById(Long id) {
        return expenseTypeRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Saves an ExpenseType entity.
     *
     * This method saves the provided ExpenseType entity to the database after performing
     * a uniqueness check based on the expense category. If an ExpenseType with the same
     * expense category (case-insensitive) already exists, an exception is thrown.
     *
     * @param entity The ExpenseType entity to be saved.
     * @return The saved ExpenseType entity.
     * @throws ExpenseTypeAlreadyExistsException If an ExpenseType already exists in the database.
     */
    public ExpenseType saveIt(ExpenseType entity) throws ExpenseTypeAlreadyExistsException {
        if (expenseTypeRepository.existsByExpenseCategoryIgnoreCase(entity.getExpenseCategory())){
            throw new ExpenseTypeAlreadyExistsException("Expense type with name '" + entity.getExpenseCategory() + "' already exists.");
        }
        return expenseTypeRepository.save(entity);

    }


    /**
     * Initializes the application's default data.
     *
     * This method is automatically executed during application startup due to the
     * presence of the {@link PostConstruct} annotation. It checks if there are any
     * existing ExpenseType records in the database. If no records are found, it
     * creates and saves a default ExpenseType with the name "Home".
     */
    @PostConstruct
    public void init() {
        Iterable<ExpenseType> allExpenses = expenseTypeRepository.findAll();
        if (((Collection<?>) allExpenses).isEmpty()) {
            ExpenseType defaultExpenseType = new ExpenseType(null, "Home");
            expenseTypeRepository.save(defaultExpenseType);
        }
    }


    public Iterable<ExpenseType> findAll() {
        return expenseTypeRepository.findAll();
    }

    public void deleteById(Long id) {
        ExpenseType expenseTypeToBeDeleted = findById(id);
        expenseTypeRepository.delete(expenseTypeToBeDeleted);
    }

}
