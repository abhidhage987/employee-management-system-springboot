package com.abhi.employeemanagement.controller;


import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpHeaders;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.abhi.employeemanagement.dto.EmployeeRequestDto;
import com.abhi.employeemanagement.dto.EmployeeResponseDto;
import com.abhi.employeemanagement.repository.EmployeeRepository;
import com.abhi.employeemanagement.service.EmployeeService;
import com.abhi.employeemanagement.service.FileUploadService;
import com.abhi.employeemanagement.util.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	private EmployeeService employeeService;

	private EmployeeRepository employeeRepository;

	private FileUploadService fileUploadService;

	public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository,
			FileUploadService fileUploadService) {

		this.employeeService = employeeService;
		this.employeeRepository = employeeRepository;
		this.fileUploadService = fileUploadService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<EmployeeResponseDto>> addEmployee(
			@Valid @RequestBody EmployeeRequestDto requestDto) {

		EmployeeResponseDto response = employeeService.addEmployee(requestDto);

		ApiResponse<EmployeeResponseDto> apiResponse = new ApiResponse<>("Employee Added Successfully",
				HttpStatus.CREATED.value(), response);

		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> getAllEmployees() {

		List<EmployeeResponseDto> employees = employeeService.getAllEmployees();

		ApiResponse<List<EmployeeResponseDto>> response = new ApiResponse<>("Employees fetched successfully",
				HttpStatus.OK.value(), employees);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployeeById(@PathVariable Long id) {

		EmployeeResponseDto employee = employeeService.getEmployeeById(id);

		ApiResponse<EmployeeResponseDto> response = new ApiResponse<>("Employee fetched successfully",
				HttpStatus.OK.value(), employee);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateEmployee(@PathVariable Long id,

			@Valid @RequestBody EmployeeRequestDto requestDto) {

		EmployeeResponseDto employee = employeeService.updateEmployee(id, requestDto);

		ApiResponse<EmployeeResponseDto> response = new ApiResponse<>("Employee updated successfully",
				HttpStatus.OK.value(), employee);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable Long id) {

		String message = employeeService.deleteEmployee(id);

		ApiResponse<String> response = new ApiResponse<>(message, HttpStatus.OK.value(), message);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/pagination")
	public ResponseEntity<?> pagination(@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "5") int size,

			@RequestParam(defaultValue = "id") String sortBy) {

		return ResponseEntity.ok(employeeRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy))));
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchByDepartment(@RequestParam String department) {

		return ResponseEntity.ok(employeeRepository.findByDepartment(department));
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(

			@RequestParam("file") MultipartFile file) {

		String fileName = fileUploadService.uploadFile(file);

		return ResponseEntity.ok(

				"File uploaded successfully : " + fileName);
	}
	
	@GetMapping("/download/{fileName}")
	public ResponseEntity<Resource> downloadFile(

	        @PathVariable
	        String fileName) {

	    Path path = Paths.get(
	            "uploads/" + fileName);

	    Resource resource;

	    try {

	        resource = new UrlResource(
	                path.toUri());

	    } catch (Exception e) {

	        throw new RuntimeException(
	                "File not found");
	    }

	    return ResponseEntity.ok()

	            .header(

	                    HttpHeaders
	                            .CONTENT_DISPOSITION,

	                    "attachment; filename=\""
	                            + resource.getFilename()
	                            + "\""
	            )

	            .body(resource);
	}
}