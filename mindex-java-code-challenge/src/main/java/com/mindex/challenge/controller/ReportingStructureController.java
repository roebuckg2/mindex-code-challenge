package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportingStructureController {
    /***
     * This controller contains private methods that recursively fill in the structure for the type.
     * This allows the ReportingStructure class to avoid needing to have an autowired EmployeeService.
     * Since the structure does not need to persist, there is no need for a repository.
     ***/
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureController.class);

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/reportingStructure/{id}")
    public ReportingStructure read(@PathVariable String id) {
        LOG.debug("Received request for ReportingStructure", id);

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(fillStructure(id));
        reportingStructure.setNumberOfReports(calcNumReports(reportingStructure.getEmployee()));
        return reportingStructure;
    }

    /***
     * This method will recursively fill in the reportingStructure on the fly with all information for an
     * employee including all information with directReports
     * @param id
     * @return an employee with its directReports data completed instead of just the employee id
     */
    private Employee fillStructure(String id) {
        Employee employee = employeeService.read(id);
        List<Employee> directReports = employee.getDirectReports();
        if(directReports != null) {
            for(int i=0; i<directReports.size(); i++) {
                directReports.set(i, fillStructure(directReports.get(i).getEmployeeId()));
            }
        }
        return employee;
    }

    /***
     * Calculate directReports on the fly using recursion since the hierarchy employs a tree structure.
     * @param employee
     * @return
     */
    private int calcNumReports(Employee employee) {
        int numReports = 0;

        List<Employee> employeeList = employee.getDirectReports();
        if (employeeList != null) {
            numReports += employeeList.size();
            for (Employee e : employeeList) {
                numReports += calcNumReports(e);
            }
        }
        return numReports;
    }
}
