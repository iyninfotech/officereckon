package com.score3s.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.score3s.android.asynctasks.NetworkUtils;
import com.score3s.android.asynctasks.StringUtils;
import com.score3s.android.asynctasks.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity {


    Button btnRegister;
    EditText edtAgencyName, edtOwnerName, edtContactNo, edtEmailID;
    TextView tvDemoRequest;

    String AgencyName, OwnerName, ContactNo, EmailID;

    public String EmailSubjectTxt, EmailBodymsgTxt;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);
        edtAgencyName = findViewById(R.id.edtAgencyName);
        edtOwnerName = findViewById(R.id.edtOwnerName);
        edtContactNo = findViewById(R.id.edtContactNo);
        edtEmailID = findViewById(R.id.edtEmailID);
        tvDemoRequest = findViewById(R.id.tvDemoRequest);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtils.isInternetAvailable(RegisterActivity.this)) {

                    attemptLogin();

                } else {

                    NetworkUtils.showNetworkAlertDialog(RegisterActivity.this);
                }
            }
        });


        // String text = "Already registred then click on <b><i>LOGIN</b></i>.";
        String text = "If your are Already using our software then click on <br /> <b><i>LOGIN</b></i>";
        SpannableString spannableString = new SpannableString(Html.fromHtml(text));
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
       /* ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
               // Toast.makeText(MainActivity.this, "THIS", Toast.LENGTH_SHORT).show();
            }
        };
        ClickableSpan clickableSpan3 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
               // Toast.makeText(MainActivity.this, "CLICKED", Toast.LENGTH_SHORT).show();
            }
        };*/
        spannableString.setSpan(clickableSpan1, 54, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // spannableString.setSpan(clickableSpan2, 16,20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // spannableString.setSpan(clickableSpan3, 27,34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDemoRequest.setText(spannableString);
        tvDemoRequest.setMovementMethod(LinkMovementMethod.getInstance());


    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void attemptLogin() {

        // Store values at the time of the login attempt.
        AgencyName = edtAgencyName.getText().toString();
        OwnerName = edtOwnerName.getText().toString();
        ContactNo = edtContactNo.getText().toString();
        EmailID = edtEmailID.getText().toString();


        if (StringUtils.isBlank(AgencyName)) {
            edtAgencyName.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter Agency Name");
        } else if (StringUtils.isBlank(OwnerName)) {
            edtOwnerName.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter Owner/Company Name");
        } else if (StringUtils.isBlank(ContactNo)) {
            edtContactNo.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter Contact Number");
        } else if (StringUtils.isBlank(EmailID)) {
            edtEmailID.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter Email Id");
        } else if (!emailValidator(EmailID)) {
            edtEmailID.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter valid Email Id");
        } else {
            if (NetworkUtils.isInternetAvailable(this)) {
                EmailSubjectTxt = "Contact To " + OwnerName.trim() + " for Software & Mobile App Demonstration(Socre 3S FMCG Mobile App)";
                EmailBodymsgTxt = "<div><i>Company Name:</i>&nbsp;<b>" + AgencyName.trim() + "</b></div><br />" +
                        "<div><i>Contact Person:</i>&nbsp;<b>" + OwnerName.trim() + "</b></div><br />" +
                        "<div><i>Contact Number:</i>&nbsp;<b>" + ContactNo.trim() + "</b></div><br />" +
                        "<div><i>Email Address:</i>&nbsp;<b>" + EmailID.trim() + "</b></div>";
                sendEmailtoInfozeal(EmailSubjectTxt, EmailBodymsgTxt);

            } else {
                NetworkUtils.showNetworkAlertDialog(this);
            }
        }
    }


    private void sendEmailtoInfozeal(String mailSubject, String msgBody) {
        BackgroundMail.newBuilder(this)
                .withUsername("sales@infozeal.co.in")
                .withPassword("google@206")
                .withMailBcc("support.infozeal@gmail.com")
                .withSubject(mailSubject)
                .withBody(msgBody)
                .withType("text/html")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {

                        EmailSubjectTxt = "Hello," + OwnerName.trim() + " thanks for registered demonstration of our Score 3s Cloud .net Software & Mobile App.";
                        EmailBodymsgTxt = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                "<head>\n" +
                                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                                "    <title>ePrompt 6 Cloud .net</title>\n" +
                                "\n" +
                                "    <style type=\"text/css\">\n" +
                                "        .ped {\n" +
                                "             margin-left:0px;\n" +
                                "             padding-left:0px;\n" +
                                "             \n" +
                                "        \n" +
                                "        }\n" +
                                "        .pedkey {\n" +
                                "\n" +
                                "             margin-left:0px;\n" +
                                "             padding-left:15px;\n" +
                                "             \n" +
                                "        \n" +
                                "        }\n" +
                                "        /* Client-specific Styles */\n" +
                                "        #outlook a {\n" +
                                "            padding: 0;\n" +
                                "        }\n" +
                                "        /* Force Outlook to provide a \"view in browser\" menu link. */\n" +
                                "        body {\n" +
                                "            width: 100% !important;\n" +
                                "            -webkit-text-size-adjust: 100%;\n" +
                                "            -ms-text-size-adjust: 100%;\n" +
                                "            margin: 0;\n" +
                                "            padding: 0;\n" +
                                "        }\n" +
                                "        /* Prevent Webkit and Windows Mobile platforms from changing default font sizes, while not breaking desktop design. */\n" +
                                "        .ExternalClass {\n" +
                                "            width: 100%;\n" +
                                "        }\n" +
                                "            /* Force Hotmail to display emails at full width */\n" +
                                "            .ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div {\n" +
                                "                line-height: 100%;\n" +
                                "            }\n" +
                                "        /* Force Hotmail to display normal line spacing.  */\n" +
                                "        .backgroundTable {\n" +
                                "            margin: 0;\n" +
                                "            padding: 0;\n" +
                                "            width: 100% !important;\n" +
                                "            line-height: 100% !important;\n" +
                                "        }\n" +
                                "\n" +
                                "        img {\n" +
                                "            outline: none;\n" +
                                "            text-decoration: none;\n" +
                                "            border: none;\n" +
                                "            -ms-interpolation-mode: bicubic;\n" +
                                "        }\n" +
                                "\n" +
                                "        a img {\n" +
                                "            border: none;\n" +
                                "        }\n" +
                                "\n" +
                                "        .image_fix {\n" +
                                "            display: block;\n" +
                                "        }\n" +
                                "\n" +
                                "        p {\n" +
                                "            margin: 0px 0px !important;\n" +
                                "        }\n" +
                                "\n" +
                                "        table td {\n" +
                                "            border-collapse: collapse;\n" +
                                "        }\n" +
                                "\n" +
                                "        table {\n" +
                                "            border-collapse: collapse;\n" +
                                "            mso-table-lspace: 0pt;\n" +
                                "            mso-table-rspace: 0pt;\n" +
                                "        }\n" +
                                "\n" +
                                "        a {\n" +
                                "            font-family: Verdana,Helvetica, arial, sans-serif;\n" +
                                "            color: #6d0303;\n" +
                                "            text-decoration: none;\n" +
                                "            text-decoration: none!important;\n" +
                                "            text-anchor: middle;\n" +
                                "            margin-left: 5px;\n" +
                                "        }\n" +
                                "            a:hover {\n" +
                                "                color:#ffffff;\n" +
                                "                background-color:#d41b29;\n" +
                                "                \n" +
                                "                \n" +
                                "            }\n" +
                                "        \n" +
                                "\n" +
                                "           \n" +
                                "\n" +
                                "        h4 {\n" +
                                "            font-family: Verdana,Helvetica, arial, sans-serif;           \n" +
                                "            background-color:#2a2a2a;\n" +
                                "            color: #ffffff;\n" +
                                "            margin-top: 1px;\n" +
                                "            margin-bottom: 0px;\n" +
                                "            display: block;\n" +
                                "            margin-right: 0.5px;\n" +
                                "            margin-left: 2px;\n" +
                                "            text-align: center;\n" +
                                "        }\n" +
                                "\n" +
                                "       \n" +
                                "\n" +
                                "               \n" +
                                "        li {\n" +
                                "            margin-left: 20px;\n" +
                                "            \n" +
                                "            list-style:url('http://www.infozeal.co.in/images/marker2.jpg');\n" +
                                "            list-style-type:inherit;\n" +
                                "        }\n" +
                                "\n" +
                                "        /*STYLES*/\n" +
                                "        table[class=full] {\n" +
                                "            width: 100%;\n" +
                                "            clear: both;\n" +
                                "        }\n" +
                                "        /*IPAD STYLES*/\n" +
                                "        @media only screen and (max-width: 640px) {\n" +
                                "            a[href^=\"tel\"], a[href^=\"sms\"] {\n" +
                                "                text-decoration: none;\n" +
                                "                color: #33b9ff; /* or whatever your want */\n" +
                                "                pointer-events: none;\n" +
                                "                cursor: default;\n" +
                                "            }\n" +
                                "\n" +
                                "            .mobile_link a[href^=\"tel\"], .mobile_link a[href^=\"sms\"] {\n" +
                                "                text-decoration: default;\n" +
                                "                color: #33b9ff !important;\n" +
                                "                pointer-events: auto;\n" +
                                "                cursor: default;\n" +
                                "            }\n" +
                                "\n" +
                                "            table[class=devicewidth] {\n" +
                                "                width: 440px!important;\n" +
                                "                text-align: center!important;\n" +
                                "            }\n" +
                                "\n" +
                                "            table[class=devicewidthinner] {\n" +
                                "                width: 420px!important;\n" +
                                "                text-align: center!important;\n" +
                                "            }\n" +
                                "\n" +
                                "            img[class=banner] {\n" +
                                "                width: 440px!important;\n" +
                                "                height: 220px!important;\n" +
                                "            }\n" +
                                "\n" +
                                "            img[class=colimg2] {\n" +
                                "                width: 440px!important;\n" +
                                "                height: 220px!important;\n" +
                                "            }\n" +
                                "        }\n" +
                                "        /*IPHONE STYLES*/\n" +
                                "        @media only screen and (max-width: 480px) {\n" +
                                "            a[href^=\"tel\"], a[href^=\"sms\"] {\n" +
                                "                text-decoration: none;\n" +
                                "                color: #ffffff; /* or whatever your want */\n" +
                                "                pointer-events: none;\n" +
                                "                cursor: default;\n" +
                                "            }\n" +
                                "\n" +
                                "            .mobile_link a[href^=\"tel\"], .mobile_link a[href^=\"sms\"] {\n" +
                                "                text-decoration: default;\n" +
                                "                color: #ffffff !important;\n" +
                                "                pointer-events: auto;\n" +
                                "                cursor: default;\n" +
                                "            }\n" +
                                "\n" +
                                "            table[class=devicewidth] {\n" +
                                "                width: 280px!important;\n" +
                                "                text-align: center!important;\n" +
                                "            }\n" +
                                "\n" +
                                "            table[class=devicewidthinner] {\n" +
                                "                width: 260px!important;\n" +
                                "                text-align: center!important;\n" +
                                "            }\n" +
                                "\n" +
                                "            img[class=banner] {\n" +
                                "                width: 280px!important;\n" +
                                "                height: 140px!important;\n" +
                                "            }\n" +
                                "\n" +
                                "            img[class=colimg2] {\n" +
                                "                width: 280px!important;\n" +
                                "                height: 140px!important;\n" +
                                "            }\n" +
                                "\n" +
                                "            td[class=\"padding-top15\"] {\n" +
                                "                padding-top: 15px!important;\n" +
                                "            }\n" +
                                "        }\n" +
                                "\n" +
                                "        .auto-style1 {\n" +
                                "            height: 5px;\n" +
                                "        }\n" +
                                "    </style>\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "    <!-- Start of preheader -->\n" +
                                "    <table width=\"100%\" bgcolor=\"#2a2a2a\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"backgroundTable\" st-sortable=\"preheader\">\n" +
                                "        <tbody>\n" +
                                "            <tr>\n" +
                                "                <td>\n" +
                                "                    <table width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                        <tbody>\n" +
                                "                            <tr>\n" +
                                "                                <td width=\"100%\">\n" +
                                "                                    <table width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                                        <tbody>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td width=\"100%\" height=\"5\"></td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td>\n" +
                                "                                                    <table width=\"60%\" align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                                "                                                        <tbody>\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td align=\"left\" valign=\"middle\" style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 13px; color: #282828\" st-content=\"viewonline\">\n" +
                                "                                                                    <a href=\"http://www.infozeal.co.in\" style=\"text-decoration: none; color: #ffffff;\" onclick=\"this.target='_blank'\">We provide total solution for FMCG Distributor.</a>\n" +
                                "                                                                </td>\n" +
                                "                                                            </tr>\n" +
                                "                                                        </tbody>\n" +
                                "                                                    </table>\n" +
                                "                                                    <table width=\"36%\" align=\"right\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                                "                                                        <tbody>\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td align=\"right\" valign=\"middle\" style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 13px; color: #282828\" st-content=\"forward\">\n" +
                                "                                                                    <a href=\"http://www.infozeal.co.in/contacts\" style=\"text-decoration: none; color: #ffffff;\" onclick=\"this.target='_blank'\">Contact Us</a>\n" +
                                "                                                                </td>\n" +
                                "                                                            </tr>\n" +
                                "                                                        </tbody>\n" +
                                "                                                    </table>\n" +
                                "                                                </td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td width=\"100%\" height=\"5\"></td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                        </tbody>\n" +
                                "                                    </table>\n" +
                                "                                </td>\n" +
                                "                            </tr>\n" +
                                "                        </tbody>\n" +
                                "                    </table>\n" +
                                "                </td>\n" +
                                "            </tr>\n" +
                                "        </tbody>\n" +
                                "    </table>\n" +
                                "    <!-- End of preheader -->\n" +
                                "\n" +
                                "    <!-- Start of header -->\n" +
                                "    <table width=\"100%\" bgcolor=\"#2a2a2a\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"backgroundTable\" st-sortable=\"header\">\n" +
                                "        <tbody>\n" +
                                "            <tr>\n" +
                                "                <td>\n" +
                                "                    <table width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                        <tbody>\n" +
                                "                            <tr>\n" +
                                "                                <td width=\"100%\">\n" +
                                "                                    <table style=\"background-color: #ffffff;\" width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                                        <tbody>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td style=\"font-size: 1px; line-height: 1px; mso-line-height-rule: exactly;\" class=\"auto-style1\"></td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td>\n" +
                                "                                                    <!-- logo -->\n" +
                                "                                                    <table width=\"400\" align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"devicewidth\">\n" +
                                "                                                        <tbody>\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td align=\"start\" style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 16px; color: #6d6d7a;padding:5px\" st-content=\"phone\" height=\"60\">\n" +
                                "                                                                  <b><i> Welcome, " + OwnerName.trim() + "</i></b>\n" +
                                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                                "                                                                </td>\n" +
                                "                                                            </tr>\n" +
                                "                                                        </tbody>\n" +
                                "                                                    </table>\n" +
                                "                                                    <!-- end of logo -->\n" +
                                "                                                    <!-- start of menu -->\n" +
                                "                                                    <table width=\"300\" border=\"0\" align=\"right\" valign=\"middle\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"devicewidth\">\n" +
                                "                                                        <tbody>\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td align=\"center\" style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 25px; color: #ffffff\" st-content=\"phone\" height=\"78\"><!--ePrompt 6 Cloud .net--> \n" +
                                "                                                                    <img src=\"http://www.infozeal.co.in/images/logo.png\" alt=\"logo\" width=\"192\" />\n" +
                                "                                                                </td>\n" +
                                "                                                                \n" +
                                "                                                            </tr>\n" +
                                "                                                        </tbody>\n" +
                                "                                                    </table>\n" +
                                "                                                    <!-- end of menu -->\n" +
                                "                                                </td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td height=\"10\" style=\"font-size: 1px; line-height: 1px; mso-line-height-rule: exactly;\">&nbsp;</td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                        </tbody>\n" +
                                "                                    </table>\n" +
                                "                                </td>\n" +
                                "                            </tr>\n" +
                                "                        </tbody>\n" +
                                "                    </table>\n" +
                                "                </td>\n" +
                                "            </tr>\n" +
                                "        </tbody>\n" +
                                "    </table>\n" +
                                "    <!-- End of Header -->\n" +
                                "\t <!-- Thanks Msg  -->\n" +
                                "    <table width=\"100%\" bgcolor=\"#2a2a2a\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"backgroundTable\">\n" +
                                "        <tbody>\n" +
                                "            <tr>\n" +
                                "                <td>\n" +
                                "                    <table width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                        <tbody>\n" +
                                "                            <tr>\n" +
                                "                                <td width=\"100%\">\n" +
                                "                                    <table bgcolor=\"#ffffff\" width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                                        <tbody>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td height=\"5\"></td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td>\n" +
                                "                                                    <table width=\"770\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"devicewidthinner\">\n" +
                                "                                                        <tbody>\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td>\n" +
                                "                                                                   \n" +
                                "                                                                    <!-- start of right column -->\n" +
                                "                                                                    <table width=\"770\" align=\"right\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"devicewidthinner\">\n" +
                                "                                                                        <tbody>\n" +
                                "                                                                          <!-- content -->\n" +
                                "                                                                            <tr>\n" +
                                "                                                                                <td style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 14px; color: #4f5458; text-align: left; line-height: 20px;\">\n" +
                                "                                                                                   <p style=\"text-align:justify;\">                                                                                   \n" +
                                "                                                                                    Thanks for download our Mobile Application Score 3S for FMCG distributers. Our sales exucative will call you as soon as possible for demostration of our Score 3s Cloud .net software and Mobile App. He will explain all functionality of boths and provide you demo login credentials.If you have any query then contact direct to sales manager through contact number or email. \n" +
                                "                                                                                   </p>\n" +
                                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   <p style=\"text-align:justify;\">  \n" +
                                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<br /><br /><br />\n" +
                                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   &nbsp;&nbsp;<b>Thanks and Regards</b><br />\n" +
                                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   &nbsp;Ramesh Gajjar (Manager)<br />\n" +
                                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   &nbsp;+91 97277 75711<br />\n" +
                                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   &nbsp;<a href=\"mailto:sales@infozeal.co.in?subject=We Want Demostration Of ePrompt 6 Cloud .net&body=Please Provide Us Demostartion of Software:\" style=\"text-decoration: none; color: #000000;\" onclick=\"this.target='_blank'\">sales@infozeal.co.in</a><br />\n" +
                                "                                                                                   </p>\n" +
                                "                                                                                </td>\n" +
                                "                                                                            </tr>\n" +
                                "                                                                            <!-- end of content -->\n" +
                                "                                                                        </tbody>\n" +
                                "                                                                    </table>\n" +
                                "                                                                    <!-- end of right column -->\n" +
                                "                                                                </td>\n" +
                                "                                                            </tr>\n" +
                                "                                                        </tbody>\n" +
                                "                                                    </table>\n" +
                                "                                                </td>\n" +
                                "                                            </tr>\n" +
                                "                                            \n" +
                                "                                        </tbody>\n" +
                                "                                    </table>\n" +
                                "                                </td>\n" +
                                "                            </tr>\n" +
                                "                        </tbody>\n" +
                                "                    </table>\n" +
                                "                </td>\n" +
                                "            </tr>\n" +
                                "        </tbody>\n" +
                                "    </table>\n" +
                                "    <!-- end of Thanksmsg -->   \n" +
                                "    <!-- Start of seperator -->\n" +
                                "    <table width=\"100%\" bgcolor=\"#2a2a2a\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"backgroundTable\" st-sortable=\"seperator\">\n" +
                                "        <tbody>\n" +
                                "            <tr>\n" +
                                "                <td>\n" +
                                "                    <table width=\"800\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"devicewidth\">\n" +
                                "                        <tbody>\n" +
                                "                            <tr>\n" +
                                "                                <td align=\"center\" height=\"10\" style=\"font-size: 1px; line-height: 1px;\">&nbsp;</td>\n" +
                                "                            </tr>\n" +
                                "                        </tbody>\n" +
                                "                    </table>\n" +
                                "                </td>\n" +
                                "            </tr>\n" +
                                "        </tbody>\n" +
                                "    </table>\n" +
                                "    <!-- End of seperator -->\n" +
                                "    <!-- Start of heading -->\n" +
                                "    <table width=\"100%\" bgcolor=\"#2a2a2a\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"backgroundTable\" st-sortable=\"seperator\">\n" +
                                "        <tbody>\n" +
                                "            <tr>\n" +
                                "                <td>\n" +
                                "                    <table width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                        <tbody>\n" +
                                "                            <tr>\n" +
                                "                                <td width=\"100%\">\n" +
                                "                                    <table bgcolor=\"#d41b29\" width=\"800\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"devicewidth\">\n" +
                                "                                        <tbody>\n" +
                                "                                            <tr>\n" +
                                "                                                <td align=\"left\" style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 22px; color: #ffffff; padding: 15px 15px;\" st-content=\"heading\" >\n" +
                                "                                                    About Score 3s Cloud .net\n" +
                                "                                                </td>\n" +
                                "                                            </tr>\n" +
                                "                                        </tbody>\n" +
                                "                                    </table>\n" +
                                "                                </td>\n" +
                                "                            </tr>\n" +
                                "                        </tbody>\n" +
                                "                    </table>\n" +
                                "                </td>\n" +
                                "            </tr>\n" +
                                "        </tbody>\n" +
                                "    </table>\n" +
                                "    <!-- End of heading -->\n" +
                                "    <!-- About  -->\n" +
                                "    <table width=\"100%\" bgcolor=\"#2a2a2a\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"backgroundTable\">\n" +
                                "        <tbody>\n" +
                                "            <tr>\n" +
                                "                <td>\n" +
                                "                    <table width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                        <tbody>\n" +
                                "                            <tr>\n" +
                                "                                <td width=\"100%\">\n" +
                                "                                    <table bgcolor=\"#ffffff\" width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                                        <tbody>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td height=\"5\"></td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td>\n" +
                                "                                                    <table width=\"770\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"devicewidthinner\">\n" +
                                "                                                        <tbody>\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td>\n" +
                                "                                                                   \n" +
                                "                                                                    <!-- start of right column -->\n" +
                                "                                                                    <table width=\"770\" align=\"right\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"devicewidthinner\">\n" +
                                "                                                                        <tbody>\n" +
                                "                                                                          <!-- content -->\n" +
                                "                                                                            <tr>\n" +
                                "                                                                                <td style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 14px; color: #4f5458; text-align: left; line-height: 20px;\">\n" +
                                "                                                                                  In Score 3s software you can do all back office work.\n" +
                                "                                                                                   <p style=\"text-align:justify;\">                                                                                   \n" +
                                "                                                                                    Start from Purchase Order, Sales Order Manual & Android, Goods Receipt Note, Goods Issue Note, Godown Transfer, Branch Transfer, Formula for Finish Product, Raw Material Consumption, Semi Finish, Finish Goods, Physical Stock Verification, Stock Journal, Goods Replacement, Quotation, Purchase (With Material/Without Material), Purchase Return, Sales(With Material/Without Material), Sales Return, General Credit/Debit Note, Journal Voucher, Bank Receipt, Cash Receipt, Cash Receipt Android, Bank Payment, Cash Payment, Bank Reconciliation, Client Database (Contact Master), Service Tax, GST, TDS, Trading, Profit & Loss, Balance Sheet, Reminders, Report (More than 200) and <a href=\"http://www.infozeal.co.in/score3s\" style=\"font-style:italic;border-radius:10%;background-color:#d41b29;color: #ffffff;\"onclick=\"this.target='_blank'\">Read more.......</a>\n" +
                                "                                                                                   </p>\n" +
                                "                                                                                </td>\n" +
                                "                                                                            </tr>\n" +
                                "                                                                            <!-- end of content -->\n" +
                                "                                                                        </tbody>\n" +
                                "                                                                    </table>\n" +
                                "                                                                    <!-- end of right column -->\n" +
                                "                                                                </td>\n" +
                                "                                                            </tr>\n" +
                                "                                                        </tbody>\n" +
                                "                                                    </table>\n" +
                                "                                                </td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td height=\"20\"></td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- Spacing -->\n" +
                                "                                            <!-- bottom-border -->\n" +
                                "                                            <tr>\n" +
                                "                                                <td width=\"100%\" bgcolor=\"#2a2a2a\" height=\"10\" style=\"font-size: 1px; line-height: 1px;\">&nbsp;</td>\n" +
                                "                                            </tr>\n" +
                                "                                            <!-- /bottom-border -->\n" +
                                "                                        </tbody>\n" +
                                "                                    </table>\n" +
                                "                                </td>\n" +
                                "                            </tr>\n" +
                                "                        </tbody>\n" +
                                "                    </table>\n" +
                                "                </td>\n" +
                                "            </tr>\n" +
                                "        </tbody>\n" +
                                "    </table>\n" +
                                "    <!-- end of About -->\n" +
                                "    <!-- footer -->\n" +
                                "    <table width=\"100%\" bgcolor=\"#2a2a2a\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"backgroundTable\">\n" +
                                "        <tbody>\n" +
                                "            <tr>\n" +
                                "                <td>\n" +
                                "                    <table width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\" \">\n" +
                                "                        <tbody>\n" +
                                "                            <tr>\n" +
                                "                                <td width=\"100%\">\n" +
                                "                                    <table bgcolor=\"#d41b29\" width=\"800\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                                "                                        <tbody>\n" +
                                "                                            <tr>\n" +
                                "                                                <td>\n" +
                                "                                                    <table width=\"400\" align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"devicewidth\" >\n" +
                                "                                                        <tbody>\n" +
                                "                                                            <!-- Spacing -->\n" +
                                "                                                            <tr >\n" +
                                "                                                                <td width=\"100%\" height=\"5\" ></td>\n" +
                                "                                                            </tr>\n" +
                                "                                                            <!-- Spacing -->\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 14px; color: #282828; text-align: left;\" class=\"link\">\n" +
                                "                                                                    <a href=\"http://www.infozeal.co.in/index.php\" style=\"text-decoration: none; color: #ffffff;font-size: 14px;\" onclick=\"this.target='_blank'\">Infozeal eSolutions Private Limited</a>\n" +
                                "                                                                </td>\n" +
                                "                                                            </tr>\n" +
                                "                                                              <!-- Spacing -->\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td width=\"100%\" height=\"5\"></td>\n" +
                                "                                                            </tr>\n" +
                                "                                                            <!-- Spacing -->\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td style=\" padding-left:5px; font-family: Verdana,Helvetica, arial, sans-serif; font-size: 11px; color: #ffffff; text-align: left;\" class=\"link\">1310-11-12, 13th Floor, Ganesh Glory,<br />\n" +
                                "                                                                    Nr. BSNL Office,<br />\n" +
                                "                                                                     Off. S.G. Highway,<br />\n" +
                                "                                                                    Jagatpur,<br />\n" +
                                "                                                                    Ahmedabad - 382481,<br />\n" +
                                "                                                                    Gujarat(India).<br />\n" +
                                "                                                                   \n" +
                                "                                                                </td>\n" +
                                "                                                            </tr>\n" +
                                "                                                            <!-- Spacing -->\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td width=\"100%\" height=\"10\"></td>\n" +
                                "                                                            </tr>\n" +
                                "                                                         </tbody>\n" +
                                "                                                    </table>\n" +
                                "                                                    <!-- end of left column -->\n" +
                                "                                                    <!-- start of right column -->\n" +
                                "                                                    <table width=\"400\" align=\"right\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"devicewidth\" style=\"border-left:solid;border-left-width:10px;border-left-color:#2a2a2a;\">\n" +
                                "                                                        <tbody>\n" +
                                "                                                           \n" +
                                "                                                             <tr style=\"vertical-align:top; text-align:start;\">\n" +
                                "                                                                <td  style=\"padding-left:5px; font-family: Verdana,Helvetica, arial, sans-serif; font-size: 11px; color: #ffffff; text-align: left;\">\n" +
                                "                                                                    <b>Sales: </b>\n" +
                                "                                                                    <br />\n" +
                                "                                                                    +91 98795 25475, +91 97277 75711\n" +
                                "                                                                    <br />\n" +
                                "                                                                    +91 95120 00403, +91 98793 14959 <br />\n" +
                                "                                                                   <b>Support: </b>\n" +
                                "                                                                    <br />\n" +
                                "                                                                    +91 (79) 49117200\n" +
                                "                                                                    <br />\n" +
                                "                                                                    +91 99791 61720, +91 99791 61732<br />\n" +
                                "                                                                    \n" +
                                "                                                                                                                                     \n" +
                                "                                                                    <a href=\"mailto:sales@infozeal.co.in?subject=We Want Demostration Of ePrompt 6 Cloud .net&body=Please Provide Us Demostartion of Software:\" style=\"text-decoration: none; color: #ffffff;\" onclick=\"this.target='_blank'\">sales@infozeal.co.in</a>                                                                  \n" +
                                "                                                                    <br />\n" +
                                "                                                                    <a href=\"http://www.infozeal.co.in/index.php\" style=\"text-decoration: none; color: #ffffff;\" onclick=\"this.target='_blank'\">www.infozeal.co.in</a>                                                           \n" +
                                "                                                                     \n" +
                                "                                                                 </td>                                                                  \n" +
                                "                                                            </tr>\n" +
                                "                                                             <!-- Spacing -->\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td width=\"100%\" height=\"5\" ></td>\n" +
                                "                                                            </tr>\n" +
                                "                                                             <!-- Spacing -->\n" +
                                "                                                             <tr>\n" +
                                "                                                                <td  style=\"font-family: Verdana,Helvetica, arial, sans-serif; font-size: 14px; color: #ffffff; text-align: left;\">\n" +
                                "                                                                   &nbsp;\n" +
                                "                                                                </td>\n" +
                                "                                                            </tr>\n" +
                                "                                                             <tr>\n" +
                                "                                                                <td width=\"100%\" height=\"5\"></td>\n" +
                                "                                                            </tr>\n" +
                                "                                                            <!-- Spacing -->\n" +
                                "                                                                                                                       \n" +
                                "                                                            <!-- Spacing -->\n" +
                                "                                                            <tr>\n" +
                                "                                                                <td width=\"100%\" height=\"10\"></td>\n" +
                                "                                                            </tr>\n" +
                                "                                                            <!-- Spacing -->\n" +
                                "                                                        </tbody>\n" +
                                "                                                    </table>\n" +
                                "                                                    <!-- end of right column -->\n" +
                                "                                                </td>\n" +
                                "                                            </tr>\n" +
                                "                                        </tbody>\n" +
                                "                                    </table>\n" +
                                "                                </td>\n" +
                                "                            </tr>\n" +
                                "                        </tbody>\n" +
                                "                    </table>\n" +
                                "                </td>\n" +
                                "            </tr>\n" +
                                "        </tbody>\n" +
                                "    </table>\n" +
                                "    <!-- end of footer -->\n" +
                                "</body>\n" +
                                "</html>";

                        sendEmailtoUser(EmailID.trim(), EmailSubjectTxt, EmailBodymsgTxt);

                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        NetworkUtils.showNetworkAlertDialog(RegisterActivity.this);
                    }
                })
                .send();

    }

    private void sendEmailtoUser(String emiailid, String mailSubject, String msgBody) {
        BackgroundMail.newBuilder(this)
                .withUsername("sales@infozeal.co.in")
                .withPassword("google@206")
                .withMailTo(emiailid)
                .withSubject(mailSubject)
                .withBody(msgBody)
                .withType("text/html")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {


                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Thank You!");
                        builder.setMessage("We sent you more detail on your email id, check your mailbox.");
                        builder.setPositiveButton(RegisterActivity.this.getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();

                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        NetworkUtils.showNetworkAlertDialog(RegisterActivity.this);
                    }
                })
                .send();

    }

}