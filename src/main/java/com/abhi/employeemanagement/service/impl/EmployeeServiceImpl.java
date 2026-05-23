package com.abhi.employeemanagement.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abhi.employeemanagement.dto.EmployeeRequestDto;
import com.abhi.employeemanagement.dto.EmployeeResponseDto;
import com.abhi.employeemanagement.entity.Employee;
import com.abhi.employeemanagement.exception.ResourceNotFoundException;
import com.abhi.employeemanagement.repository.EmployeeRepository;
import com.abhi.employeemanagement.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	private EmployeeRepository employeeRepository;

	public EmployeeServiceImpl(EmployeeRepository employeeRepository) {

		this.employeeRepository = employeeRepository;
	}

	@CacheEvict(value = "employees", allEntries = true)
	@Override
	public EmployeeResponseDto addEmployee(EmployeeRequestDto requestDto) {

		logger.info("Adding employee");

		Employee employee = new Employee();

		employee.setName(requestDto.getName());
		employee.setEmail(requestDto.getEmail());
		employee.setDepartment(requestDto.getDepartment());
		employee.setSalary(requestDto.getSalary());
		employee.setJoiningDate(requestDto.getJoiningDate());

		Employee savedEmployee = employeeRepository.save(employee);

		return mapToResponse(savedEmployee);
	}

	@Cacheable(value = "employees")
	@Override
	public List<EmployeeResponseDto> getAllEmployees() {

		List<Employee> employees = employeeRepository.findAll();

		List<EmployeeResponseDto> responseList = new ArrayList<>();

		for (Employee employee : employees) {

			responseList.add(mapToResponse(employee));
		}

		return responseList;
	}

	@Override
	public EmployeeResponseDto getEmployeeById(Long id) {

		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

		return mapToResponse(employee);
	}

	@CacheEvict(value = "employees", allEntries = true)
	@Override
	public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto) {

		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

		employee.setName(requestDto.getName());
		employee.setEmail(requestDto.getEmail());
		employee.setDepartment(requestDto.getDepartment());
		employee.setSalary(requestDto.getSalary());
		employee.setJoiningDate(requestDto.getJoiningDate());

		Employee updatedEmployee = employeeRepository.save(employee);

		return mapToResponse(updatedEmployee);
	}

	@CacheEvict(value = "employees", allEntries = true)
	@Override
	public String deleteEmployee(Long id) {

		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

		employeeRepository.delete(employee);

		return "Employee Deleted Successfully";
	}

	private EmployeeResponseDto mapToResponse(Employee employee) {

		EmployeeResponseDto dto = new EmployeeResponseDto();

		dto.setId(employee.getId());
		dto.setName(employee.getName());
		dto.setEmail(employee.getEmail());
		dto.setDepartment(employee.getDepartment());
		dto.setSalary(employee.getSalary());
		dto.setJoiningDate(employee.getJoiningDate());

		return dto;
	}
}