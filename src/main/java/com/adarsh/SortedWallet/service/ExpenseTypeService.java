package com.cognizant.SortedWallet.service;


import com.cognizant.SortedWallet.exceptions.ExpenseTypeAlreadyExistsException;
import com.cognizant.SortedWallet.model.ExpenseType;
import com.cognizant.SortedWallet.model.User;
import com.cognizant.SortedWallet.repository.ExpenseTypeRepository;
import com.cognizant.SortedWallet.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExpenseTypeService {
    private final ExpenseTypeRepository expenseTypeRepository;

    private final UserRepository userRepository;


    @Autowired
    public ExpenseTypeService(ExpenseTypeRepository expenseTypeRepository, UserService userService, UserRepository userRepository) {
        this.expenseTypeRepository = expenseTypeRepository;
        this.userRepository = userRepository;
    }

    public ExpenseType findById(Long id) {
        return expenseTypeRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Iterable<ExpenseType> findByUserId(Long userId){
        return expenseTypeRepository.findByUserId(userId);
    }

    public Optional<User> findByUser(User user){
        return userRepository.findById(user.getId());
    }


    public ExpenseType saveIt(ExpenseType entity, User currentUser) throws ExpenseTypeAlreadyExistsException {
        Long userId = entity.getUser().getId();
        String expenseCategory = entity.getExpenseCategory();

        boolean exists = expenseTypeRepository.existsByUserAndExpenseCategoryIgnoreCase(entity.getUser(),expenseCategory);
        if (expenseTypeRepository.existsByUserAndExpenseCategoryIgnoreCase(currentUser,entity.getExpenseCategory())){
            throw new ExpenseTypeAlreadyExistsException("Expense type with name '" + entity.getExpenseCategory() + "' already exists .");
        }
        entity.setUser(currentUser);
        return expenseTypeRepository.save(entity);

    }


//    @PostConstruct
//    public void init() {
//        Iterable<ExpenseType> allExpenses = expenseTypeRepository.findAll();
//        if (((Collection<?>) allExpenses).isEmpty()) {
//            ExpenseType defaultExpenseType = new ExpenseType(null, "Home");
//            expenseTypeRepository.save(defaultExpenseType);
//        }
//    }


    public Iterable<ExpenseType> findAll() {
        return expenseTypeRepository.findAll();
    }

    public void deleteById(Long id) {
        ExpenseType expenseTypeToBeDeleted = findById(id);
        expenseTypeRepository.delete(expenseTypeToBeDeleted);
    }

}
