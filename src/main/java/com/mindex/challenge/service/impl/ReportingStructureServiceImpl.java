package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure getAllReports(String id) {
        LOG.debug("Received request to calculate reporting for employee id [{}]", id);
        Employee employee = employeeService.read(id);
        if (employee == null) {
            throw new RuntimeException("Wrong Employee ID");
        }

        int totalReports = this.calculateAllReports(employee);

        ReportingStructure reportingStructure = new ReportingStructure(employee, totalReports);;

        return reportingStructure;
    }

    public int calculateAllReports(Employee targetEmp) {
        LOG.debug("Calculate all reports");
        //use hashset to store all his reports, the total number of reports equal to set.size();
        //because hashset can check duplicate reports. or we can use:
        //int count=0;
        HashSet<String> idSet = new HashSet<>();
        Queue<Employee> idQueue = new LinkedList<>();

        idQueue.add(targetEmp);
        //use BFS to find every report.
        while (!idQueue.isEmpty()) {
            Employee currentEmployee = idQueue.poll();
            List<Employee> directReports = currentEmployee.getDirectReports();
            if(directReports != null){
                for(int i=0; i<directReports.size(); i++) {
                    String childId  = directReports.get(i).getEmployeeId();
                    //if set contains this id already. return -1.
                    //if it's not necessary to check duplicate, use commented code below.
                    if(!idSet.contains(childId)){
                        idSet.add(childId);
                        idQueue.add(employeeService.read(childId));
                    }else{
                        LOG.error("report structure error");
                        return -1;
                    }
                    //count++;
                    //idSet.add(childId);
                    //idQueue.add(employeeService.read(childId));

                }
            }
        }
        System.out.println(idSet.size());
        return idSet.size();
    }

}
