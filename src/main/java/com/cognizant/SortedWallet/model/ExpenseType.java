package com.cognizant.SortedWallet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseType {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty(message = "Please specify the type of expense")
    @Column(unique = true)
    private String expenseCategory;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public ExpenseType(Long id, String expenseCategory) {
        this.id=id;
        this.expenseCategory=expenseCategory;
    }
}
