package com.example.expense_tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.expense_tracker.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	List<Expense> findByUserId(Long userId);

	List<Expense> findByUserIdAndCategory(Long userId, String category);

	List<Expense> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime sDate, LocalDateTime eDate);

	Page<Expense> findUserById(Long userId, Pageable pageable);
	
	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND " +
		       "(LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "OR LOWER(e.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Expense> searchByKeyword(Long userId, String keyword);
	

}
