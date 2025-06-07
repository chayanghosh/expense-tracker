package com.example.expense_tracker.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.expense_tracker.DTO.ExpenseDTO;
import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public String addExpense(ExpenseDTO expenseDTO, String username) {
        User loggedInUser = userRepo.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Expense expense = new Expense();
        expense.setName(expenseDTO.getName());
        expense.setCategory(expenseDTO.getCategory());
        expense.setAmount(expenseDTO.getAmount());
        expense.setUser(loggedInUser);
        expense.setCreatedAt(LocalDateTime.now());
        expenseRepo.save(expense);
        return "Expense added!";
    }

    @Override
    public String updateExpense(Long expenseId, ExpenseDTO expenseDTO, String username) {
        Expense expense = expenseRepo.findById(expenseId).get();
        if (!username.equals(expense.getUser().getEmail())) {
            throw new AccessDeniedException("Access Prohibited");
        }
        expense.setName(expenseDTO.getName());
        expense.setCategory(expenseDTO.getCategory());
        expense.setAmount(expenseDTO.getAmount());
        expenseRepo.save(expense);
        return "Updated";
    }


    @Override
    public List<Expense> getExpensesByDate(Long userId, String sDate, String eDate) {
        if (sDate != null && eDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDateTime startDate = LocalDate.parse(sDate, formatter).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(eDate, formatter).atStartOfDay();
            return expenseRepo.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
        }
        return expenseRepo.findByUserId(userId);
    }

    @Override
    public List<Expense> getExpenseByCategory(Long userId, String category) {
        return expenseRepo.findByUserIdAndCategory(userId, category);
    }

    @Override
    public Page<Expense> getSortedExpenses(Long userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return expenseRepo.findUserById(userId, pageable);
    }

    @Override
    public List<Expense> searchExpenses(Long userId, String keyword) {
        return expenseRepo.searchByKeyword(userId, keyword);
    }

    @Override
    public Double getTotalExpense(String username) {
        User user = userRepo.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return expenseRepo.getTotalExpenseByUser(user.getId());
    }

    @Override
    public String deleteExpense(Long expenseId) {
        if (!expenseRepo.existsById(expenseId)) {
            throw new ResourceNotFoundException("Please enter valid ID");
        }
        expenseRepo.deleteById(expenseId);
        return "Deleted successfully";
    }
}
