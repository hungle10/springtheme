package com.example.controllers.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.*;

import com.example.entity.CategoryEntity;
import com.example.models.CategoryModel;
import com.example.service.ICategoryService;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
	@Autowired
	ICategoryService categoryService;

	@GetMapping("/add")
	public String add(ModelMap model) {
		CategoryModel cateModel = new CategoryModel();
		cateModel.setIsEdit(false);
		// chuyen du lieu tu model vao bien category de dua len view
		model.addAttribute("category", cateModel);
		return "admin/categories/addOrEdit";
	}

	@PostMapping("/saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("category") CategoryModel cateModel,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/categories/addOrEdit");
		}

		CategoryEntity entity = new CategoryEntity();
		// Copy từ Model sang Entity
		BeanUtils.copyProperties(cateModel, entity);

		// Gọi hàm save trong service
		categoryService.save(entity);

		// Đưa thông báo về cho biến message
		String message = "";
		if (cateModel.getIsEdit() == true) {
			message = "Category is Edited!!!!!";
		} else {
			message = "Category is saved!!!!!";
		}

		model.addAttribute("message", message);

		// Redirect về URL controller
		return new ModelAndView("forward:/admin/categories/searchpaginated", model);
	}

	@GetMapping("")
	public String findAll(ModelMap model) {
		// gọi hàm findAll() trong service
		List<CategoryEntity> list = categoryService.findAll();
		// chuyển dữ liệu từ list lên biến categories
		model.addAttribute("categories", list);
		return "views/admin/list";
	}

	@GetMapping("/edit/{categoryId}")
	public ModelAndView edit(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		System.out.println("Hung code");
	    Optional<CategoryEntity> optCategory = categoryService.findById(categoryId);
	    CategoryModel cateModel = new CategoryModel();
	    // kiểm tra sự tồn tại của category
	    if (optCategory.isPresent()) {
	        CategoryEntity entity = optCategory.get();
	        // copy từ entity sang cateModel
	        BeanUtils.copyProperties(entity, cateModel);
	        cateModel.setIsEdit(true);
	        // đẩy dữ liệu ra view
	        model.addAttribute("category", cateModel);
	        return new ModelAndView("views/admin/AddOrEdit", model);
	    }
	    model.addAttribute("message", "Category is not existed!!!!");
	    return new ModelAndView("forward:/admin/categories", model);
	}
	@GetMapping("delete/{categoryId}")
	public ModelAndView delete(ModelMap model, @PathVariable("categoryId") Long categoryId) {
	    categoryService.deleteById(categoryId);
	    model.addAttribute("message", "Category is deleted!!!!");
	    return new ModelAndView("forward:/admin/categories/searchpaginated",  model);
	}

	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
	    List<CategoryEntity> list = null;
	    if (StringUtils.hasText(name)) {
	        list = categoryService.findByNameContaining(name);
	    } else {
	        list = categoryService.findAll();
	    }
	    model.addAttribute("categories", list);
	    return "admin/categories/search";
	}
	@RequestMapping("/searchpaginated")
	public String search(ModelMap model,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		//modelmap để gửi dữ liệu từ controller sang view
		int count = (int) categoryService.count();
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(3);
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("name"));
		Page<CategoryEntity> resultPage = null;
		if (StringUtils.hasText(name)) {
			resultPage = categoryService.findByNameContaining(name, pageable);
			model.addAttribute("name", name);
		} else {
			resultPage = (Page<CategoryEntity>) categoryService.findAll(pageable);
		}
		int totalPages = resultPage.getTotalPages();
		if (totalPages > 0) {
			int start = Math.max(1, currentPage - 2);
			int end = Math.min(currentPage + 2, totalPages);
			if (totalPages > count) {
				if (end == totalPages)
					start = end - count;
				else if (start == 1)
					end = start + count;
			}
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		model.addAttribute("categoryPage", resultPage);
		return "views/admin/category-list";
	}

}
