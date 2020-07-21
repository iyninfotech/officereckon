package com.score3s.android;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrollActivity extends Activity {

    private Button btn,Send;
    private LinearLayout llScroll;
    private Bitmap bitmap;

    private BaseFont bfBold;
    private BaseFont bf;
    private int pageNumber = 0;

    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
           // Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        checkPermissions();
        btn = findViewById(R.id.btn);
        Send = findViewById(R.id.Send);
        llScroll = findViewById(R.id.llScroll);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Log.d("size"," "+llScroll.getWidth() +"  "+llScroll.getWidth());
                bitmap = loadBitmapFromView(llScroll, llScroll.getWidth(), llScroll.getHeight());
               // createPdf();
               // createPdfItext();*/

                GeneratePdf();

            }
        });


        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp();

            }
        });

    }
    // END ON Create

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

    private void GeneratePdf()    {
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Mypdf/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String FILENAME =directory_path + "TestPDF.pdf";

        /*Document document = new Document();
        document.setPageSize(PageSize.A4);*/

        Document document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILENAME));

            document.open();
            Phrase space = new Phrase(50);

            //Paragraph paragraphspace = new Paragraph(50);

            Paragraph headsection = new Paragraph();
            headsection.setSpacingAfter(10);
            headsection.setSpacingBefore(10);
            headsection.setAlignment(Element.ALIGN_CENTER);
            headsection.setIndentationLeft(50);
            headsection.setIndentationRight(50);
            headsection.add(new Chunk("\nTax Invoice", FontFactory.getFont(FontFactory.COURIER, 20, Font.BOLDITALIC )));
            headsection.add(new Chunk("\n\n\nInfozeal eSolutions Private Limited", FontFactory.getFont(FontFactory.COURIER, 15, Font.BOLD )));
            headsection.add(new Chunk("\nB-38,Chandralok Society, Nr.Gobri Road,Palanpur.385001",FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL )));
            headsection.add(new Chunk("\nGSTIN:24AAJSIJDIII1Z",FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL )));

            document.add(headsection);

            Paragraph subheadsection = new Paragraph();
            subheadsection.add(new Chunk("\nInvoice Number: ",FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL )));
            subheadsection.add(new Chunk("ITI00000111",FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL )));
            subheadsection.add(new Chunk("\nInvoice Date: ",FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL )));
            subheadsection.add(new Chunk("23/10/2019",FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL )));
            subheadsection.add(new Chunk(Chunk.NEWLINE));
            subheadsection.add(new Chunk(new LineSeparator(0.5f, 100, null, 0, -5)));
            document.add(subheadsection);

            document.add(new Chunk(Chunk.NEWLINE));

            // Creating a table
            float [] pointColumnWidths = {50F, 150F, 125F, 125F , 150F};

            PdfPTable table = new PdfPTable(pointColumnWidths);
            table.setWidthPercentage(100);
            table.getDefaultCell().setUseAscender(true);
            table.getDefaultCell().setUseDescender(true);

            table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
            for (int i = 0; i < 1; i++)
            {
                table.addCell("Sr.No.");
                table.addCell("Item");
                table.addCell("Qty");
                table.addCell("Rate");
                table.addCell("Total");
            }
            table.setHeaderRows(1);

            table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            for (int counter = 1; counter <= 10; counter++)
            {
                table.addCell(String.valueOf(counter));
                table.addCell("item " + counter);
                table.addCell("qty " + counter);
                table.addCell("100 " + counter);
                table.addCell("999 " + counter);
            }

            Font f = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, GrayColor.GRAYWHITE);
            PdfPCell cell = new PdfPCell(new Phrase("Total", f));
            cell.setBackgroundColor(GrayColor.GRAYBLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(4);
            table.addCell(cell);

            PdfPCell cell2 = new PdfPCell(new Phrase("10000", f));
            cell2.setBackgroundColor(GrayColor.GRAYBLACK);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setColspan(1);
            table.addCell(cell2);

            //table.setFooterRows(1);

            // Adding Table to document
            document.add(table);

            document.add(new Chunk(Chunk.NEWLINE));

            Paragraph terms = new Paragraph();
            terms.add(new Chunk(new LineSeparator(0.5f, 100, null, 0, -5)));
            terms.add(new Chunk("\n# Valid Invoice only valid.",FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL )));
            terms.add(new Chunk("\n# GST as per goverment norm.",FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL )));
            terms.add(new Chunk("\n# No return policy.\n",FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL )));
            terms.add(new Chunk(new LineSeparator(0.5f, 100, null, 0, -5)));

            document.add(terms);
            document.add(new Chunk(Chunk.NEWLINE));

            document.close(); // no need to close PDFwriter?

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
       // float width = displaymetrics.widthPixels ;

        float width = 500;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Mypdf/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path+"test.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
            openWhatsApp();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();

        // openGeneratedPDF();

    }

    private void openGeneratedPDF(){
        String directory_path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "test.pdf";
        // String targetPdf = directory_path+"test-2.pdf";

        File file = new File(directory_path);
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(ScrollActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openWhatsApp() {
        try {
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Mypdf/";
            String targetPdf = directory_path + "test.pdf";

            File outputFile = new File(targetPdf);
            Uri uri = FileProvider.getUriForFile(ScrollActivity.this, ScrollActivity.this.getPackageName() + ".provider", outputFile);

            String smsNumber = "916356154321";
            String toemailid = "se.pradeepmodi@gmail.com";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {toemailid });
            //sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER,smsNumber);
            //sendIntent.setType("text/plain");
            sendIntent.setType("text/plain|application/pdf");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Invoice is");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Please find attachment");
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

            //// sendIntent.setPackage("com.whatsapp");
            if (sendIntent.resolveActivity(ScrollActivity.this.getPackageManager()) == null) {
                Toast.makeText(this, "Error/n" +"No Whatsapp found !", Toast.LENGTH_SHORT).show();
                return;
            }
            ////ScrollActivity.this.startActivity(sendIntent);

            ScrollActivity.this.startActivity(Intent.createChooser(sendIntent,
                    "Send via which Application?"));
        }
        catch(ActivityNotFoundException e)
        {
            Toast.makeText(ScrollActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void openWhatsAppNew() {
        try {
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Mypdf/";
            String targetPdf = directory_path + "test.pdf";

            File outputFile = new File(targetPdf);
            Uri uri = FileProvider.getUriForFile(ScrollActivity.this, ScrollActivity.this.getPackageName() + ".provider", outputFile);


        }
        catch(ActivityNotFoundException e)
        {
            Toast.makeText(ScrollActivity.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }



}
