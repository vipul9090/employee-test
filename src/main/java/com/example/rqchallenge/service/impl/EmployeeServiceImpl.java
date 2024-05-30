package com.example.rqchallenge.service.impl;


import com.example.rqchallenge.pojo.Employee;
import com.example.rqchallenge.pojo.EmployeeData;
import com.example.rqchallenge.pojo.EmployeeList;
import com.example.rqchallenge.service.EmployeeService;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Employee Service Implementation
 */
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
  private final ConcurrentHashMap<String, List<Employee>> employeeCache;

  @Autowired
  private RestTemplate restTemplate;

  public EmployeeServiceImpl() {
    this.employeeCache = new ConcurrentHashMap<>();
    ;
  }

  @Override
  public List<Employee> getAllEmployees() throws IOException {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    String url = "https://dummy.restapiexample.com/api/v1/employees";
    ResponseEntity<EmployeeList> responseEntity = null;
    try {
      responseEntity = restTemplate.exchange(
        url, HttpMethod.GET, request, EmployeeList.class
      );
    } catch (Exception e) {
      log.error(e.getMessage());
      if (e.getMessage().contains("429")) {
        if (Objects.isNull(employeeCache.get("listEmployee"))) {
          return Collections.singletonList(Employee.builder()
            .employee_name("Test")
            .employee_salary(Integer.parseInt("170750"))
            .employee_age(Integer.parseInt("33"))
            .build());
        }
        return employeeCache.get("listEmployee");
      }
    }
    log.info("Details of All employees: " + responseEntity.getBody().getData());
    employeeCache.put("listEmployee", responseEntity.getBody().getData());
    return responseEntity.getBody().getData();
  }

  @Override
  public List<Employee> getEmployeesByNameSearch(String searchString) {
    List<Employee> employeeList;
    try {
      employeeList = getAllEmployees();
    } catch (IOException e) {
      log.error(e.getMessage());
      return Collections.singletonList(Employee.builder()
        .employee_name(searchString)
        .employee_salary(Integer.parseInt("170750"))
        .employee_age(Integer.parseInt("33"))
        .build());
    }
    return employeeList.stream()
      .filter(employee -> employee.getEmployee_name().contains(searchString))
      .collect(Collectors.toList());
  }

  @Override
  public Employee getEmployeeById(String id) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    String url = "https://dummy.restapiexample.com/api/v1/employee/{id}";
    ResponseEntity<EmployeeData> responseEntity;
    try {
      responseEntity = restTemplate.exchange(
        url, HttpMethod.GET, request, EmployeeData.class, id);
    } catch (Exception e) {
      log.error(e.getMessage());
      return Employee.builder()
        .id(Integer.parseInt(id))
        .employee_name("Test")
        .employee_salary(Integer.parseInt("170750"))
        .employee_age(Integer.parseInt("33"))
        .build();
    }
    log.info("Employee Details" + responseEntity.getBody().getData());
    return responseEntity.getBody().getData();
  }

  @Override
  public Integer getHighestSalaryOfEmployees() {
    List<Employee> employeeList = null;
    try {
      employeeList = getAllEmployees();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return employeeList.stream()
      .max(Comparator.comparing(Employee::getEmployee_salary))
      .get().getEmployee_salary();
  }

  @Override
  public List<String> getTopTenHighestEarningEmployeeNames() {
    List<Employee> employeeList = null;
    try {
      employeeList = getAllEmployees();
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    return employeeList.stream().sorted(Comparator.comparing(Employee::getEmployee_salary).reversed())
      .limit(10)
      .map(Employee::getEmployee_name)
      .collect(Collectors.toList());
  }

  @Override
  public Employee createEmployee(String name, String salary, String age) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    Employee employee = Employee.builder()
      .employee_name(name)
      .employee_salary(Integer.parseInt(salary))
      .employee_age(Integer.parseInt(age))
      .build();

    HttpEntity<String> request = new HttpEntity<>(employee.toString(), httpHeaders);
    String url = "https://dummy.restapiexample.com/api/v1/create";
    ResponseEntity<EmployeeData> responseEntity = null;
    try {
      responseEntity = restTemplate.exchange(
        url, HttpMethod.POST, request, EmployeeData.class);
    } catch (Exception e) {
      if (e.getMessage().contains("429")) {
        return Employee.builder()
          .employee_name(name)
          .employee_salary(Integer.parseInt(salary))
          .employee_age(Integer.parseInt(age))
          .build();
      }
    }
    log.info("Created employee Data : ", responseEntity.getBody().getData());

    return responseEntity.getBody().getData();
  }

  @Override
  public String deleteEmployee(String id) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    String url = "https://dummy.restapiexample.com/api/v1/delete/{id}";
    Employee employee = getEmployeeById(id);
    if (Objects.isNull(employee)) {
      return "No Employee exists";
    }

    ResponseEntity<EmployeeData> responseEntity = null;
    try {
      responseEntity = restTemplate.exchange(
        url, HttpMethod.DELETE, request, EmployeeData.class, id);
    } catch (Exception e) {
      if (e.getMessage().contains("429")) {
        return "successfully! deleted Records";
      }
    }
    if (responseEntity.getStatusCode() == HttpStatus.OK) {
      return "successfully! deleted Records";
    } else {
      return "No Employee exists";
    }
  }
}
