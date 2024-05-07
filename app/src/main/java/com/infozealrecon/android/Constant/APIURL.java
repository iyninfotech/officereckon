package com.infozealrecon.android.Constant;

public class APIURL {
    public static final String BASE_URL = "http://103.250.144.192:8555/api/v1/";

    public static final String PERFORM_LOGIN = "UserLogin";
    public static final String CLIENT = "getpartymaster";
    public static final String CLIENTBYID = "GetPartyByID";
    public static final String GETInquiryHead = "GetInquiryHead";
    public static final String GETInquiryHeadBYID = "GetInquiryHeadBYID";


    public static final String LOGIN_AUTH = "LoginAuth";
    public static final String MRP = "GetMRP";
    public static final String MRP_DETAILS = "GetMRPDetails";
    public static final String InVoice = "GetInvoice";
    public static final String DIVISION = "GetDivision";
    public static final String ROUTE = "GetRoute";
    public static final String SALESMEN = "GetSalesman";
    public static final String GODOWN = "GetGodown";
    public static final String ITEM = "GetItem";
    public static final String GETUNIT = "GetUnits";
    public static final String GETALTUNITS = "GetAlternetUnits";
    public static final String DASHBOARD = "GetDashboard";
    public static final String LOGOUT = "PerformLogout";
    public static final String POSTINVOICE = "PostInvoice";
    public static final String GETINVOICEBYID = "GetInvoicebyId";
    public static final String POSTSETTING = "PostSetting";

    //software lock
    public static final String BASE_URL_epr = "http://182.18.139.221:9131/api/v3/";
    public static final String GETCUSTID = "ePromptGetCustomer";
    public static final String ADDUPDATE = "ePromptCustomerAddUpdate";
    public static final String LOCKDELETE = "ePromptDeleteCustomer";
}
