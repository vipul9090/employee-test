package com.example.rqchallenge.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import com.example.rqchallenge.exceptions.RequestException;
import com.example.rqchallenge.pojo.Employee;
import com.example.rqchallenge.pojo.EmployeeData;
import com.example.rqchallenge.pojo.EmployeeList;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EmployeeServiceImplTest {
  private final List<Employee> employeeList = new ArrayList<>();

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private EmployeeServiceImpl employeeService = new EmployeeServiceImpl();

  @BeforeEach
  public void initializeData() {
    employeeList.add(Employee.builder().id(1).employee_name("Vipul").employee_age(33).employee_salary(1000).build());
    employeeList.add(Employee.builder().id(2).employee_name("Shivani").employee_age(32).employee_salary(1002).build());
    employeeList.add(Employee.builder().id(3).employee_name("Doris").employee_age(44).employee_salary(1004).build());
    employeeList.add(Employee.builder().id(4).employee_name("Nixon").employee_age(67).employee_salary(100001).build());
    employeeList
      .add(Employee.builder().id(5).employee_name("Kennedy").employee_age(27).employee_salary(100006).build());
    employeeList.
      add(Employee.builder().id(6).employee_name("Haley").employee_age(33).employee_salary(10000).build());
    employeeList.
      add(Employee.builder().id(7).employee_name("Tiger").employee_age(33).employee_salary(10001).build());
    employeeList.
      add(Employee.builder().id(8).employee_name("Vance").employee_age(33).employee_salary(10002).build());
    employeeList.add(Employee.builder().id(9).employee_name("Caesar").employee_age(33).employee_salary(10003).build());
    employeeList.add(Employee.builder().id(10).employee_name("Yuri").employee_age(33).employee_salary(10004).build());
    employeeList
      .add(Employee.builder().id(11).employee_name("Henry").employee_age(33).employee_salary(10005).build());
  }

  @Test
  public void gettAllEmployees() throws URISyntaxException, IOException {
    getAllEmployee();
    List<Employee> allEmployeesList = employeeService.getAllEmployees();
    assertEquals(allEmployeesList.size(), employeeList.size());
    assertEquals(allEmployeesList, employeeList);
  }

  @Test
  public void getEmployeesByNameSearch() throws URISyntaxException {
    getAllEmployee();
    List<Employee> allEmployeesList = employeeService.getEmployeesByNameSearch("Vip");
    assertEquals(allEmployeesList.get(0).getEmployee_name(), "Vipul");
  }

  @Test
  public void getEmployeeById() {
    EmployeeData employeeData = getEmployeeByID();
    Employee employee = employeeService.getEmployeeById("1");
    assertEquals(employeeData.getData(), employee);
  }

  private EmployeeData getEmployeeByID() {
    String id = "1";
    EmployeeData employeeResponse = EmployeeData.builder()
      .data(Employee.builder().id(1)
        .employee_name("vipul")
        .employee_age(33)
        .employee_salary(1000)
        .build())
      .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    when(restTemplate.exchange(
      "https://dummy.restapiexample.com/api/v1/employee/{id}",
      HttpMethod.GET,
      request,
      EmployeeData.class,
      id))
      .thenReturn(new ResponseEntity(employeeResponse, HttpStatus.OK));
    return employeeResponse;
  }

  @Test
  public void highestSalaryOfEmployees() throws URISyntaxException, IOException {
    getAllEmployee();

    Integer highestSalaryOfEmployee = employeeService.getHighestSalaryOfEmployees();

    assertEquals(highestSalaryOfEmployee, Integer.valueOf(100006));
  }

  @Test
  public void topTenHighestEarningEmployeeNames() throws URISyntaxException {
    getAllEmployee();

    List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

    assertEquals(topTenHighestEarningEmployeeNames.contains("Nitesh"), false);
    assertEquals(topTenHighestEarningEmployeeNames.contains("Henry"), true);
    assertEquals(topTenHighestEarningEmployeeNames.size(), 10);
  }

  @Test
  public void createEmployee() {
    Employee employee = Employee.builder()
      .employee_name("Byrd")
      .employee_salary(1004)
      .employee_age(29)
      .build();

    EmployeeData employeeResponse = EmployeeData.builder().data(employee).status("Success").build();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(employee.toString(), httpHeaders);

    when(restTemplate.exchange(
      "https://dummy.restapiexample.com/api/v1/create",
      HttpMethod.POST,
      request,
      EmployeeData.class))
      .thenReturn(new ResponseEntity(employeeResponse, HttpStatus.OK));

    Employee serviceEmployee = employeeService.createEmployee("Byrd", "1004", "29");
    assertEquals(serviceEmployee, employee);
    assertEquals(serviceEmployee, employee);

  }

  @Test
  public void deleteEmployee() {
    String id = "1";
    EmployeeData employeeData = getEmployeeByID();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);

    when(restTemplate.exchange(
      "https://dummy.restapiexample.com/api/v1/delete/{id}",
      HttpMethod.DELETE,
      request,
      EmployeeData.class,
      id))
      .thenReturn(new ResponseEntity(employeeData, HttpStatus.OK));

    String message = employeeService.deleteEmployee("1");

    assertEquals(message, "successfully! deleted Records");
  }

  private void getAllEmployee() {
    EmployeeList employeeList = new EmployeeList(this.employeeList);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    when(restTemplate.exchange(
      "https://dummy.restapiexample.com/api/v1/employees",
      HttpMethod.GET,
      request,
      EmployeeList.class))
      .thenReturn(new ResponseEntity(employeeList, HttpStatus.OK));
  }

  @Test
  public void testGetAllEmployeesForTooManyRequest() throws IOException {
    getAllEmployeeException();
    List<Employee> allEmployeesList = employeeService.getAllEmployees();
    assertEquals(1, allEmployeesList.size());
  }

  @Test
  public void testGetEmployeesByNameSearchException() throws URISyntaxException, IOException {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    when(restTemplate.exchange(
      "https://dummy.restapiexample.com/api/v1/employees",
      HttpMethod.GET,
      request,
      EmployeeList.class))
      .thenThrow(new RequestException("Data not found"));
    Exception exception = assertThrows(Exception.class, () -> employeeService.getEmployeesByNameSearch("ABC"));
    assertTrue(exception.getMessage().contains("responseEntity"));
  }

  @Test
  public void testGetEmployeeByIdException() throws URISyntaxException, IOException {
    EmployeeData employeeData = getEmployeeByIDException();
    Employee employee = employeeService.getEmployeeById("1");
    assertEquals(employeeData.getData().getId(), employee.getId());
  }

  @Test
  public void testCreateEmployeeRequestException() throws URISyntaxException, IOException {
    Employee employee = Employee.builder()
      .employee_name("vipul")
      .employee_salary(10000)
      .employee_age(29)
      .build();

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(employee.toString(), httpHeaders);

    when(restTemplate.exchange(
      "https://dummy.restapiexample.com/api/v1/create",
      HttpMethod.POST,
      request,
      EmployeeData.class))
      .thenThrow(new RequestException("429"));

    Employee serviceEmployee = employeeService.createEmployee("vipul", "10000", "29");
    assertEquals(serviceEmployee, employee);
    assertEquals(serviceEmployee, employee);
  }

  private void getAllEmployeeException() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    when(restTemplate.exchange(
      "https://dummy.restapiexample.com/api/v1/employees",
      HttpMethod.GET,
      request,
      EmployeeList.class))
      .thenThrow(new RequestException("429"));
  }

  private EmployeeData getEmployeeByIDException() {
    String id = "1";
    EmployeeData employeeResponse = EmployeeData.builder()
      .data(Employee.builder().id(1)
        .employee_name("test")
        .employee_age(20)
        .employee_salary(1000)
        .build())
      .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(httpHeaders);
    when(restTemplate.exchange(
      "https://dummy.restapiexample.com/api/v1/employee/{id}",
      HttpMethod.GET,
      request,
      EmployeeData.class,
      id))
      .thenThrow(new RequestException("429"));
    return employeeResponse;
  }
}
