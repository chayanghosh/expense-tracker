package com.example.expense_tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.expense_tracker.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	List<Expense> findByUserId(Long userId);

	List<Expense> findByUserIdAndCategory(Long userId, String category);

	List<Expense> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime sDate, LocalDateTime eDate);

	Page<Expense> findUserById(Long userId, Pageable pageable);
	

}
