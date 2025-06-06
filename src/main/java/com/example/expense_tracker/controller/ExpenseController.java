package com.example.expense_tracker.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
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
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.security.MyUserDetails;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
	
	@Autowired
	private ExpenseRepository expenseRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	
	@PostMapping("/post")
	public ResponseEntity<String> addExpense(@RequestBody ExpenseDTO expenseDTO, @AuthenticationPrincipal MyUserDetails userdetails){
		String username = userdetails.getUsername();
		User loggedInUser = userRepo.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		Expense expense = new Expense();
		expense.setName(expenseDTO.getName());
		expense.setCategory(expenseDTO.getCategory());
		expense.setAmount(expenseDTO.getAmount());
		expense.setUser(loggedInUser);
		expense.setCreatedAt(LocalDateTime.now());
		expenseRepo.save(expense);
		return ResponseEntity.ok("Expense added!");
	}
	
	@PutMapping("/update/{expenseId}")
	public ResponseEntity<?> updateExpense(@AuthenticationPrincipal MyUserDetails userDetails, @RequestBody ExpenseDTO expenseDto, @PathVariable Long expenseId){
		String username = userDetails.getUsername();
		Expense expense = expenseRepo.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Invalid ID"));
		
		if(username.equals(expense.getUser().getEmail())) {
			expense.setName(expenseDto.getName());
			expense.setCategory(expenseDto.getCategory());
			expense.setAmount(expenseDto.getAmount());
			expenseRepo.save(expense);
			return ResponseEntity.ok("Updated");
		}else {
			return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("access prohibited");
		}
	}
	
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Expense>> getExpensesByUser(@PathVariable Long userId) {
	    List<Expense> expenses = expenseRepo.findByUserId(userId);
	    return ResponseEntity.ok(expenses);
	}
	
	@GetMapping("/filter")
	public ResponseEntity<List<Expense>> getExpensesByDate(@RequestParam Long userId,
			@RequestParam(required = false) String sDate,
			@RequestParam(required = false) String eDate){
		List<Expense> expenses;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDateTime startDate = LocalDate.parse(sDate,formatter).atStartOfDay();
		LocalDateTime endDate = LocalDate.parse(eDate,formatter).atStartOfDay();
		if(sDate!=null && eDate!=null) {
			expenses = expenseRepo.findByUserIdAndCreatedAtBetween(userId,startDate,endDate);
			return ResponseEntity.ok(expenses);
		}
		return ResponseEntity.ok(expenseRepo.findByUserId(userId));
		
		//http://localhost:8081/expense/filter?userId=1&sDate=01-01-2025&eDate=31-12-2025
		
	}
	
	@GetMapping("/user/{userId}/category/{category}")
	public ResponseEntity<List<Expense>>  getExpenseByCategory(@PathVariable Long userId, @PathVariable String category){
		List<Expense> expenses = expenseRepo.findByUserIdAndCategory(userId,category);
		if(expenses.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(expenses);
	}
	
	@GetMapping("/paged/{userId}")
	public ResponseEntity<Page<Expense>> getSortedExpense(@PathVariable Long userId,
			@RequestParam(defaultValue= "0") int page,
			@RequestParam(defaultValue= "5") int size,
			@RequestParam(defaultValue= "createdAt") String sortBy){
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
		Page<Expense> expenses = expenseRepo.findUserById(userId,pageable);
		return ResponseEntity.ok(expenses);
		
		//http://localhost:8081/expense/paged/1?page=0&size=5&sortBy=createdAt
	}
	
	@GetMapping("/search/{userId}")
	public ResponseEntity<List<Expense>> searchExpenses(
	        @PathVariable Long userId,
	        @RequestParam String keyword) {
	    
	    List<Expense> results = expenseRepo.searchByKeyword(userId, keyword);
	    return ResponseEntity.ok(results);
	    
	    //http://localhost:8081/expense/search/1?keyword=Groceries
	}

	@GetMapping("/summary/total")
	public ResponseEntity<?> getTotalExpense(@AuthenticationPrincipal MyUserDetails userdetails){
		String username = userdetails.getUsername();
		User loggedInUser = userRepo.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		Double total = expenseRepo.getTotalExpenseByUser(loggedInUser.getId());
		return ResponseEntity.ok("Total Expense: "+total);
	}
	
	@DeleteMapping("/delete/{expenseId}")
	public ResponseEntity<String> deleteExpense(@PathVariable Long expenseId){
		if(!expenseRepo.findById(expenseId).isPresent()) {
			throw new ResourceNotFoundException("Please enter valid ID");
			//return ResponseEntity.notFound().build();
		}
		expenseRepo.deleteById(expenseId);
		return ResponseEntity.ok("Deleted succesfully");
	}
	
}

//@GetMapping("/{id}")
//public Expense getExpense(@PathVariable Long id) {
//    return expenseRepository.findById(id)
//        .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id " + id));
//}

