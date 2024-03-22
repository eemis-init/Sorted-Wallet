package com.cognizant.SortedWallet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message="Name is required")
    @Column(unique=true)
    private String name;

    @NotEmpty(message="Email is required")
    @Email(message="Invalid Email")
    private String email;

    @NotEmpty(message="Password is required")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Expense> expenses;

    @OneToMany(mappedBy = "user")
    private List<ExpenseType> expenseTypes;
}
