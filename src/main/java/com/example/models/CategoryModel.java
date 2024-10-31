package com.example.models;

import java.io.Serializable;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	@Id
	private Long categoryId;
	@NotEmpty(message="khong duoc bo trong")
	private String categoryname;
	private String images;
	private int status;
	
	private Boolean isEdit=false;

}