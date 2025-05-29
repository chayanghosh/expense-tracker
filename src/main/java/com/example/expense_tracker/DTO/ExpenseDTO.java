package com.example.expense_tracker.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDTO {
	private String name;
	private String category;
	private Double amount;
	private Date date;
}
