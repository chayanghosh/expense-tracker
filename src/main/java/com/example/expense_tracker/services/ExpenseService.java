package com.example.expense_tracker.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.expense_tracker.DTO.ExpenseDTO;
import com.example.expense_tracker.entity.Expense;

public interface ExpenseService {
    String addExpense(ExpenseDTO expenseDTO, String username);
    String updateExpense(Long expenseId, ExpenseDTO expenseDTO, String username);
    List<Expense> getExpensesByDate(Long userId, String sDate, String eDate);
    List<Expense> getExpenseByCategory(Long userId, String category);
    Page<Expense> getSortedExpenses(Long userId, int page, int size, String sortBy);
    List<Expense> searchExpenses(Long userId, String keyword);
    Double getTotalExpense(String username);
    String deleteExpense(Long expenseId);
}


