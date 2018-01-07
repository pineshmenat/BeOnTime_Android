package com.its5314.project.beontime;

import java.util.List;

/**
 * Created by imsil on 4/1/18.
 */

class EmployeeEngine {
    List<Employee> employees;

    public EmployeeEngine(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "EmployeeEngine{" +
                "employees=" + employees +
                '}';
    }

    public Employee getEmployee(int position) {
        return employees.get(position);
    }

    public int size() {
        return employees.size();
    }
}
