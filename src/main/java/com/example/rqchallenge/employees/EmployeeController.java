package com.example.rqchallenge.employees;

import com.example.rqchallenge.pojo.Employee;
import com.example.rqchallenge.service.EmployeeService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Employee Controller implementing IEmployeeController
 */
@Slf4j
@RestController
public class EmployeeController implements IEmployeeController{

  @Autowired
  EmployeeService employeeService;

  @Override
  public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
    return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
    return new ResponseEntity<>(employeeService.getEmployeesByNameSearch(searchString), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Employee> getEmployeeById(String id) {
    Employee employee = employeeService.getEmployeeById(id);
    if (Objects.isNull(employee)) {
      return ResponseEntity.notFound().build();
    }
    return new ResponseEntity<>(employee, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
    return new ResponseEntity<>(employeeService.getHighestSalaryOfEmployees(), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
    return new ResponseEntity<>(employeeService.getTopTenHighestEarningEmployeeNames(), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
    return new ResponseEntity<>(employeeService.createEmployee((String) employeeInput.get("name"),
      (String) employeeInput.get("salary"), (String) employeeInput.get("age")), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<String> deleteEmployeeById(String id) {
    return new ResponseEntity<>(employeeService.deleteEmployee(id), HttpStatus.OK);
  }
}
