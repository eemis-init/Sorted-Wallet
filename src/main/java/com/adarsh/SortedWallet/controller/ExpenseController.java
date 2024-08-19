package com.cognizant.SortedWallet.controller;

import com.cognizant.SortedWallet.exceptions.ExpenseTypeAlreadyExistsException;
import com.cognizant.SortedWallet.model.Expense;
import com.cognizant.SortedWallet.model.ExpenseType;
import com.cognizant.SortedWallet.model.User;
import com.cognizant.SortedWallet.service.ExpenseService;
import com.cognizant.SortedWallet.service.ExpenseTypeService;
import com.cognizant.SortedWallet.utils.Auth;
import com.cognizant.SortedWallet.utils.Helpers;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Month;

@Controller
@RequestMapping
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseTypeService expenseTypeService;
    private final Auth auth;
    private static final int PAGE_SIZE = 8; //number of records per page

    public ExpenseController(ExpenseService expenseService, ExpenseTypeService expenseTypeService, Auth auth) {
        this.expenseService = expenseService;
        this.expenseTypeService = expenseTypeService;
        this.auth = auth;
    }

    @ModelAttribute("totalAmount")
    public BigDecimal getTotalAmount(HttpSession session){
        User user = auth.retrieveAuthenticatedUser(session);
        Iterable<Expense> expenses = expenseService.findByUserId(user.getId());
        return expenseService.getTotalAmount(expenses);
    }

    @ModelAttribute("expenses")
    public Page<Expense> getExpensesByCurrentUser(@PageableDefault(size = PAGE_SIZE) Pageable page,Model model, HttpSession session){
        User currentUser = auth.retrieveAuthenticatedUser(session);
        String usersname=currentUser.getName();
        model.addAttribute("uname",usersname);
        if(currentUser!=null){
            return expenseService.findByUser(currentUser, page);
        }
        else{
            return Page.empty();
        }
    }

    @ModelAttribute
    public Expense getExpense(){
        return new Expense();
    }

    @GetMapping("/expenses")
    public String showExpenses(){
        return "/expenses";
    }

    @PostMapping("/AddExpense")
    public String addExpense(@Valid Expense expense, Errors errors, HttpSession session){
        User user = auth.retrieveAuthenticatedUser(session);
        if(user==null){
            return "redirect:/login";
        }
        if (errors.hasErrors()) {
            return "expenses"; //returns same page to keep data in form fields and to show errors
        }
        expense.setUser(user);
        expenseService.saveIt(expense);

        return "redirect:/expenses";
    }

    @ModelAttribute("expenseTypes")
    public Iterable<ExpenseType> getExpenseTypes(HttpSession session) {
        User user = auth.retrieveAuthenticatedUser(session);
        return expenseTypeService.findByUserId(user.getId());
    }

    @ModelAttribute
    public ExpenseType getExpenseType(){
        return new ExpenseType();
    }

    @GetMapping("/newExpenseType")
    public String showExpenseTypes(){
        return "/newExpenseType";
    }


    @PostMapping("/newExpenseType")
    public String addExpenseType(@Valid ExpenseType expenseType, Errors errors, Model model, HttpSession session) throws ExpenseTypeAlreadyExistsException {
        User user = auth.retrieveAuthenticatedUser(session);
        if(user==null){
            return "redirect:/login";
        }
        // Checking for validation errors
        if (errors.hasErrors()) {
            //returns same page to keep data in form fields and show errors
            return "newExpenseType";
        }
        try {
            expenseType.setUser(user);
            // Attempt to save the new expense type
            expenseTypeService.saveIt(expenseType,user);
        } catch (ExpenseTypeAlreadyExistsException e) {
            // Add error message to the model in case of existing expense type
            model.addAttribute("errorMessage", e.getMessage());
            return "newExpenseType";
        }
        // Redirect to the "newExpenseType" page after successful processing
        return "redirect:/newExpenseType";
    }


    @PostMapping(value = "expenses/delete/individual/{id}")
    public String deleteExpense(@PathVariable("id") Long id){
        expenseService.deleteById(id);
        return "redirect:/expenses";
    }

    @PostMapping(value = "newExpenseType/delete/{id}")
    public String deleteExpenseType(@PathVariable("id") Long id){
        expenseTypeService.deleteById(id);
        return "redirect:/newExpenseType";
    }


    @GetMapping("/update/{id}")
    public String showUpdateExpenseForm(@PathVariable String id, Model model) {
        Long longId = Long.parseLong(id);

        // Retrieve the expense object by ID and add it to the model
        Expense expense = expenseService.findById(longId);
        model.addAttribute("expense", expense);

        return "updateExpense";

    }

    @PostMapping("/update")
    public String updateExpense(@Valid Expense expense, Errors errors, HttpSession session) {
        if (errors.hasErrors()) {
            return "updateExpense";
        }
        User user = auth.retrieveAuthenticatedUser(session);
        if (user==null){
            return "redirect:/login";
        }
        expense.setUser(user);

        expenseService.saveIt(expense); // Save the updated expense object to the database
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/filter")
    public String showFilteredExpenses(@RequestParam(name = "year", required = false) Integer year,
                                       @RequestParam(name = "month", required = false) Month month,
                                       @RequestParam(name = "expenseTypeFilter", required = false) String expenseType,
                                       Model model, @PageableDefault(size = PAGE_SIZE) Pageable page, HttpSession session) {

        Page<Expense> expenses;
        String monthToDisplay = null;
        String yearToDisplay = null;
        User user = auth.retrieveAuthenticatedUser(session);

        // If all filters are provided (year, month, and expense type)
        if (year != null && month != null && expenseType != null && !expenseType.isEmpty()) {
            expenses = expenseService.getExpensesByUserYearMonthAndType(user, year, month, expenseType, page);
            monthToDisplay = Helpers.toSentenceCase(month.toString());
            yearToDisplay = year.toString();
        }
        // If only year and month filters are provided
        else if (year != null && month != null) {
            expenses = expenseService.getExpensesByUserYearMonth(user, year, month, page);
            monthToDisplay = Helpers.toSentenceCase(month.toString());
            yearToDisplay = year.toString();
        }
        // If only expense type filter is provided
        else if (expenseType != null && !expenseType.isEmpty()) {
            expenses = expenseService.getExpensesByUserType(user, expenseType, page);
        }
        // If no filters are provided, show all expenses
        else {
            expenses = expenseService.findByUser(user,page);
        }

        model.addAttribute("expenses", expenses);
        model.addAttribute("month", monthToDisplay);
        model.addAttribute("year", yearToDisplay);
        model.addAttribute("expenseType", expenseType);

        return "expenses";
    }


    @GetMapping("/downloadExpenses")
    public ResponseEntity<Resource> downloadExpenses(HttpSession session) {
        User user = auth.retrieveAuthenticatedUser(session);
        // Get all expenses from the database
        Iterable<Expense> expenses = expenseService.findByUserId(user.getId());

        // Convert expenses to a CSV format
        String csvData = expenseService.convertToCSV(expenses);

        // Set the CSV data as a ByteArrayResource
        ByteArrayResource resource = new ByteArrayResource(csvData.getBytes());

        // Return the CSV data as a downloadable file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=expenses.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

}
