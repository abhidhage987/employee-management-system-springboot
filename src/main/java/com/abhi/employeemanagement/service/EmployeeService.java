package com.abhi.employeemanagement.service;

import java.util.List;

import com.abhi.employeemanagement.dto.EmployeeRequestDto;
import com.abhi.employeemanagement.dto.EmployeeResponseDto;

public interface EmployeeService {

   
    EmployeeResponseDto addEmployee(EmployeeRequestDto requestDto);

    
    List<EmployeeResponseDto> getAllEmployees();

    
    EmployeeResponseDto getEmployeeById(Long id);

    
    EmployeeResponseDto updateEmployee(Long id,
                                       EmployeeRequestDto requestDto);

   
    String deleteEmployee(Long id);
}