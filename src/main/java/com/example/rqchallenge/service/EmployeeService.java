package com.example.rqchallenge.service;

import com.example.rqchallenge.pojo.Employee;
import java.io.IOException;
import java.util.List;

/**
 * Employee service Interface (Same methods as IEmployeeController so Implementation can be done)
 */
public interface EmployeeService {

  List<Employee> getAllEmployees() throws IOException;

  List<Employee> getEmployeesByNameSearch(String searchString);

  Employee getEmployeeById(String id);

  Integer getHighestSalaryOfEmployees();

  List<String> getTopTenHighestEarningEmployeeNames();

  Employee createEmployee(String name, String salary, String age);

  String deleteEmployee(String id);
}
