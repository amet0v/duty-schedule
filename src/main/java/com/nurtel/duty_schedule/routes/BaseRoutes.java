package com.nurtel.duty_schedule.routes;

public class BaseRoutes {
    public final static String ROOT = "/api/v1";
    public final static String NOT_SECURED = ROOT + "/not-secured";
    public final static String NOT_SECURED_INIT = NOT_SECURED + "/init";

    public final static String USER = ROOT + "/user";
    public final static String USER_BY_ID = USER + "/{id}";
    public final static String USER_EDIT = USER + "/edit";

    public final static String DEPARTMENT = ROOT + "department";
    public final static String DEPARTMENT_BY_ID = DEPARTMENT + "/{id}";

    public final static String EMPLOYEE = ROOT + "/employee";
    public final static String EMPLOYEE_BY_ID = EMPLOYEE + "/{id}";

}
