package com.score3s.android;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.score3s.android.Constant.APIURL;
import com.score3s.android.Validations.CheckValidate;
import com.score3s.android.asynctasks.CustomProcessbar;
import com.score3s.android.asynctasks.ToastUtils;
import com.score3s.android.fontStyle.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.score3s.android.AddInvoiceActivity.DEFHEADDATA;
import static com.score3s.android.MainActivity.AUTHKEY;

public class ViewinvoiceActivity extends Activity {

    private Button btnShare;
    private PDFView pdfView;
    private static String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Score 3S/";
    JSONArray jsonArrayInvoiceDetails, jsonArrayMRP, jsonArrayUnits, jsonArrayAlternetUnit;
    ImageView btnBack;
    SharedPreferences preferencesDEFHEADDATA;
    SharedPreferences.Editor editorDEFHEADDATA;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    String CompanyName, Salesman, InvoiceDate, InvoiceNum, RouteName, Clientname;
    double Discount = 0, Other = 0, CGST = 0, SGST = 0, IGST = 0, GST = 0, GrossTotal=0, roundoff = 0, totalAmount=0;
    int SrNo = 1;
    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            // Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewinvoice);
        checkPermissions();
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);
        pdfView = findViewById(R.id.pdfView);
        preferencesDEFHEADDATA = getSharedPreferences(DEFHEADDATA, MODE_PRIVATE);
        editorDEFHEADDATA = preferencesDEFHEADDATA.edit();
        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();

        AuthKey = preferencesUserAuthKey.getString("auth", "");
        CompanyName = preferencesUserAuthKey.getString("CompanyName", "");

        getMRP();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(android.R.animator.fade_out, android.R.animator.fade_in);
                finish();
            }
        });


    }

    // END ON Create

    //getMRP
    private void getMRP() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.MRP;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {

                            jsonArrayMRP = jRootObject.getJSONArray("MRPs");
                            getInvoiceDetails();
                            // GetItemUnits("0");

                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();
                            ToastUtils.showErrorToast(ViewinvoiceActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(ViewinvoiceActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(ViewinvoiceActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();
                    ToastUtils.showErrorToast(ViewinvoiceActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    //getinvoice
    private void getInvoiceDetails() {
        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.GETINVOICEBYID;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("InvId", getIntent().getStringExtra("id"));

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {

                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            if (ErrorMessage.equalsIgnoreCase("")) {

                                jsonArrayInvoiceDetails = jRootObject.getJSONArray("InvoiceDetails");
                                JSONObject jobj = jRootObject.getJSONObject("InvoiceHeads");

                                roundoff = CheckValidate.checkemptyDouble(jobj.getString("RoundOff"));
                                totalAmount = CheckValidate.checkemptyDouble(jobj.getString("Amount"));

                                JSONObject jsonObject = jRootObject.getJSONObject("InvoiceHeads");
                                SimpleDateFormat sd1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date dt = sd1.parse(jsonObject.getString("InvDate"));
                                SimpleDateFormat sd2 = new SimpleDateFormat("dd-MM-yyyy");
                                InvoiceNum = jsonObject.getString("InvNo");
                                InvoiceDate = sd2.format(dt);

                                Salesman = jsonObject.getString("SalesMan");
                                Clientname = jsonObject.getString("Client");
                                RouteName = jsonObject.getString("RouteName");

                                //GeneratePdf();
                                GenerateSalesOrder();
                            }


                            CustomProcessbar.hideProcessBar();

                        } else {

                            CustomProcessbar.hideProcessBar();
                            ToastUtils.showErrorToast(ViewinvoiceActivity.this, "Error " + ErrorMessage);
                        }

                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(ViewinvoiceActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(ViewinvoiceActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();
                    ToastUtils.showErrorToast(ViewinvoiceActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });
    }

    //Calculate
    private void calculateSum() {
        Discount = 0;
        double disc = 0, disc1 = 0, disc2 = 0;
        double other1 = 0, other2 = 0;
        double cgstin = 0, sgstin = 0, igstin = 0;
        double gross = 0;

        for (int i = 0; i < jsonArrayInvoiceDetails.length(); i++) {
            try {
                JSONObject jobj = jsonArrayInvoiceDetails.getJSONObject(i);

                double grossAmt = Double.parseDouble(jobj.getString("Gross"));
                gross = gross + grossAmt;

                double discamt = Double.parseDouble(jobj.getString("Discount"));
                disc = disc + discamt;
                double discamt1 = Double.parseDouble(jobj.getString("DiscountII"));
                disc1 = disc1 + discamt1;
                double discamt2 = Double.parseDouble(jobj.getString("DiscountIII"));
                disc2 = disc2 + discamt2;

                double otheramt1 = Double.parseDouble(jobj.getString("Other"));
                other1 = other1 + otheramt1;
                double otheramt2 = Double.parseDouble(jobj.getString("OtherII"));
                other2 = other2 + otheramt2;

                double cgstamt = Double.parseDouble(jobj.getString("CGSTAmt"));
                cgstin = cgstin + cgstamt;
                double sgstamt = Double.parseDouble(jobj.getString("SGSTAmt"));
                sgstin = sgstin + sgstamt;

                double igstamt = Double.parseDouble(jobj.getString("IGSTAmt"));
                igstin = igstin + igstamt;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String tmpGross="";
        tmpGross = String.format("%.2f", gross);
        GrossTotal = CheckValidate.checkemptyDouble(tmpGross);

        String tmpdisc = "", tmpdisc1 = "", tmpdisc2 = "";
        tmpdisc = String.format("%.2f", disc);
        tmpdisc1 = String.format("%.2f", disc1);
        tmpdisc2 = String.format("%.2f", disc2);
        Discount = CheckValidate.checkemptyDouble(tmpdisc) + CheckValidate.checkemptyDouble(tmpdisc1) + CheckValidate.checkemptyDouble(tmpdisc2);

        String tmpother1 = "", tmpother2 = "";
        tmpother1 = String.format("%.2f", other1);
        tmpother2 = String.format("%.2f", other2);
        Other = CheckValidate.checkemptyDouble(tmpother1) + CheckValidate.checkemptyDouble(tmpother2);

        String tmpGST = "", tmpcgst = "", tmpsgst = "";
        if (igstin > 0) {
            tmpGST = String.format("%.2f", igstin);
            GST = CheckValidate.checkemptyDouble(tmpGST);
        } else {
            tmpcgst = String.format("%.2f", cgstin);
            tmpsgst = String.format("%.2f", sgstin);
            GST = CheckValidate.checkemptyDouble(tmpcgst) + CheckValidate.checkemptyDouble(tmpsgst);
        }

    }

    //for remove special symbol
    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number.trim();
    }

    //draw pageborder event
    public class BlackBorder extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            Rectangle rect = new Rectangle(10, 2, 585, 840);//document.getPageSize();
            rect.setBorder(Rectangle.BOX); // left, right, top, bottom border
            rect.setBorderWidth(1); // a width of 5 user units
            rect.setBorderColor(BaseColor.BLACK); // a black border
            rect.setUseVariableBorders(true); // the full width will be visible
            canvas.rectangle(rect);

            /*Rectangle rect= new Rectangle(10, 2, 585, 840); // you can resize rectangle
            rect.enableBorderSide(1);
            rect.enableBorderSide(2);
            rect.enableBorderSide(4);
            rect.enableBorderSide(8);
            rect.setBorderColor(BaseColor.BLACK);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(1);
            document.add(rect);*/
        }
    }

    class CustomBorder implements PdfPCellEvent {
        protected LineDash left;
        protected LineDash right;
        protected LineDash top;
        protected LineDash bottom;

        public CustomBorder(LineDash left, LineDash right,
                            LineDash top, LineDash bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        public void cellLayout(PdfPCell cell, Rectangle position,
                               PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            if (top != null) {
                canvas.saveState();
                top.applyLineDash(canvas);
                canvas.moveTo(position.getRight(), position.getTop());
                canvas.lineTo(position.getLeft(), position.getTop());
                canvas.stroke();
                canvas.restoreState();
            }
            if (bottom != null) {
                canvas.saveState();
                bottom.applyLineDash(canvas);
                canvas.moveTo(position.getRight(), position.getBottom());
                canvas.lineTo(position.getLeft(), position.getBottom());
                canvas.stroke();
                canvas.restoreState();
            }
            if (right != null) {
                canvas.saveState();
                right.applyLineDash(canvas);
                canvas.moveTo(position.getRight(), position.getTop());
                canvas.lineTo(position.getRight(), position.getBottom());
                canvas.stroke();
                canvas.restoreState();
            }
            if (left != null) {
                canvas.saveState();
                left.applyLineDash(canvas);
                canvas.moveTo(position.getLeft(), position.getTop());
                canvas.lineTo(position.getLeft(), position.getBottom());
                canvas.stroke();
                canvas.restoreState();
            }
        }
    }

    interface LineDash {
        public void applyLineDash(PdfContentByte canvas);
    }

    class SolidLine implements LineDash {
        public void applyLineDash(PdfContentByte canvas) {
            canvas.setColorStroke(GrayColor.GRAYBLACK);
        }
    }

    class DottedLine implements LineDash {
        public void applyLineDash(PdfContentByte canvas) {
            canvas.setLineCap(PdfContentByte.LINE_CAP_ROUND);
            canvas.setLineDash(0, 4, 2);
            canvas.setColorStroke(GrayColor.GRAYBLACK);
        }
    }

    class DashedLine implements LineDash {
        public void applyLineDash(PdfContentByte canvas) {
            canvas.setLineDash(3, 3);
            canvas.setColorStroke(GrayColor.GRAYBLACK);
        }
    }

    class UDFDashedLine implements LineDash {
        float unitsOn;
        float phase;

        public UDFDashedLine(float unitsOn, float phase) {
            this.unitsOn = unitsOn;
            this.phase = phase;
        }

        public void applyLineDash(PdfContentByte canvas) {
            canvas.setLineDash(unitsOn, phase);
        }
    }

    //CreatePDFUsingPDFtable
    private void GenerateSalesOrder() {
        calculateSum();

        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }

        String FILENAME = directory_path + getOnlyDigits(InvoiceNum) + ".pdf";

        Document document = new Document(PageSize.A4, 1.5f, 1.5f, 0f, 0f);

        try {

            File chkfile = new File(FILENAME);
            if (chkfile.exists()) {
                chkfile.delete();
            }

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILENAME));
            document.addTitle(FILENAME + Clientname.trim());
            document.addAuthor(CompanyName.trim());
            document.addSubject("This example shows how to add metadata");
            document.addKeywords("Sales order" + Clientname.trim() + FILENAME);
            document.addCreator("Score 3s mobile application");
            document.open();

           /* BlackBorder event = new BlackBorder();
            writer.setPageEvent(event);*/

            LineDash solid = new SolidLine();
            LineDash dotted = new DottedLine();
            LineDash dashed = new DashedLine();

            Font companyName = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, GrayColor.GRAYBLACK);

            Font BoldItalic = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLDITALIC, GrayColor.GRAYBLACK);
            Font ItalicFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, GrayColor.GRAYBLACK);
            Font BoldFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, GrayColor.GRAYBLACK);
            Font NormalFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GrayColor.GRAYBLACK);

            // Creating a parent table
            float[] pointColumnWidthsParent = {750F, 250F};
            PdfPTable parentTable = new PdfPTable(pointColumnWidthsParent);
            parentTable.setWidthPercentage(96);
            PdfPCell Parentcell;

            Parentcell = new PdfPCell(new Phrase(CompanyName.trim(), companyName));
            Parentcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Parentcell.setColspan(2);
            Parentcell.setPadding(10);
            Parentcell.setBorder(PdfPCell.NO_BORDER);
            Parentcell.setCellEvent(new CustomBorder(null, null, null, dashed));
            parentTable.addCell(Parentcell);


            Paragraph headCustomersection = new Paragraph();
            headCustomersection.add(new Chunk("To", ItalicFont));
            headCustomersection.add(new Chunk("\n" + Clientname.trim(), BoldFont));
            headCustomersection.add(new Chunk("\n" + RouteName.trim(), NormalFont));

            Parentcell = new PdfPCell(headCustomersection);
            Parentcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            Parentcell.setPaddingTop(5);
            Parentcell.setPaddingBottom(5);
            Parentcell.setPaddingRight(5);
            Parentcell.setBorder(PdfPCell.NO_BORDER);
            Parentcell.setCellEvent(new CustomBorder(null, dotted, null, null));
            parentTable.addCell(Parentcell);

            //invoice section table
            PdfPTable invoiceHead = new PdfPTable(2);
            PdfPCell invoiceHeadcell;


            invoiceHeadcell = new PdfPCell(new Phrase("Sales Order", BoldFont));
            invoiceHeadcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            invoiceHeadcell.setPadding(5);
            invoiceHeadcell.setColspan(2);
            invoiceHeadcell.setBorder(PdfPCell.NO_BORDER);
            invoiceHeadcell.setCellEvent(new CustomBorder(null, null, null, dotted));
            invoiceHead.addCell(invoiceHeadcell);


            invoiceHeadcell = new PdfPCell(new Phrase(InvoiceNum.trim(), BoldFont));
            invoiceHeadcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            invoiceHeadcell.setPadding(5);
            invoiceHeadcell.setBorder(PdfPCell.NO_BORDER);
            invoiceHeadcell.setCellEvent(new CustomBorder(null, null, null, dotted));
            invoiceHead.addCell(invoiceHeadcell);

            invoiceHeadcell = new PdfPCell(new Phrase(InvoiceDate, BoldFont));
            invoiceHeadcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invoiceHeadcell.setPadding(5);
            invoiceHeadcell.setBorder(PdfPCell.NO_BORDER);
            invoiceHeadcell.setCellEvent(new CustomBorder(null, null, null, dotted));
            invoiceHead.addCell(invoiceHeadcell);

            invoiceHeadcell = new PdfPCell(new Phrase("Salesman("+Salesman+")", BoldItalic));
            invoiceHeadcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            invoiceHeadcell.setPadding(5);
            invoiceHeadcell.setBorder(PdfPCell.NO_BORDER);
            invoiceHeadcell.setColspan(2);
            invoiceHead.addCell(invoiceHeadcell);

            //add parent to invoice Head
            Parentcell = new PdfPCell(invoiceHead);
            Parentcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            Parentcell.setBorder(PdfPCell.NO_BORDER);
            Parentcell.setBackgroundColor(GrayColor.LIGHT_GRAY);
            Parentcell.setCellEvent(new CustomBorder(dashed, null, null, null));
            parentTable.addCell(Parentcell);

            document.add(parentTable);
            //create InvoiceDetail table
            float[] pointColumnWidths = {50F, 350F, 100F, 100F, 100F, 100F, 200F};

            PdfPTable table = new PdfPTable(pointColumnWidths);
            PdfPCell invdetailcell;
            table.setWidthPercentage(96);

            for (int i = 0; i < 1; i++) {

                invdetailcell = new PdfPCell(new Phrase("Sr.", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBackgroundColor(new GrayColor(0.75f));
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("Item Name", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBackgroundColor(new GrayColor(0.75f));
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("MRP", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBackgroundColor(new GrayColor(0.75f));
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("Qty", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBackgroundColor(new GrayColor(0.75f));
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("Unit", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBackgroundColor(new GrayColor(0.75f));
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("Rate", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBackgroundColor(new GrayColor(0.75f));
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("Total", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBackgroundColor(new GrayColor(0.75f));
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, null, dashed, dashed));
                table.addCell(invdetailcell);
            }
            table.setHeaderRows(1);

            //itemdetail fill
            PdfPCell itemdetailcell;
            for (int i = 0; i < jsonArrayInvoiceDetails.length(); i++) {
                try {


                    itemdetailcell = new PdfPCell(new Phrase(String.valueOf(SrNo), NormalFont));
                    itemdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    itemdetailcell.setPadding(5);
                    itemdetailcell.setUseAscender(true);
                    itemdetailcell.setUseDescender(true);
                    itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                    itemdetailcell.setCellEvent(new CustomBorder(null, dashed, null, dotted));
                    table.addCell(itemdetailcell);


                    for (int j = 0; j < jsonArrayMRP.length(); j++) {
                        try {
                            if (jsonArrayMRP.getJSONObject(j).getString("ItemId").equals(jsonArrayInvoiceDetails.getJSONObject(i).getString("ItemId")) && jsonArrayMRP.getJSONObject(j).getString("MRPId").equals(jsonArrayInvoiceDetails.getJSONObject(i).getString("MRPId"))) {

                                itemdetailcell = new PdfPCell(new Phrase(jsonArrayMRP.getJSONObject(j).getString("ItemName"),NormalFont));
                                itemdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                itemdetailcell.setPadding(5);
                                itemdetailcell.setUseAscender(true);
                                itemdetailcell.setUseDescender(true);
                                itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                                itemdetailcell.setCellEvent(new CustomBorder(null, dashed, null, dotted));
                                table.addCell(itemdetailcell);

                                itemdetailcell = new PdfPCell(new Phrase(String.format("%.2f",CheckValidate.checkemptyDouble(jsonArrayMRP.getJSONObject(j).getString("ItemMRP"))),NormalFont));
                                itemdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                itemdetailcell.setPadding(5);
                                itemdetailcell.setUseAscender(true);
                                itemdetailcell.setUseDescender(true);
                                itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                                itemdetailcell.setCellEvent(new CustomBorder(null, dashed, null, dotted));
                                table.addCell(itemdetailcell);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(CheckValidate.checkemptyDouble(jsonArrayInvoiceDetails.getJSONObject(i).getString("AlternetUnitQty")) > 0 && CheckValidate.checkemptyDouble(jsonArrayInvoiceDetails.getJSONObject(i).getString("PrimaryUnitQty")) <= 0)
                    {
                        double doubleNumber = CheckValidate.checkemptyDouble(jsonArrayInvoiceDetails.getJSONObject(i).getString("AlternetUnitQty"));
                        int intPart = (int) doubleNumber;
                        if((doubleNumber - intPart) > 0 || (doubleNumber - intPart) < 0 )
                        {
                            itemdetailcell = new PdfPCell(new Phrase(String.valueOf(CheckValidate.checkemptyDouble(jsonArrayInvoiceDetails.getJSONObject(i).getString("AlternetUnitQty"))),NormalFont));
                        }else{
                            itemdetailcell = new PdfPCell(new Phrase(String.valueOf(intPart),NormalFont));
                        }

                        itemdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        itemdetailcell.setPadding(5);
                        itemdetailcell.setUseAscender(true);
                        itemdetailcell.setUseDescender(true);
                        itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                        itemdetailcell.setCellEvent(new CustomBorder(null, dashed, null, dotted));
                        table.addCell(itemdetailcell);

                        itemdetailcell = new PdfPCell(new Phrase(CheckValidate.checkemptystring(jsonArrayInvoiceDetails.getJSONObject(i).getString("AlternetUnit")),NormalFont));
                        itemdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        itemdetailcell.setPadding(5);
                        itemdetailcell.setUseAscender(true);
                        itemdetailcell.setUseDescender(true);
                        itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                        itemdetailcell.setCellEvent(new CustomBorder(null, dashed, null, dotted));
                        table.addCell(itemdetailcell);
                    }
                    else
                    {
                        double doubleNumber = CheckValidate.checkemptyDouble(jsonArrayInvoiceDetails.getJSONObject(i).getString("TotalQty"));
                        int intPart = (int) doubleNumber;
                        if((doubleNumber - intPart) > 0 || (doubleNumber - intPart) < 0 )
                        {
                            itemdetailcell = new PdfPCell(new Phrase(String.valueOf(CheckValidate.checkemptyDouble(jsonArrayInvoiceDetails.getJSONObject(i).getString("TotalQty"))),NormalFont));
                        }else{
                            itemdetailcell = new PdfPCell(new Phrase(String.valueOf(intPart),NormalFont));
                        }
                        itemdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        itemdetailcell.setPadding(5);
                        itemdetailcell.setUseAscender(true);
                        itemdetailcell.setUseDescender(true);
                        itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                        itemdetailcell.setCellEvent(new CustomBorder(null, dashed, null, dotted));
                        table.addCell(itemdetailcell);

                        itemdetailcell = new PdfPCell(new Phrase(CheckValidate.checkemptystring(jsonArrayInvoiceDetails.getJSONObject(i).getString("PrimaryUnit")),NormalFont));
                        itemdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        itemdetailcell.setPadding(5);
                        itemdetailcell.setUseAscender(true);
                        itemdetailcell.setUseDescender(true);
                        itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                        itemdetailcell.setCellEvent(new CustomBorder(null, dashed, null, dotted));
                        table.addCell(itemdetailcell);
                    }

                    itemdetailcell = new PdfPCell(new Phrase(String.format("%.2f",CheckValidate.checkemptyDouble(jsonArrayInvoiceDetails.getJSONObject(i).getString("Rate"))),NormalFont));
                    itemdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    itemdetailcell.setPadding(5);
                    itemdetailcell.setUseAscender(true);
                    itemdetailcell.setUseDescender(true);
                    itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                    itemdetailcell.setCellEvent(new CustomBorder(null, dashed, null, dotted));
                    table.addCell(itemdetailcell);


                    itemdetailcell = new PdfPCell(new Phrase(String.format("%.2f",CheckValidate.checkemptyDouble(jsonArrayInvoiceDetails.getJSONObject(i).getString("Gross"))),NormalFont));
                    itemdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    itemdetailcell.setPadding(5);
                    itemdetailcell.setUseAscender(true);
                    itemdetailcell.setUseDescender(true);
                    itemdetailcell.setBorder(PdfPCell.NO_BORDER);
                    itemdetailcell.setCellEvent(new CustomBorder(null, null, null, dotted));
                    table.addCell(itemdetailcell);

                    SrNo++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //set last raw of invoice table invoice detail
            for (int i = 0; i < 1; i++) {

                invdetailcell = new PdfPCell(new Phrase("", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase("", BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, dashed, dashed, dashed));
                table.addCell(invdetailcell);

                invdetailcell = new PdfPCell(new Phrase(String.format("%.2f",GrossTotal), BoldFont));
                invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                invdetailcell.setPadding(5);
                invdetailcell.setUseAscender(true);
                invdetailcell.setUseDescender(true);
                invdetailcell.setBorder(PdfPCell.NO_BORDER);
                invdetailcell.setCellEvent(new CustomBorder(null, null, dashed, dashed));
                table.addCell(invdetailcell);
            }

            //Blank space
            invdetailcell = new PdfPCell(new Phrase(" ", NormalFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invdetailcell.setColspan(4);
            invdetailcell.setRowspan(4);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(invdetailcell);

            //Discount
            invdetailcell = new PdfPCell(new Phrase("Less Discount ", ItalicFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            invdetailcell.setColspan(2);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPaddingTop(2);
            invdetailcell.setPaddingBottom(0);
            invdetailcell.setPaddingRight(5);
            invdetailcell.setPaddingLeft(5);
            table.addCell(invdetailcell);

            invdetailcell = new PdfPCell(new Phrase(String.format("%.2f",Discount), NormalFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invdetailcell.setColspan(1);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPaddingTop(2);
            invdetailcell.setPaddingBottom(0);
            invdetailcell.setPaddingRight(5);
            invdetailcell.setPaddingLeft(5);
            table.addCell(invdetailcell);

            //Other
            invdetailcell = new PdfPCell(new Phrase("Add Other Charges", ItalicFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            invdetailcell.setColspan(2);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPaddingTop(2);
            invdetailcell.setPaddingBottom(0);
            invdetailcell.setPaddingRight(5);
            invdetailcell.setPaddingLeft(5);
            table.addCell(invdetailcell);

            invdetailcell = new PdfPCell(new Phrase(String.format("%.2f",Other), NormalFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invdetailcell.setColspan(1);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPaddingTop(2);
            invdetailcell.setPaddingBottom(0);
            invdetailcell.setPaddingRight(5);
            invdetailcell.setPaddingLeft(5);
            table.addCell(invdetailcell);

            //GST
            invdetailcell = new PdfPCell(new Phrase("Add Total GST", ItalicFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            invdetailcell.setColspan(2);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPaddingTop(2);
            invdetailcell.setPaddingBottom(0);
            invdetailcell.setPaddingRight(5);
            invdetailcell.setPaddingLeft(5);
            table.addCell(invdetailcell);

            invdetailcell = new PdfPCell(new Phrase(String.format("%.2f",GST), NormalFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invdetailcell.setColspan(1);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPaddingTop(2);
            invdetailcell.setPaddingBottom(0);
            invdetailcell.setPaddingRight(5);
            invdetailcell.setPaddingLeft(5);
            table.addCell(invdetailcell);

            //Roundoff
            invdetailcell = new PdfPCell(new Phrase("Round Off", ItalicFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            invdetailcell.setColspan(2);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPaddingTop(2);
            invdetailcell.setPaddingBottom(2);
            invdetailcell.setPaddingRight(5);
            invdetailcell.setPaddingLeft(5);
            table.addCell(invdetailcell);

            invdetailcell = new PdfPCell(new Phrase(String.format("%.2f",roundoff), NormalFont));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invdetailcell.setColspan(1);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPaddingTop(2);
            invdetailcell.setPaddingBottom(2);
            invdetailcell.setPaddingRight(5);
            invdetailcell.setPaddingLeft(5);
            table.addCell(invdetailcell);

            //Total last raw
            Font f = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, GrayColor.GRAYBLACK);

            invdetailcell = new PdfPCell(new Phrase("", f));
            invdetailcell.setBackgroundColor(GrayColor.LIGHT_GRAY);
            invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPadding(5);
            invdetailcell.setCellEvent(new CustomBorder(null, null, dashed, dashed));
            invdetailcell.setColspan(4);
            table.addCell(invdetailcell);

            invdetailcell = new PdfPCell(new Phrase("Net Total", f));
            invdetailcell.setBackgroundColor(new GrayColor(0.75f));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPadding(5);
            invdetailcell.setCellEvent(new CustomBorder(null, null, dashed, dashed));
            invdetailcell.setColspan(2);
            table.addCell(invdetailcell);

            invdetailcell = new PdfPCell(new Phrase(String.format("%.2f",totalAmount),f));
            invdetailcell.setBackgroundColor(new GrayColor(0.75f));
            invdetailcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            invdetailcell.setBorder(PdfPCell.NO_BORDER);
            invdetailcell.setPadding(5);
            invdetailcell.setCellEvent(new CustomBorder(null, null, dashed, dashed));
            invdetailcell.setColspan(1);
            table.addCell(invdetailcell);

            document.add(table);


            //table for sign portion
            float[] pointColumnWidthstermANDsigntable = {80F,240F, 260F, 420F};
            PdfPTable termANDsigntable = new PdfPTable(pointColumnWidthstermANDsigntable);
            termANDsigntable.setWidthPercentage(96);
            PdfPCell termANDsignCell;

            //first row
            termANDsignCell = new PdfPCell(new Phrase("Terms:", BoldFont));
            termANDsignCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            termANDsignCell.setBorder(PdfPCell.NO_BORDER);
            termANDsignCell.setColspan(1);
            termANDsigntable.addCell(termANDsignCell);

            termANDsignCell = new PdfPCell(new Phrase("# All Amount in Indian Rupees.\n# Kindly check all details carefully.\n# This is not final invoice.", NormalFont));
            termANDsignCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            termANDsignCell.setBorder(PdfPCell.NO_BORDER);
            termANDsignCell.setColspan(2);
            termANDsigntable.addCell(termANDsignCell);

            termANDsignCell = new PdfPCell(new Phrase("for " + CompanyName.trim(), BoldFont));
            termANDsignCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            termANDsignCell.setBorder(PdfPCell.NO_BORDER);
            termANDsignCell.setColspan(1);
            termANDsigntable.addCell(termANDsignCell);

            //forSpace
            termANDsignCell = new PdfPCell(new Phrase(" ", NormalFont));
            termANDsignCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            termANDsignCell.setBorder(PdfPCell.NO_BORDER);
            termANDsignCell.setColspan(4);
            termANDsignCell.setPadding(10);
            termANDsigntable.addCell(termANDsignCell);
            //sign row
            termANDsignCell = new PdfPCell(new Phrase("E. & O. E.", NormalFont));
            termANDsignCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            termANDsignCell.setBorder(PdfPCell.NO_BORDER);
            termANDsignCell.setColspan(1);
            termANDsigntable.addCell(termANDsignCell);

            termANDsignCell = new PdfPCell(new Phrase("Receiver's Signature", NormalFont));
            termANDsignCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            termANDsignCell.setBorder(PdfPCell.NO_BORDER);
            termANDsignCell.setPaddingRight(20);
            termANDsignCell.setColspan(2);
            termANDsigntable.addCell(termANDsignCell);

            termANDsignCell = new PdfPCell(new Phrase("Authorized Signatory", NormalFont));
            termANDsignCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            termANDsignCell.setBorder(PdfPCell.NO_BORDER);
            termANDsignCell.setColspan(1);
            termANDsigntable.addCell(termANDsignCell);


            //Last row
            termANDsignCell = new PdfPCell(new Phrase("This is a Computer generated document and does not require any signature.",NormalFont));
            termANDsignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            termANDsignCell.setBorder(PdfPCell.NO_BORDER);
            termANDsignCell.setPadding(2);
            termANDsignCell.setCellEvent(new CustomBorder(null, null, dashed, null));
            termANDsignCell.setColspan(4);
            termANDsigntable.addCell(termANDsignCell);
            document.add(termANDsigntable);




            //last page footer
            /*PdfPTable datatablebottom = new PdfPTable(1);
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("This is a Computer generated document and does not require any signature.",NormalFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPaddingBottom(5);
            cell.setCellEvent(new CustomBorder(null, null, dashed, null));
            datatablebottom.addCell(cell);
            datatablebottom.setTotalWidth(document.right(document.rightMargin())
                    - document.left(document.leftMargin()));

            datatablebottom.writeSelectedRows(0, -1,
                    document.left(document.leftMargin()),
                    datatablebottom.getTotalHeight() + document.bottom(document.bottomMargin()),
                    writer.getDirectContent());*/

            document.close(); // no need to close PDFwriter?

            ViewPdf();

        } catch (DocumentException e) {
            Toast.makeText(ViewinvoiceActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Toast.makeText(ViewinvoiceActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    //View PDF
    private void ViewPdf() {

        String targetPdf = directory_path + getOnlyDigits(InvoiceNum) + ".pdf";
        File chkfile = new File(targetPdf);
        if (!chkfile.exists()) {
            ShowAlert.ShowAlert(ViewinvoiceActivity.this, "Not Found!", "Sorry file not generated, something going wrong! ");
            return;
        }
        File outputFile = new File(targetPdf);
        Uri uri = FileProvider.getUriForFile(ViewinvoiceActivity.this, ViewinvoiceActivity.this.getPackageName() + ".provider", outputFile);

        pdfView.fromUri(uri)
                .defaultPage(1)
                .swipeHorizontal(true).load();
    }

    //Share code
    private void openWhatsApp() {
        try {

            String targetPdf = directory_path + getOnlyDigits(InvoiceNum) + ".pdf";

            File chkfile = new File(targetPdf);
            if (!chkfile.exists()) {
                ShowAlert.ShowAlert(ViewinvoiceActivity.this, "Not Found!", "Sorry file not found! ");
                return;
            }
            File outputFile = new File(targetPdf);
            Uri uri = FileProvider.getUriForFile(ViewinvoiceActivity.this, ViewinvoiceActivity.this.getPackageName() + ".provider", outputFile);

            String smsNumber = "";
            String toemailid = "";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toemailid});
            //sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER,smsNumber);
            //sendIntent.setType("text/plain");
            sendIntent.setType("text/plain|application/pdf");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Order Number is "+ InvoiceNum.trim());
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Please find attachment");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

            //// sendIntent.setPackage("com.whatsapp");
            if (sendIntent.resolveActivity(ViewinvoiceActivity.this.getPackageManager()) == null) {
                Toast.makeText(this, "Error/n" + "No Whatsapp found !", Toast.LENGTH_SHORT).show();
                return;
            }
            ////ViewinvoiceActivity.this.startActivity(sendIntent);

            ViewinvoiceActivity.this.startActivity(Intent.createChooser(sendIntent,
                    "Send via which Application?"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ViewinvoiceActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                //initialize();
                break;
        }
    }


    //Generatecode
    /*private void GeneratePdf() {
        calculateSum();

        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }

        String FILENAME = directory_path + getOnlyDigits(InvoiceNum) + ".pdf";

        Document document = new Document(PageSize.A4, 1.5f, 1.5f, 0f, 0f);

        try {

            File chkfile = new File(FILENAME);
            if (chkfile.exists()) {
                chkfile.delete();
            }

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILENAME));
            document.addTitle(FILENAME + Clientname.trim());
            document.addAuthor(CompanyName.trim());
            document.addSubject("This example shows how to add metadata");
            document.addKeywords("Sales order" + Clientname.trim() + FILENAME);
            document.addCreator("Score 3s mobile application");
            document.open();

            BlackBorder event = new BlackBorder();
            writer.setPageEvent(event);

            Paragraph headsection = new Paragraph();
            //headsection.setSpacingBefore(10);
            headsection.setAlignment(Element.ALIGN_CENTER);
            headsection.setIndentationLeft(10);
            headsection.setIndentationRight(10);

            Chunk chunkSales = new Chunk("\nSales Order", FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLDITALIC, BaseColor.LIGHT_GRAY));
            chunkSales.setTextRenderMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 0.5f, BaseColor.BLACK);
            headsection.add(chunkSales);
            headsection.add(new Chunk("\n\n" + CompanyName, FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLD)));

            document.add(headsection);

            Chunk glue = new Chunk(new VerticalPositionMark());

            Paragraph headCustomersection = new Paragraph();
            headCustomersection.setSpacingAfter(10);
            headCustomersection.setSpacingBefore(10);
            headCustomersection.setAlignment(Element.ALIGN_LEFT);
            headCustomersection.setIndentationLeft(10);
            headCustomersection.setIndentationRight(10);
            headCustomersection.add(new Chunk(new LineSeparator(0.5f, 100, null, 0, -5)));
            headCustomersection.add(new Chunk("\n\nTo,", FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLDITALIC)));
            headCustomersection.add(new Chunk("\n" + Clientname, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD)));
            headCustomersection.add(new Chunk("\n" + RouteName, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD)));
            // headCustomersection.add(new Chunk("\nB-38,Chandralok Society, Nr.Gobri Road,\nPalanpur.385001", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)));
            // headCustomersection.add(new Chunk("\nGSTIN:24AAJSIJDIII1Z", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)));

            document.add(headCustomersection);

            Paragraph subheadsection = new Paragraph();
            subheadsection.setAlignment(Element.ALIGN_LEFT);
            subheadsection.setIndentationLeft(10);
            subheadsection.setIndentationRight(10);
            subheadsection.add(new Chunk("Order Number: ", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)));
            subheadsection.add(new Chunk(InvoiceNum, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)));
            subheadsection.add(new Chunk(glue));
            subheadsection.add(new Chunk("Order Date: ", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)));
            subheadsection.add(new Chunk(InvoiceDate + "\n\n", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)));
            document.add(subheadsection);

            // document.add(new Chunk(Chunk.NEWLINE));

            // Creating a table
            float[] pointColumnWidths = {50F, 150F, 125F, 125F, 150F};

            PdfPTable table = new PdfPTable(pointColumnWidths);
            table.setWidthPercentage(96);
            table.getDefaultCell().setUseAscender(true);
            table.getDefaultCell().setUseDescender(true);

            table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            for (int i = 0; i < 1; i++) {
                table.addCell("Sr.No.");
                table.addCell("Item");
                table.addCell("Qty");
                table.addCell("Rate");
                table.addCell("Total");
            }
            table.setHeaderRows(1);

            table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            int SrNo = 1;
            for (int i = 0; i < jsonArrayInvoiceDetails.length(); i++) {
                try {
                    table.addCell(String.valueOf(SrNo));

                    for (int j = 0; j < jsonArrayMRP.length(); j++) {
                        try {
                            if (jsonArrayMRP.getJSONObject(j).getString("ItemId").equals(jsonArrayInvoiceDetails.getJSONObject(i).getString("ItemId")) && jsonArrayMRP.getJSONObject(j).getString("MRPId").equals(jsonArrayInvoiceDetails.getJSONObject(i).getString("MRPId"))) {
                                table.addCell(jsonArrayMRP.getJSONObject(j).getString("ItemName"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    table.addCell(jsonArrayInvoiceDetails.getJSONObject(i).getString("TotalQty"));
                    table.addCell(jsonArrayInvoiceDetails.getJSONObject(i).getString("Rate"));
                    table.addCell(jsonArrayInvoiceDetails.getJSONObject(i).getString("Gross"));
                    SrNo++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Font fdo = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, GrayColor.GRAYBLACK);
            Font fdoTitle = new Font(Font.FontFamily.HELVETICA, 13, Font.ITALIC, GrayColor.GRAYBLACK);

            //Discount
            PdfPCell cellspaceDisc = new PdfPCell(new Phrase("", fdo));
            cellspaceDisc.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellspaceDisc.setColspan(3);
            cellspaceDisc.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellspaceDisc);

            PdfPCell cellDisc = new PdfPCell(new Phrase("Less Discount ", fdoTitle));
            cellDisc.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellDisc.setColspan(1);
            cellDisc.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellDisc);

            PdfPCell cellDiscAmt = new PdfPCell(new Phrase(String.valueOf(Discount), fdo));
            cellDiscAmt.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellDiscAmt.setColspan(1);
            cellDiscAmt.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellDiscAmt);

            //Other
            PdfPCell cellspaceOther = new PdfPCell(new Phrase("", fdo));
            cellspaceOther.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellspaceOther.setColspan(3);
            cellspaceOther.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellspaceOther);

            PdfPCell cellOther = new PdfPCell(new Phrase("Add Other Charges", fdoTitle));
            cellOther.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellOther.setColspan(1);
            cellOther.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellOther);

            PdfPCell cellOtherAmt = new PdfPCell(new Phrase(String.valueOf(Other), fdo));
            cellOtherAmt.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellOtherAmt.setColspan(1);
            cellOtherAmt.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellOtherAmt);

            //GST
            PdfPCell cellspaceGST = new PdfPCell(new Phrase("", fdo));
            cellspaceGST.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellspaceGST.setColspan(3);
            cellspaceGST.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellspaceGST);

            PdfPCell cellGST = new PdfPCell(new Phrase("Add Total GST", fdoTitle));
            cellGST.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellGST.setColspan(1);
            cellGST.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellGST);

            PdfPCell cellGSTAmt = new PdfPCell(new Phrase(String.valueOf(GST), fdo));
            cellGSTAmt.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellGSTAmt.setColspan(1);
            cellGSTAmt.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellGSTAmt);

            //Roundoff
            PdfPCell cellspaceRoundOff = new PdfPCell(new Phrase("", fdo));
            cellspaceRoundOff.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellspaceRoundOff.setColspan(3);
            cellspaceRoundOff.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellspaceRoundOff);

            PdfPCell cellRoundOff = new PdfPCell(new Phrase("Round Off", fdoTitle));
            cellRoundOff.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellRoundOff.setColspan(1);
            cellRoundOff.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellRoundOff);

            PdfPCell cellRoundOffAmt = new PdfPCell(new Phrase(String.valueOf(roundoff), fdo));
            cellRoundOffAmt.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellRoundOffAmt.setColspan(1);
            cellRoundOffAmt.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cellRoundOffAmt);

            //Total last raw
            Font f = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, GrayColor.GRAYWHITE);
            PdfPCell cell = new PdfPCell(new Phrase("Total Amount", f));
            cell.setBackgroundColor(GrayColor.GRAYBLACK);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(4);
            table.addCell(cell);

            PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(totalAmount), f));
            cell2.setBackgroundColor(GrayColor.GRAYBLACK);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setColspan(1);
            table.addCell(cell2);

            //table.setFooterRows(1);

            // Adding Table to document
            document.add(table);

            Paragraph signpara = new Paragraph();
            signpara.setAlignment(Element.ALIGN_RIGHT);
            signpara.setIndentationLeft(10);
            signpara.setIndentationRight(10);


            signpara.add(new Chunk("for " + CompanyName, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)));

            signpara.add(new Chunk("\n" + Salesman + "   \n", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL)));
            signpara.add(new Chunk(new LineSeparator(0.5f, 100, null, 0, -5)));
            document.add(signpara);

            document.add(new Chunk(Chunk.NEWLINE));

            Paragraph terms = new Paragraph();
            terms.setAlignment(Element.ALIGN_LEFT);
            terms.setIndentationLeft(10);
            terms.setIndentationRight(10);
            terms.add(new Chunk("\n # Valid Invoice only valid.", FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL)));
            terms.add(new Chunk("\n # GST as per goverment norm.", FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL)));
            terms.add(new Chunk("\n # No return policy.", FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL)));

            document.add(terms);

            document.close(); // no need to close PDFwriter?

            ViewPdf();

        } catch (DocumentException e) {
            Toast.makeText(ViewinvoiceActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Toast.makeText(ViewinvoiceActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }*/

    //using abstract method
    /*abstract class CustomBorderAb implements PdfPCellEvent {
        private int border = 0;
        public CustomBorderAb(int border) {
            this.border = border;
        }
        public void cellLayout(PdfPCell cell, Rectangle position,
                               PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.saveState();
            setLineDash(canvas);
            if ((border & PdfPCell.TOP) == PdfPCell.TOP) {
                canvas.moveTo(position.getRight(), position.getTop());
                canvas.lineTo(position.getLeft(), position.getTop());
            }
            if ((border & PdfPCell.BOTTOM) == PdfPCell.BOTTOM) {
                canvas.moveTo(position.getRight(), position.getBottom());
                canvas.lineTo(position.getLeft(), position.getBottom());
            }
            if ((border & PdfPCell.RIGHT) == PdfPCell.RIGHT) {
                canvas.moveTo(position.getRight(), position.getTop());
                canvas.lineTo(position.getRight(), position.getBottom());
            }
            if ((border & PdfPCell.LEFT) == PdfPCell.LEFT) {
                canvas.moveTo(position.getLeft(), position.getTop());
                canvas.lineTo(position.getLeft(), position.getBottom());
            }
            canvas.stroke();
            canvas.restoreState();
        }

        public abstract void setLineDash(PdfContentByte canvas);
    }
    class SolidBorder extends CustomBorderAb {
        public SolidBorder(int border) { super(border); }
        public void setLineDash(PdfContentByte canvas) {}
    }

    class DottedBorder extends CustomBorderAb {
        public DottedBorder(int border) { super(border); }
        public void setLineDash(PdfContentByte canvas) {
            canvas.setLineCap(PdfContentByte.LINE_CAP_ROUND);
            canvas.setLineDash(0, 4, 2);
        }
    }

    class DashedBorder extends CustomBorderAb {
        public DashedBorder(int border) { super(border); }
        public void setLineDash(PdfContentByte canvas) {
            canvas.setLineDash(3, 3);
        }
    }*/
}
