package com.example.expense_tracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.expense_tracker.DTO.ExpenseDTO;
import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.security.MyUserDetails;
import com.example.expense_tracker.services.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/expense")
@Tag(name="Expense APIs", description = "CRUD operation for expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/post")
    @Operation(summary = "Add expense")
    public ResponseEntity<String> addExpense(@RequestBody ExpenseDTO dto, @AuthenticationPrincipal MyUserDetails userDetails) {
        String result = expenseService.addExpense(dto, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update/{expenseId}")
    @Operation(summary = "Update expense")
    public ResponseEntity<String> updateExpense(@PathVariable Long expenseId,
                                                @RequestBody ExpenseDTO dto,
                                                @AuthenticationPrincipal MyUserDetails userDetails) {
        String result = expenseService.updateExpense(expenseId, dto, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/filter")
    @Operation(summary = "Show the expenses of given time interval")
    public ResponseEntity<List<Expense>> getExpensesByDate(@AuthenticationPrincipal MyUserDetails userdetails,
                                                           @RequestParam(required = false) String sDate,
                                                           @RequestParam(required = false) String eDate) {
        return ResponseEntity.ok(expenseService.getExpensesByDate(userdetails.getUserId(), sDate, eDate));
    }

    @GetMapping("/user/{userId}/category/{category}")
    @Operation(summary = "Show the expenses of specific category")
    public ResponseEntity<List<Expense>> getExpenseByCategory(@PathVariable Long userId, @PathVariable String category) {
        return ResponseEntity.ok(expenseService.getExpenseByCategory(userId, category));
    }

    @GetMapping("/paged/{userId}")
    @Operation(summary = "Page view")
    public ResponseEntity<Page<Expense>> getSortedExpense(@PathVariable Long userId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size,
                                                          @RequestParam(defaultValue = "createdAt") String sortBy) {
        return ResponseEntity.ok(expenseService.getSortedExpenses(userId, page, size, sortBy));
    }

    @GetMapping("/search/{userId}")
    @Operation(summary = "Search")
    public ResponseEntity<List<Expense>> searchExpenses(@PathVariable Long userId,
                                                        @RequestParam String keyword) {
        return ResponseEntity.ok(expenseService.searchExpenses(userId, keyword));
    }

    @GetMapping("/summary/total")
    @Operation(summary = "Total expense of user")
    public ResponseEntity<String> getTotalExpense(@AuthenticationPrincipal MyUserDetails userDetails) {
        Double total = expenseService.getTotalExpense(userDetails.getUsername());
        return ResponseEntity.ok("Total Expense: " + total);
    }

    @DeleteMapping("/delete/{expenseId}")
    @Operation(summary = "Delete an expense")
    public ResponseEntity<String> deleteExpense(@PathVariable Long expenseId) {
        return ResponseEntity.ok(expenseService.deleteExpense(expenseId));
    }
}
