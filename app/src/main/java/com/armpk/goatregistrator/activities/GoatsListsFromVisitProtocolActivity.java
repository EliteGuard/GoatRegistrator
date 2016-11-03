package com.armpk.goatregistrator.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.adapters.GoatsListsFromBookAdapter;
import com.armpk.goatregistrator.database.City;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.database.enums.LocationType;
import com.armpk.goatregistrator.database.enums.Sex;
import com.armpk.goatregistrator.database.mobile.LocalGoat;
import com.armpk.goatregistrator.database.mobile.LocalVisitProtocol;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

public class GoatsListsFromVisitProtocolActivity extends AppCompatActivity {
    private static final String ARG_VISIT_PROTOCOL = "visit_protocol";
    private static final String ARG_LOCAL_VP_ID = "local_visit_protocol_id";

    private DatabaseHelper dbHelper;
    private SharedPreferences mSharedPreferences;

    private ListView listViewGoats;
    private Button mButtonPrint;
    private GoatsListsFromBookAdapter bookGLadapter;

    private LocalVisitProtocol mLocalVisitProtocol;
    private VisitProtocol mVisitProtocol;
    private boolean isProtocolSynced = false;

    private WebView mWebView;

    Calendar now;

    List<Goat> listG = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());

        setContentView(R.layout.activity_goat_list_from_book);

        mWebView = (WebView)findViewById(R.id.webForPrint);
        mButtonPrint = (Button)findViewById(R.id.buttonPrint);
        mButtonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isProtocolSynced){
                    printLists();
                }else{
                    printLocalLists();
                }
            }
        });
        listViewGoats = (ListView)findViewById(R.id.listMain);
        if (getIntent().getExtras()!=null) {
            if(getIntent().getExtras().getSerializable(ARG_VISIT_PROTOCOL) instanceof LocalVisitProtocol) {
                mLocalVisitProtocol = (LocalVisitProtocol) getIntent().getExtras().getSerializable(ARG_VISIT_PROTOCOL);
                mLocalVisitProtocol.setId(getIntent().getExtras().getLong(ARG_LOCAL_VP_ID));
                isProtocolSynced = false;

            }else if(getIntent().getExtras().getSerializable(ARG_VISIT_PROTOCOL) instanceof VisitProtocol){
                mVisitProtocol = (VisitProtocol) getIntent().getExtras().getSerializable(ARG_VISIT_PROTOCOL);
                isProtocolSynced = true;
            }
            InitActivity ia = new InitActivity(this);
            ia.execute((Void) null);
        }else{
            finish();
        }
    }

    private void printLists(){
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            //mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("PRINTING INFO", "page finished loading " + url);
                createWebPrintJob(view);
                //mWebView = null;
            }
        });

        StringBuffer htmlDocument = new StringBuffer();
        htmlDocument.append("<html>" +
                "<head>\n" +
                "<style type=\"text/css\">\n" +
                "table, th, td {\n" +
                "    border: 1px solid black;\n" +
                "}\n" +
                "table { page-break-inside:auto }\n" +
                "    tr    { page-break-inside:avoid; page-break-after:auto }\n" +
                "thead { display:table-header-group }\n" +
                "@media print {\n" +
                "      .output {\n" +
                "        -ms-transform: rotate(270deg);\n" +
                "        /* IE 9 */\n" +
                "        -webkit-transform: rotate(270deg);\n" +
                "        /* Chrome, Safari, Opera */\n" +
                "        transform: rotate(270deg);\n" +
                "        top: 1.5in;\n" +
                "        left: -1in;\n" +
                "      }\n" +
                "    }\n" +
                //".pagebreak { page-break-before: always; }" +
                "@page  \n" +
                "{ \n" +
                "    size: landscape;   /* auto is the initial value */ \n" +
                "\n" +
                "    /* this affects the margin in the printer settings */ \n" +
                "    margin: 5mm 10mm 10mm 10mm;  \n" +
                "}" +
                "</style>\n" +
                "</head><body><p><b>");
        htmlDocument.append("Списък с ").append(bookGLadapter.getCount()).append(" кози на: ")
                .append(mVisitProtocol.getFarm().getCompanyName())
                .append(", ");

        City cty = null;
        try {
            cty = dbHelper.getDaoCity().queryForId(mVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(cty.getLocationType()== LocationType.CITY){
            htmlDocument.append("гр. ");
        }else{
            htmlDocument.append("с. ");
        }
        htmlDocument.append(cty.getName())
                .append(", община ")
                .append(cty.getMunicipality())
                .append(", област ")
                .append(cty.getArea())
                .append(", No. ЖО: ")
                .append(mVisitProtocol.getFarm().getBreedingPlaceNumber());
        htmlDocument.append("</p><b><table style=\"width:100%\">");

        insertTableHeader(htmlDocument);
        //htmlDocument.append("<tr>");
        //htmlDocument.append(createTableCellSpan("Налични кози без промени"));
        //htmlDocument.append("</tr>");

        for(int i = 0; i<bookGLadapter.getCount(); i++){
            htmlDocument.append("<tr>");
            htmlDocument.append(processGoat(bookGLadapter.getItem(i), i));
            htmlDocument.append("</tr>");
        }


        htmlDocument.append("</table></body></html>");
        webView.loadDataWithBaseURL(null, htmlDocument.toString(), "text/HTML", "UTF-8", null);
        mWebView = webView;
        //finish();
    }

    private void printLocalLists(){
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            //mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("PRINTING INFO", "page finished loading " + url);
                createWebPrintJob(view);
                //mWebView = null;
            }
        });

        StringBuffer htmlDocument = new StringBuffer();
        htmlDocument.append("<html>" +
                "<head>\n" +
                "<style type=\"text/css\">\n" +
                "table, th, td {\n" +
                "    border: 1px solid black;\n" +
                "}\n" +
                "table { page-break-inside:auto }\n" +
                "    tr    { page-break-inside:avoid; page-break-after:auto }\n" +
                "thead { display:table-header-group }\n" +
                "@media print {\n" +
                "      .output {\n" +
                "        -ms-transform: rotate(270deg);\n" +
                "        /* IE 9 */\n" +
                "        -webkit-transform: rotate(270deg);\n" +
                "        /* Chrome, Safari, Opera */\n" +
                "        transform: rotate(270deg);\n" +
                "        top: 1.5in;\n" +
                "        left: -1in;\n" +
                "      }\n" +
                "    }\n" +
                //".pagebreak { page-break-before: always; }" +
                "@page  \n" +
                "{ \n" +
                "    size: landscape;   /* auto is the initial value */ \n" +
                "\n" +
                "    /* this affects the margin in the printer settings */ \n" +
                "    margin: 5mm 10mm 10mm 10mm;  \n" +
                "}" +
                "</style>\n" +
                "</head><body><p><b>");
        htmlDocument.append("Списък с ").append(bookGLadapter.getCount()).append(" кози на: ")
                .append(mLocalVisitProtocol.getFarm().getCompanyName())
                .append(", ");

        City cty = null;
        try {
            cty = dbHelper.getDaoCity().queryForId(mLocalVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(cty.getLocationType()== LocationType.CITY){
            htmlDocument.append("гр. ");
        }else{
            htmlDocument.append("с. ");
        }
        htmlDocument.append(cty.getName())
                .append(", община ")
                .append(cty.getMunicipality())
                .append(", област ")
                .append(cty.getArea())
                .append(", No. ЖО: ")
                .append(mLocalVisitProtocol.getFarm().getBreedingPlaceNumber());
        htmlDocument.append("</p><b><table style=\"width:100%\">");

        insertTableHeader(htmlDocument);
        //htmlDocument.append("<tr>");
        //htmlDocument.append(createTableCellSpan("Налични кози без промени"));
        //htmlDocument.append("</tr>");

        for(int i = 0; i<bookGLadapter.getCount(); i++){
            htmlDocument.append("<tr>");
            htmlDocument.append(processGoat(bookGLadapter.getItem(i), i));
            htmlDocument.append("</tr>");
        }


        htmlDocument.append("</table></body></html>");
        webView.loadDataWithBaseURL(null, htmlDocument.toString(), "text/HTML", "UTF-8", null);
        mWebView = webView;
        //finish();
    }

    private String processGoat(Goat goat, int position) {
        StringBuffer result = new StringBuffer();
        //result.append("<td>");

        //column 1
        result.append(createTableCellCentered(String.valueOf(position+1)));

        //column 2
        if(goat.getAppliedForSelectionControlYear()!=null) {
            result.append(createTableCellCentered("Да"));
        }else{
            result.append(createTableCellCentered("Не"));
        }
        //column 3
        if(goat.getBreed()!=null) {
            result.append(createTableCell(goat.getBreed().getBreedName()));
        }else{
            result.append(createTableCellCentered("НЕПОСОЧЕНА"));
        }

        //column 4
        if(goat.getFirstVeterinaryNumber()!=null) {
            result.append(createTableCell(goat.getFirstVeterinaryNumber()));
        }else if(goat.getFirstVeterinaryNumber()==null || (goat.getFirstVeterinaryNumber()!=null && goat.getFirstVeterinaryNumber().equals(""))) {
            result.append(createTableCell("НЯМА МАРКА"));
        }else{
            result.append(createTableCell("НЯМА МАРКА"));
        }

        //column 5
        if(goat.getSecondVeterinaryNumber()!=null) {
            result.append(createTableCell(goat.getSecondVeterinaryNumber()));
        }else{
            result.append(createTableCell(""));
        }

        //column 6
        //result.append(createTableCell(""));

        //column 7
        if(goat.getFirstBreedingNumber()!=null) {
            result.append(createTableCell(goat.getFirstBreedingNumber()));
        }else if(goat.getFirstBreedingNumber()==null || (goat.getFirstBreedingNumber()!=null && goat.getFirstBreedingNumber().equals(""))) {
            result.append(createTableCell("НЯМА МАРКА"));
        }else{
            result.append(createTableCell("НЯМА МАРКА"));
        }

        //column 8
        if(goat.getSecondBreedingNumber()!=null) {
            result.append(createTableCell(goat.getSecondBreedingNumber()));
        }else{
            result.append(createTableCell(""));
        }

        //column 9
        if(goat.getBirthDate()!=null) {
            Calendar bc = new GregorianCalendar();
            bc.setTimeInMillis(goat.getBirthDate().getTime());
            result.append(createTableCellCentered(String.valueOf(now.get(Calendar.YEAR) - bc.get(Calendar.YEAR))));
        }else{
            result.append(createTableCell(""));
        }

        //column 10_1
        if(goat.getSex()== Sex.MALE){
            result.append(createTableCell("Мъжки"));
        }else{
            result.append(createTableCell("Женски"));
        }

        //column 10
        if(goat.getId() != null){
            result.append(createTableCell("Стара"));
        }else{
            result.append(createTableCell("Нова"));
        }

        //column 11
        if(goat.getCertificateNumber()!=null && goat.getNumberInCertificate()!=null){
            result.append(createTableCell(
                    "No. "+String.valueOf(goat.getNumberInCertificate())+" в "+goat.getCertificateNumber()));
        }else{
            result.append(createTableCell(""));
        }

        //column 12
        if(goat.getWastageProtocolNumber()!=null && goat.getNumberInWastageProtocol()!=null){
            result.append(createTableCell(
                    "No. "+String.valueOf(goat.getNumberInWastageProtocol()+" в "+goat.getWastageProtocolNumber())
            ));
        }else{
            result.append(createTableCell(""));
        }



        return result.toString();
    }

    private void insertTableHeader(StringBuffer htmlDocument){
        htmlDocument.append("<thead>");
        htmlDocument.append("<tr>");
        htmlDocument.append(createTableCell("No."));
        htmlDocument.append(createTableCell("Подадено\nСК 2016"));
        htmlDocument.append(createTableCell("Порода"));
        htmlDocument.append(createTableCell("Ветеринарна марка -\nактивна"));
        htmlDocument.append(createTableCell("Стара ветеринарна марка"));
        //htmlDocument.append(createTableCell("Премаркирана\n/попълва се от фермера/"));
        htmlDocument.append(createTableCell("Развъден номер -\nактивен"));
        htmlDocument.append(createTableCell("Развъден номер -\nстар"));
        htmlDocument.append(createTableCell("Години"));
        htmlDocument.append(createTableCell("Пол"));
        htmlDocument.append(createTableCell("Нова/Стара"));
        htmlDocument.append(createTableCell("No. и Сертификат"));
        htmlDocument.append(createTableCell("No. и Протокол за брак"));
        htmlDocument.append("</tr>");
        htmlDocument.append("</thead>");
    }

    private String createTableCell(String data){
        StringBuffer buf = new StringBuffer();
        buf.append("<td>");
        buf.append(data);
        buf.append("</td>");
        return buf.toString();
    }

    private String createTableCellCentered(String data){
        StringBuffer buf = new StringBuffer();
        buf.append("<td align=\"center\">");
        buf.append(data);
        buf.append("</td>");
        return buf.toString();
    }

    private String createTableCellSpan(String data){
        StringBuffer buf = new StringBuffer();
        buf.append("<td align=\"center\" colspan=\"12\">");
        buf.append(data);
        buf.append("</td>");
        return buf.toString();
    }

    private void createWebPrintJob(WebView webView) {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        // Save the job object for later status checking
        //mPrintJobs.add(printJob);
    }

    private void sortSingleList(List<Goat> list){
        Collections.sort(list, new Comparator<Goat>() {

            @Override
            public int compare(Goat goat, Goat goat2) {

                int goat1years = 0;
                int goat2years = 0;

                int goatAFSCY = 3000;
                int goat2AFSCY = 3000;

                if(goat.getBirthDate()!=null && goat2.getBirthDate()!=null) {
                    Calendar bc = new GregorianCalendar();
                    bc.setTimeInMillis(goat.getBirthDate().getTime());
                    Calendar bc2 = new GregorianCalendar();
                    bc2.setTimeInMillis(goat2.getBirthDate().getTime());
                    goat1years = now.get(Calendar.YEAR) - bc.get(Calendar.YEAR);
                    goat2years = now.get(Calendar.YEAR) - bc2.get(Calendar.YEAR);
                }
                if(goat.getAppliedForSelectionControlYear()!=null) goatAFSCY = goat.getAppliedForSelectionControlYear();
                if(goat2.getAppliedForSelectionControlYear()!=null) goat2AFSCY = goat2.getAppliedForSelectionControlYear();


                //int result = this.getName().compareTo(quote.getName());
                int result = 0;
                if(goat.getBreed()!=null && goat2.getBreed()!=null) {
                    result = goat.getBreed().getBreedName().compareTo(goat2.getBreed().getBreedName());
                }
                if (result == 0) {
                    //result = this.getChange().compareTo(quote.getChange());

                    result = goatAFSCY-goat2AFSCY;
                }
                if (result == 0) {
                    //result = this.getPercentChange().compareTo(quote.getPercentChange());
                    result = goat1years - goat2years;
                }
                return result;
            }
        });
    }



    private class InitActivity extends AsyncTask<Void, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        public InitActivity(Context ctx){
            mContext = ctx;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Зареждане на данни");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            //mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final boolean[] success = {false};

            /*listG = dbHelper.getGoatsForFarmSelectedColumns(mVisitProtocol.getFarm());
            sortSingleList(listG);
            bookGLadapter = new GoatsListsFromBookAdapter(GoatsListsFromBookActivity.this, listG);*/
            ArrayList<LocalGoat> GOATS_ARRAY = new ArrayList<LocalGoat>();
            if(isProtocolSynced){
                listG = dbHelper.getGoatsForFarmSelectedColumns(mVisitProtocol.getFarm());
            }else{
                try {
                    if (dbHelper.getDaoLocalVisitProtocol().queryForId(mLocalVisitProtocol.getId()).getLst_localGoat() != null) {
                        GOATS_ARRAY = new ArrayList<LocalGoat>(
                                dbHelper.getDaoLocalVisitProtocol().queryForId(mLocalVisitProtocol.getId()).getLst_localGoat()
                        );
                    }
                    if (GOATS_ARRAY.size() == 0)
                        GOATS_ARRAY = new ArrayList<LocalGoat>(dbHelper.getDaoLocalGoat().queryBuilder()
                                .where().eq("farm_id", mLocalVisitProtocol.getFarm().getId()).query());
                    listG.clear();
                    for(LocalGoat lg : GOATS_ARRAY){
                        listG.add(new Goat(lg));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //listG = dbHelper.getGoatsForFarmSelectedColumns(mLocalVisitProtocol.getFarm());
            }
            sortSingleList(listG);
            bookGLadapter = new GoatsListsFromBookAdapter(GoatsListsFromVisitProtocolActivity.this, listG);

            return success[0];
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(Boolean success) {

            listViewGoats.setAdapter(bookGLadapter);
            /*setTitle("Списък с "+bookGLadapter.getCount()+" кози за ферма "+mVisitProtocol.getFarm().getCompanyName()+" гр./с."
                +mVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getName()+", общ. "
                +mVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getMunicipality()+", област "
                +mVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getArea());*/

            if(isProtocolSynced) {
                setTitle("Списък с " + bookGLadapter.getCount() + " кози за ферма " + mVisitProtocol.getFarm().getCompanyName() + " гр./с."
                        + mVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getName() + ", общ. "
                        + mVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getMunicipality() + ", област "
                        + mVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getArea());
            }else{
                City cty = null;
                try {
                    cty = dbHelper.getDaoCity().queryForId(mLocalVisitProtocol.getFarm().getBreedingPlaceAddress().getCity().getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                setTitle("Списък с " + bookGLadapter.getCount() + " кози за ферма " + mLocalVisitProtocol.getFarm().getCompanyName() + " гр./с."
                        + cty.getName() + ", общ. "
                        + cty.getMunicipality() + ", област "
                        + cty.getArea());
            }

            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
        }
    }
}
