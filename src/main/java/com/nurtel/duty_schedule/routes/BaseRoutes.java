package com.nurtel.duty_schedule.routes;

public class BaseRoutes {
    public final static String ROOT = "/api/v1";
    public final static String NOT_SECURED = ROOT + "/not-secured";
    public final static String NOT_SECURED_INIT = NOT_SECURED + "/init";
    public final static String NOT_SECURED_DEPARTMENT_FILL = NOT_SECURED + "/departments/fill";

    public final static String USERS = ROOT + "/users";
    public final static String USER_BY_ID = USERS + "/{id}";
    public final static String USER_EDIT = USERS + "/edit";

    public final static String DEPARTMENTS = ROOT + "/departments";
    public final static String DEPARTMENT_BY_ID = DEPARTMENTS + "/{id}";

    public final static String EMPLOYEES = ROOT + "/employees";
    public final static String EMPLOYEE_BY_ID = EMPLOYEES + "/{id}";

    public final static String SCHEDULE = ROOT + "/schedule";
    public final static String SCHEDULE_BY_ID = SCHEDULE + "/{id}";
    public final static String SCHEDULE_GET_DUTY_BY_DEPARTMENT = SCHEDULE + "/duty/{departmentId}";
}
