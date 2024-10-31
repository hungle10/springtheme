package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long>{
	//tim kiem theo noi dung ten
	List<CategoryEntity> findByNameContaining(String name);
	
	//Tim kiem va phan trang
	Page<CategoryEntity> findByNameContaining(String name,Pageable pageable);

}
