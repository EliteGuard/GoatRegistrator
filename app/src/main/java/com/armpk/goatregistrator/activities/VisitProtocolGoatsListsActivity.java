package com.armpk.goatregistrator.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.adapters.VisitProtocolGoatListExpandableAdapter;
import com.armpk.goatregistrator.adapters.VisitProtocolGoatsListsAdapter;
import com.armpk.goatregistrator.database.Breed;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.FarmGoat;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.GoatFromVetIs;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.database.enums.LocationType;
import com.armpk.goatregistrator.database.enums.Sex;
import com.armpk.goatregistrator.database.mobile.LocalGoat;
import com.armpk.goatregistrator.database.mobile.LocalVisitProtocol;
import com.armpk.goatregistrator.utilities.Globals;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VisitProtocolGoatsListsActivity extends AppCompatActivity {

    private static final String ARG_VISIT_PROTOCOL = "visit_protocol";
    private static final String ARG_SYNCED = "visit_protocol_synced";
    private static final String ARG_LOCAL_VP_ID = "local_visit_protocol_id";

    private DatabaseHelper dbHelper;
    private SharedPreferences mSharedPreferences;

    private ExpandableListView listViewGoats;
    private Button mButtonPrint;
    private VisitProtocolGoatListExpandableAdapter vpGLEAdapter;
    List<String> listDataHeader;
    HashMap<String, List<LocalGoat>> listDataChild;

    private LocalVisitProtocol mLocalVisitProtocol;
    private VisitProtocol mVisitProtocol;
    private boolean isProtocolSynced = false;
    private WebView mWebView;

    Calendar now;



    List<LocalGoat> list1 = new ArrayList<>();
    List<LocalGoat> list2 = new ArrayList<>();
    /*List<Goat> list3 = new ArrayList<>();
    List<Goat> list4 = new ArrayList<>();
    List<Goat> list5 = new ArrayList<>();
    List<Goat> list6 = new ArrayList<>();
    List<Goat> list7 = new ArrayList<>();*/

    List<Goat> listG = new ArrayList<>();

    Map<Long, List<LocalGoat>> listsByFarm = new HashMap<Long, List<LocalGoat>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        now = new GregorianCalendar();
        now.setTimeInMillis(System.currentTimeMillis());

        setContentView(R.layout.activity_visit_protocol_goats_lists);

        mWebView = (WebView)findViewById(R.id.webForPrint);

        mButtonPrint = (Button)findViewById(R.id.buttonPrint);
        mButtonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(isProtocolSynced){
                    printLists();
                /*}else {
                    printLocalLists();
                }*/
            }
        });
        listViewGoats = (ExpandableListView)findViewById(R.id.listMain);
        if (getIntent().getExtras()!=null) {
            isProtocolSynced = getIntent().getExtras().getBoolean(ARG_SYNCED);
            if(isProtocolSynced){
                mVisitProtocol = (VisitProtocol) getIntent().getExtras().getSerializable(ARG_VISIT_PROTOCOL);
            }else{
                mLocalVisitProtocol = (LocalVisitProtocol) getIntent().getExtras().getSerializable(ARG_VISIT_PROTOCOL);
                mLocalVisitProtocol.setId(getIntent().getExtras().getLong(ARG_LOCAL_VP_ID));
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

        beginHTML(htmlDocument);

        Iterator it = listsByFarm.entrySet().iterator();
        try {
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                list1.clear();
                list2.clear();
                /*list3.clear();
                list4.clear();
                list5.clear();
                list6.clear();
                list7.clear();*/

                List<LocalGoat> temp = listsByFarm.get(pair.getKey());
                /*for (Goat g : temp) {
                    processGoatToLists(g);
                }*/
                list1.addAll(temp);
                Farm f = dbHelper.getDaoFarm().queryForId((Long) pair.getKey());
                f.setLst_visitProtocol(null);
                processForListMissing(temp, f);
                sortLists();


                listDataChild = new HashMap<String, List<LocalGoat>>();
                listDataChild.put(listDataHeader.get(0), list1);
                listDataChild.put(listDataHeader.get(1), list2);
                /*listDataChild.put(listDataHeader.get(2), list3);
                listDataChild.put(listDataHeader.get(3), list4);
                listDataChild.put(listDataHeader.get(4), list5);
                listDataChild.put(listDataHeader.get(5), list6);
                listDataChild.put(listDataHeader.get(6), list7);*/

                vpGLEAdapter = new VisitProtocolGoatListExpandableAdapter(VisitProtocolGoatsListsActivity.this,
                        listDataHeader, listDataChild);


                createTable(htmlDocument, f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        list1.clear();
        list2.clear();
        /*list3.clear();
        list4.clear();
        list5.clear();
        list6.clear();
        list7.clear();*/

        htmlDocument.append("</body></html>");
        webView.loadDataWithBaseURL(null, htmlDocument.toString(), "text/HTML", "UTF-8", null);
        mWebView = webView;
    }

    private void beginHTML(StringBuffer htmlDocument) {
        htmlDocument.append("<html>" +
                "<head>\n" +
                "<style type=\"text/css\">\n" +
                "th, td {\n" +
                "    border: 1px solid black;\n" +
                "}\n" +
                "table { page-break-inside:auto; page-break-after:always ;}\n" +
                "tr { page-break-inside:avoid; page-break-after:auto;}\n" +
                "thead { display:table-header-group; }\n" +
                //"    tfoot { display:table-header-group }" +
                "@media print {\n" +
                "      .output {\n" +
                "        -ms-transform: rotate(270deg);\n" +
                "        -webkit-transform: rotate(270deg);\n" +
                "        transform: rotate(270deg);\n" +
                "        top: 1.5in;\n" +
                "        left: -1in;\n" +
                "      }\n" +
                "    }\n" +
                //".pagebreak { page-break-before: always; }" +
                "@page  \n" +
                "{ \n" +
                "    size: landscape;    \n" +
                "    margin: 5mm 10mm 10mm 10mm;  \n" +
                "}" +
                "</style>\n" +
                "</head><body>");
    }

    private void createTable(StringBuffer htmlDocument, Farm farm){
        htmlDocument.append("<p><br><br><br><br><br><b>");
        htmlDocument.append("Списък с ").append(
                (vpGLEAdapter.getChildrenCount(0)
                        +vpGLEAdapter.getChildrenCount(1)
                        /*+vpGLEAdapter.getChildrenCount(2)
                        +vpGLEAdapter.getChildrenCount(3)
                        +vpGLEAdapter.getChildrenCount(4)
                        +vpGLEAdapter.getChildrenCount(5)
                        +vpGLEAdapter.getChildrenCount(6)*/)
        ).append(" кози за Протокол за посещение от ");
        if(isProtocolSynced) {
            htmlDocument.append(Globals.getDateShort(mVisitProtocol.getVisitDate()));
        }else{
            htmlDocument.append(Globals.getDateShort(mLocalVisitProtocol.getVisitDate()));
        }
        htmlDocument.append(" на: <br>")
                .append(farm.getCompanyName())
                .append(", <br>");
        if(farm.getBreedingPlaceAddress().getCity().getLocationType()== LocationType.CITY){
            htmlDocument.append("гр. ");
        }else{
            htmlDocument.append("с. ");
        }
        htmlDocument.append(farm.getBreedingPlaceAddress().getCity().getName())
                .append(", община ")
                .append(farm.getBreedingPlaceAddress().getCity().getMunicipality())
                .append(", област ")
                .append(farm.getBreedingPlaceAddress().getCity().getArea())
                .append(",<br> No. ЖО: ")
                .append(farm.getBreedingPlaceNumber());
        htmlDocument.append("</b></p><table style=\"width:100%;\">");
        insertTableHeader(htmlDocument);

        int counter = 0;
        for(int i = 0; i<vpGLEAdapter.getGroupCount(); i++){
            htmlDocument.append("<tr>");
            htmlDocument.append(createTableCellSpan(vpGLEAdapter.getGroup(i).toString()));
            htmlDocument.append("</tr>");
            for(int j=0; j<vpGLEAdapter.getChildrenCount(i); j++){
                htmlDocument.append("<tr>");
                htmlDocument.append(processGoat(vpGLEAdapter.getChild(i, j), counter, j));
                htmlDocument.append("</tr>");
                counter++;
            }
        }
        /*htmlDocument.append(createTableCellSpan("Налични кози без промени"));
        int subcounter = 0;
        for(int i = 0; i<vpGLadapter.getCount(); i++){
            if(vpGLadapter.getSectionsHeader().contains(i)) subcounter = 0;
            htmlDocument.append("<tr>");
            htmlDocument.append(processGoat(vpGLadapter.getItem(i), i, subcounter));
            htmlDocument.append("</tr>");
            subcounter++;
        }*/
        htmlDocument.append("</table>");
    }

    private String processGoat(LocalGoat goat, int position, int subposition) {
        StringBuffer result = new StringBuffer();

        //column 1
        result.append(createTableCellCentered(String.valueOf(position+1)));

        //column 2
        result.append(createTableCellCentered(String.valueOf(subposition+1)));

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
        result.append(createTableCell(""));

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
        if(goat.getSex()==Sex.MALE){
            result.append(createTableCell("Мъжки"));
        }else{
            result.append(createTableCell("Женски"));
        }

        //column 10
        if(goat.getRealId() != null){
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

        //column 13
        if(goat.isInVetIS()){
            result.append(createTableCell("ДА"));
        }else{
            result.append(createTableCell("НЕ"));
        }


        //result.append("<td>");
        /*int pos = 0;
        List<Integer> sectionPositions = new ArrayList<>(vpGLadapter.getSectionsHeader());
        int rowType = vpGLadapter.getItemViewType(position);
        switch (rowType) {
            case VisitProtocolGoatsListsAdapter.TYPE_ITEM:


                if(position<sectionPositions.get(0)) pos = position+1;
                else if(position<sectionPositions.get(1)) pos = position;
                else if(position<sectionPositions.get(2)) pos = position-1;
                else if(position<sectionPositions.get(3)) pos = position-2;
                else if(position<sectionPositions.get(4)) pos = position-3;
                else if(position<sectionPositions.get(5)) pos = position-4;
                else pos = position-5;

                //column 1
                result.append(createTableCellCentered(String.valueOf(pos)));

                //column 2
                result.append(createTableCellCentered(String.valueOf(subposition)));

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
                }else{
                    result.append(createTableCell(""));
                }

                //column 5
                if(goat.getSecondVeterinaryNumber()!=null) {
                    result.append(createTableCell(goat.getSecondVeterinaryNumber()));
                }else{
                    result.append(createTableCell(""));
                }

                //column 6
                result.append(createTableCell(""));

                //column 7
                if(goat.getFirstBreedingNumber()!=null) {
                    result.append(createTableCell(goat.getFirstBreedingNumber()));
                }else{
                    result.append(createTableCell(""));
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
                if(goat.getSex()==Sex.MALE){
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

                break;
            case VisitProtocolGoatsListsAdapter.TYPE_SEPARATOR:

                result.append("<tr>");
                if(position==sectionPositions.get(0))
                    result.append(createTableCellSpan("Налични кози с променени ветеринарни марки, които се водят във ВетИС"));
                else if(position==sectionPositions.get(1))
                    result.append(createTableCellSpan("Кози с липсващи ветеринарни марки, които са вписани в родословна книга"));
                else if(position==sectionPositions.get(2))
                    result.append(createTableCellSpan("Нови кози с ветеринарни марки, които се водят във ВетИС"));
                else if(position==sectionPositions.get(3))
                    result.append(createTableCellSpan("Налични кози с ветеринарни марки, които не се водят във ВетИС"));
                else if(position==sectionPositions.get(4))
                    result.append(createTableCellSpan("Кози, липсващи при проверка"));
                else if(position==sectionPositions.get(5))
                    result.append(createTableCellSpan("Кози с особени случаи, за допълнителна проверка"));
                else
                    result.append(createTableCellSpan("Други"));
                result.append("</tr>");
                break;
        }*/


        return result.toString();
    }

    private void insertTableHeader(StringBuffer htmlDocument){
        htmlDocument.append("<thead>");
        htmlDocument.append("<tr>");
        htmlDocument.append(createTableCell("No."));
        htmlDocument.append(createTableCell("НвС"));
        htmlDocument.append(createTableCell("Подадено\nСК 2016"));
        htmlDocument.append(createTableCell("Порода"));
        htmlDocument.append(createTableCell("Ветеринарна марка -\nактивна"));
        htmlDocument.append(createTableCell("Стара ветеринарна марка"));
        htmlDocument.append(createTableCell("Премаркирана\n/попълва се от фермера/"));
        htmlDocument.append(createTableCell("Развъден номер -\nактивен"));
        htmlDocument.append(createTableCell("Развъден номер -\nстар"));
        htmlDocument.append(createTableCell("Години"));
        htmlDocument.append(createTableCell("Пол"));
        htmlDocument.append(createTableCell("Нова/Стара"));
        htmlDocument.append(createTableCell("No. и Сертификат"));
        htmlDocument.append(createTableCell("No. и Протокол за брак"));
        htmlDocument.append(createTableCell("ВетИС"));
        htmlDocument.append("</tr>");
        htmlDocument.append("</thead>");

    }

    private void insertHeading(StringBuffer htmlDocument){
        //htmlDocument.append("Списък с "+(vpGLadapter.getCount()-6)+" кози за Протокол за посещение от "+Globals.getDateShort(mVisitProtocol.getVisitDate()));
    }

    private void insertPageBreak(StringBuffer htmlDocument){
        htmlDocument.append("<div class=\"pagebreak\"> </div>");
        htmlDocument.append("<p><!-- pagebreak --></p> ");
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
        buf.append("<td align=\"center\" colspan=\"14\">");
        buf.append(data);
        buf.append("</td>");
        return buf.toString();
    }

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = null;
        String jobName = "";//getString(R.string.app_name) +  + " Document";
        if(isProtocolSynced) {
            printAdapter = webView.createPrintDocumentAdapter(mVisitProtocol.getFarm().getCompanyName()
            +"_"+Globals.getDateShort(mVisitProtocol.getVisitDate()));
            //jobName = getString(R.string.app_name) + mVisitProtocol.getId() + " Document";
            jobName = mVisitProtocol.getFarm().getCompanyName().trim()
                    +"_"+Globals.getDateShort(mVisitProtocol.getVisitDate());
        }else{
            printAdapter = webView.createPrintDocumentAdapter(mLocalVisitProtocol.getFarm().getCompanyName()
                    +"_"+Globals.getDateShort(mLocalVisitProtocol.getVisitDate()));
            //jobName = getString(R.string.app_name) + mLocalVisitProtocol.getId() + " Document";
            jobName = mLocalVisitProtocol.getFarm().getCompanyName().trim()
                    +"_"+Globals.getDateShort(mLocalVisitProtocol.getVisitDate());
        }


        // Create a print job with name and adapter instance


        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);

        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());

        // Save the job object for later status checking
        //mPrintJobs.add(printJob);

        //finish();
    }

    private void processGoatToLists(Goat gt){
        try {
            //listG.add(Globals.jsonToObject(new JSONObject(s), Goat.class));
            //Goat gt = Globals.jsonToObject(new JSONObject(s), Goat.class);
            ///
            ////
            /*QueryBuilder<FarmGoat, Long> farmGoatLongQb = dbHelper.getDaoFarmGoat().queryBuilder();
            farmGoatLongQb.selectColumns("goat_id");
            farmGoatLongQb.where().eq("farm_id", mVisitProtocol.getFarm().getId());*/

            QueryBuilder<Goat, Long> gqb = dbHelper.getDaoGoat().queryBuilder();
            gqb.selectColumns("_id", "appliedForSelectionControlYear", "breed_id",
                    "firstVeterinaryNumber", "secondVeterinaryNumber", "firstBreedingNumber", "secondBreedingNumber",
                    "sex")
                    .where()
                    .eq("firstVeterinaryNumber", gt.getFirstVeterinaryNumber())
                    .or()
                    .eq("firstBreedingNumber", gt.getFirstBreedingNumber());
            //gqb.join(farmGoatLongQb);
            List<Goat> tg = gqb.query();
            Goat gk = null;
            if(tg.size()>0) gk=tg.get(0);
            GoatFromVetIs gv = null;
            List<GoatFromVetIs> tgv = dbHelper.getDaoGoatFromVetIs().queryBuilder().where()
                    .eq("firstVeterinaryNumber", gt.getFirstVeterinaryNumber())
                    .query();
            if(tgv.size()>0) gv=tgv.get(0);


            /*
            //------------ LIST 1 CONDITIONS
            if (gk!=null && gv!=null && gt.getBreed()!=null && gk.getBreed()!=null && gv.getBreed()!=null &&
                    gt.getBreed().getId().equals(gk.getBreed().getId()) && gt.getBreed().getBreedName().equals(gv.getBreed())
                    &&
                    gt.getFirstVeterinaryNumber().equals(gk.getFirstVeterinaryNumber()) && gk.getFirstVeterinaryNumber().equals(gv.getFirstVeterinaryNumber())
                    &&
                    gt.getId()!=null
                    ) {
                list1.add(gt);
            }

            //------------ LIST 2 CONDITIONS
            else if (gk!=null && gv!=null && gt.getBreed()!=null && gk.getBreed()!=null && gv.getBreed()!=null &&
                    (gt.getSecondVeterinaryNumber()!=null && !gt.getSecondVeterinaryNumber().equals(""))
                    &&
                    (gk.getFirstVeterinaryNumber()!=null && !gk.getFirstVeterinaryNumber().equals(""))
                    &&
                    gt.getBreed().getId().equals(gk.getBreed().getId()) && gt.getBreed().getBreedName().equals(gv.getBreed())
                    &&
                    gt.getFirstBreedingNumber().equals(gk.getFirstBreedingNumber())
                    &&
                    gt.getSecondVeterinaryNumber().equals(gk.getFirstVeterinaryNumber())
                    &&
                    gt.getFirstVeterinaryNumber().equals(gv.getFirstVeterinaryNumber())
                    &&
                    gt.getId()!=null
                    ) {
                list2.add(gt);
            }
            //------------ LIST 3 CONDITIONS
            else if (gk!=null && gt.getBreed()!=null && gt.getBreed()!=null && gk.getBreed()!=null &&
                    gt.getBreed().getId().equals(gk.getBreed().getId())
                    &&
                    (gt.getFirstBreedingNumber().equals(gk.getFirstBreedingNumber()) || gt.getFirstBreedingNumber()==null || gt.getFirstBreedingNumber().equals(""))
                    &&
                    (gt.getFirstVeterinaryNumber().equals("") || gt.getFirstVeterinaryNumber()==null)
                    ) {
                list3.add(gt);
            }

            //------------ LIST 4 CONDITIONS
            else if (gk==null && gv!=null &&
                    gt.getId()==null &&
                    gt.getFirstVeterinaryNumber().equals(gv.getFirstVeterinaryNumber())
                    ) {
                list4.add(gt);
            }

            //------------ LIST 5 CONDITIONS
            else if (gk!=null && gv==null &&
                    (gt.getFirstVeterinaryNumber()!=null || !gt.getFirstVeterinaryNumber().equals(""))
                    ) {
                list5.add(gt);
            }

            //------------ LIST 6 CONDITIONS


            //------------ LIST 7 CONDITIONS
            else {
                list7.add(gt);
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processForListMissing(List<LocalGoat> goatsFromTablet, Farm farm){

        //------------ LIST 6 CONDITIONS
        try{
            QueryBuilder<Goat, Long> gqb;
            QueryBuilder<FarmGoat, Long> fgqb = dbHelper.getDaoFarmGoat().queryBuilder();
            fgqb.where().eq("farm_id", farm.getId());
            gqb = dbHelper.getDaoGoat().queryBuilder();
            gqb.selectColumns("firstVeterinaryNumber", "secondVeterinaryNumber",
                    "firstBreedingNumber", "secondBreedingNumber",
                    "sex", "birthDate", "breed_id",
                    "appliedForSelectionControlYear", "inclusionStatusString",
                    "numberInCertificate", "certificateNumber", "numberInWastageProtocol", "wastageProtocolNumber", "dateDeregistered",
                    "dateLastUpdated", "lastUpdatedByUser_id", "lastModifiedBy");
            gqb.join(fgqb);
            List<Goat> goatsFromBook = gqb.query();

            for(LocalGoat tg : goatsFromTablet){
                for(Goat bg : goatsFromBook){
                    if(bg.getFirstVeterinaryNumber()!=null && tg.getFirstVeterinaryNumber()!=null){
                        String bgv1 = bg.getFirstVeterinaryNumber().trim();
                        String tgv1 = tg.getFirstVeterinaryNumber().trim();
                        if(bgv1.equals(tgv1)){
                            goatsFromBook.remove(bg);
                            break;
                        }
                    }
                    if(bg.getFirstVeterinaryNumber()!=null && tg.getSecondVeterinaryNumber()!=null){
                        String bgv1 = bg.getFirstVeterinaryNumber().trim();
                        String tgv2 = tg.getSecondVeterinaryNumber().trim();
                        if(bgv1.equals(tgv2)){
                            goatsFromBook.remove(bg);
                            break;
                        }
                    }
                    if(bg.getFirstBreedingNumber()!=null && tg.getFirstBreedingNumber()!=null){
                        String bgb1 = bg.getFirstBreedingNumber().trim();
                        String tgb1 = tg.getFirstBreedingNumber().trim();
                        if(bgb1.equals(tgb1)){
                            goatsFromBook.remove(bg);
                            break;
                        }
                    }
                }
            }

            for(Goat g : goatsFromBook){
                LocalGoat tlg = new LocalGoat(g);

                QueryBuilder<GoatFromVetIs, Long> gvqb = dbHelper.getDaoGoatFromVetIs().queryBuilder();
                //gvqb.selectColumns("firstVeterinaryNumber", "secondVeterinaryNumber");

                if(g.getFirstVeterinaryNumber().contains("953551")){
                    Log.d("", "");
                }

                String gv1 = "";
                String gv2 = "";
                if(g.getFirstVeterinaryNumber()!=null) gv1 = g.getFirstVeterinaryNumber().trim();
                if(g.getSecondVeterinaryNumber()!=null) gv2 = g.getSecondVeterinaryNumber().trim();

                if(gv1.length()>0){
                    gvqb.where()
                            .eq("firstVeterinaryNumber", gv1)
                            .or()
                            .eq("secondVeterinaryNumber", gv1);
                }

                List<GoatFromVetIs> lgv = gvqb.query();
                GoatFromVetIs tgv = null;
                if(lgv.size()>0) {
                    tgv = lgv.get(0);
                    tlg.setInVetIS(true);
                }

                list2.add(tlg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sortLists(){
        sortSingleList(list1);
        sortSingleList(list2);
        /*sortSingleList(list3);
        sortSingleList(list4);
        sortSingleList(list5);
        sortSingleList(list6);
        sortSingleList(list7);*/
    }

    private void sortSingleList(List<LocalGoat> list){
        Collections.sort(list, new Comparator<LocalGoat>() {

            @Override
            public int compare(LocalGoat goat, LocalGoat goat2) {

                int goat1years = 0;
                int goat2years = 0;

                int goatAFSCY = 3000;
                int goat2AFSCY = 3000;

                if (goat.getBirthDate() != null && goat2.getBirthDate() != null) {
                    Calendar bc = new GregorianCalendar();
                    bc.setTimeInMillis(goat.getBirthDate().getTime());
                    Calendar bc2 = new GregorianCalendar();
                    bc2.setTimeInMillis(goat2.getBirthDate().getTime());
                    goat1years = now.get(Calendar.YEAR) - bc.get(Calendar.YEAR);
                    goat2years = now.get(Calendar.YEAR) - bc2.get(Calendar.YEAR);
                }
                if (goat.getAppliedForSelectionControlYear() != null)
                    goatAFSCY = goat.getAppliedForSelectionControlYear();
                if (goat2.getAppliedForSelectionControlYear() != null)
                    goat2AFSCY = goat2.getAppliedForSelectionControlYear();

                /*long goat1farmId = 0;
                long goat2farmId = 0;

                StringBuffer key1 = new StringBuffer();
                if(goat.getFirstVeterinaryNumber()!=null) key1.append(goat.getFirstVeterinaryNumber());
                if(goat.getSecondVeterinaryNumber()!=null) key1.append(goat.getSecondVeterinaryNumber());
                if(goat.getFirstBreedingNumber()!=null) key1.append(goat.getFirstBreedingNumber());
                if(goat.getSecondBreedingNumber()!=null) key1.append(goat.getSecondBreedingNumber());
                key1.append("_farm");

                StringBuffer key2 = new StringBuffer();
                if(goat2.getFirstVeterinaryNumber()!=null) key2.append(goat2.getFirstVeterinaryNumber());
                if(goat2.getSecondVeterinaryNumber()!=null) key2.append(goat2.getSecondVeterinaryNumber());
                if(goat2.getFirstBreedingNumber()!=null) key2.append(goat2.getFirstBreedingNumber());
                if(goat2.getSecondBreedingNumber()!=null) key2.append(goat2.getSecondBreedingNumber());
                key2.append("_farm");

                String farm1 = mSharedPreferences.getString(key1.toString(), "");
                String farm2 = mSharedPreferences.getString(key2.toString(), "");

                try {
                    if (farm1.length() > 0) goat1farmId = new JSONObject(farm1).getLong("id");
                    if (farm2.length() > 0) goat2farmId = new JSONObject(farm2).getLong("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                int result = 0;



                //if (result == 0) {
                    if (goat.getBreed() != null && goat2.getBreed() != null) {
                        result = goat.getBreed().getBreedName().compareTo(goat2.getBreed().getBreedName());
                    }
                //}
                if (result == 0) {
                    result = goatAFSCY-goat2AFSCY;
                }
                if (result == 0) {
                    //result = this.getPercentChange().compareTo(quote.getPercentChange());
                    result = goat1years - goat2years;
                }
                /*if (result == 0) {
                    result = Boolean.compare(goat.isInVetIS(),goat2.isInVetIS());
                }*/

                return result;
            }
        });
    }

    private void sortGoatsByFarm(List<Goat> list){
        Collections.sort(list, new Comparator<Goat>() {

            @Override
            public int compare(Goat goat, Goat goat2) {

                long goat1farmId = 0;
                long goat2farmId = 0;

                StringBuffer key1 = new StringBuffer();
                if(goat.getFirstVeterinaryNumber()!=null) key1.append(goat.getFirstVeterinaryNumber());
                if(goat.getSecondVeterinaryNumber()!=null) key1.append(goat.getSecondVeterinaryNumber());
                if(goat.getFirstBreedingNumber()!=null) key1.append(goat.getFirstBreedingNumber());
                if(goat.getSecondBreedingNumber()!=null) key1.append(goat.getSecondBreedingNumber());
                key1.append("_farm");

                StringBuffer key2 = new StringBuffer();
                if(goat2.getFirstVeterinaryNumber()!=null) key2.append(goat2.getFirstVeterinaryNumber());
                if(goat2.getSecondVeterinaryNumber()!=null) key2.append(goat2.getSecondVeterinaryNumber());
                if(goat2.getFirstBreedingNumber()!=null) key2.append(goat2.getFirstBreedingNumber());
                if(goat2.getSecondBreedingNumber()!=null) key2.append(goat2.getSecondBreedingNumber());
                key2.append("_farm");

                String farm1 = mSharedPreferences.getString(key1.toString(), "");
                String farm2 = mSharedPreferences.getString(key2.toString(), "");

                try {
                    if (farm1.length() > 0) goat1farmId = new JSONObject(farm1).getLong("id");
                    if (farm2.length() > 0) goat2farmId = new JSONObject(farm2).getLong("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int result = 0;
                result = (int) (goat1farmId - goat2farmId);
                return result;
            }
        });
    }

    private void distributeLocalGoatsToListsByFarm(List<LocalGoat> goatsToProcess) {
        long farmId = -1;

        //try {
            for (LocalGoat goat : goatsToProcess) {

                Farm farm1 = goat.getFarm();
                if(farm1==null){
                    if(isProtocolSynced) {
                        farm1 = mVisitProtocol.getFarm();
                    }else{
                        farm1 = mLocalVisitProtocol.getFarm();
                    }
                }

                List<LocalGoat> list = listsByFarm.get(farm1.getId());
                if (list==null) {
                    listsByFarm.put(farm1.getId(), list = new ArrayList<LocalGoat>());
                }
                if(goat.getFarm()!=null) goat.getFarm().setLst_visitProtocol(null);
                goat.setLocalVisitProtocol(null);
                list.add(goat);
            }
        /*} catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private List<String> distributeGoatsToSpecialLists(){
        List<String> results = new ArrayList<String>();
        String resultList1 = "НАЛИЧНИ КОЗИ\n";
        String resultList2 = "КОЗИ ЛИПСВАЩИ ПРИ ПРОВЕРКА\n";


        HashMap<String, List<LocalGoat>> goatsNumberByBreed = new HashMap<String, List<LocalGoat>>();
        HashMap<String, List<LocalGoat>> kidsNumberByBreed = new HashMap<String, List<LocalGoat>>();
        HashMap<String, List<LocalGoat>> maleNumberByBreed = new HashMap<String, List<LocalGoat>>();
        HashMap<String, List<LocalGoat>> missingGoatsNumberByBreed = new HashMap<String, List<LocalGoat>>();
        HashMap<String, List<LocalGoat>> missingKidsNumberByBreed = new HashMap<String, List<LocalGoat>>();



        for(LocalGoat lg : list1){
            if(lg.getBreed()!=null && lg.getBirthDate()!=null && lg.getSex()!=null) {
                Calendar bc = new GregorianCalendar();
                bc.setTimeInMillis(lg.getBirthDate().getTime());
                int age = now.get(Calendar.YEAR) - bc.get(Calendar.YEAR);

                List<LocalGoat> listGoats = goatsNumberByBreed.get(lg.getBreed().getBreedName());
                if (listGoats == null) {
                    goatsNumberByBreed.put(lg.getBreed().getBreedName(), listGoats = new ArrayList<LocalGoat>());
                }
                listGoats.add(lg);

                if(age<1){
                    List<LocalGoat> listKids = kidsNumberByBreed.get(lg.getBreed().getBreedName());
                    if (listKids == null) {
                        kidsNumberByBreed.put(lg.getBreed().getBreedName(), listKids = new ArrayList<LocalGoat>());
                    }
                    listKids.add(lg);
                }
                if(lg.getSex()==Sex.MALE){
                    List<LocalGoat> listMales = maleNumberByBreed.get(lg.getBreed().getBreedName());
                    if (listMales == null) {
                        maleNumberByBreed.put(lg.getBreed().getBreedName(), listMales = new ArrayList<LocalGoat>());
                    }
                    listMales.add(lg);
                }
            }
        }
        Iterator it = goatsNumberByBreed.entrySet().iterator();
        resultList1 += "\nКози по породи: \n";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            List<LocalGoat> temp = goatsNumberByBreed.get(pair.getKey());
            resultList1 += pair.getKey().toString()+ "->" +temp.size()+"\n";
        }
        it = kidsNumberByBreed.entrySet().iterator();
        resultList1 += "\nЯрета по породи: \n";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            List<LocalGoat> temp = kidsNumberByBreed.get(pair.getKey());
            resultList1 += pair.getKey().toString()+ "->" +temp.size()+"\n";
        }
        it = maleNumberByBreed.entrySet().iterator();
        resultList1 += "\nМъжки(вкл. ярета) по породи: \n";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            List<LocalGoat> temp = maleNumberByBreed.get(pair.getKey());
            resultList1 += pair.getKey().toString()+ "->" +temp.size()+"\n";
        }
        results.add(resultList1);



        for(LocalGoat lg : list2){
            if(lg.getBreed()!=null && lg.getBirthDate()!=null && lg.getSex()!=null) {
                Calendar bc = new GregorianCalendar();
                bc.setTimeInMillis(lg.getBirthDate().getTime());
                int age = now.get(Calendar.YEAR) - bc.get(Calendar.YEAR);

                List<LocalGoat> listGoats = missingGoatsNumberByBreed.get(lg.getBreed().getBreedName());
                if (listGoats == null) {
                    missingGoatsNumberByBreed.put(lg.getBreed().getBreedName(), listGoats = new ArrayList<LocalGoat>());
                }
                listGoats.add(lg);

                if(age<1){
                    List<LocalGoat> listKids = missingKidsNumberByBreed.get(lg.getBreed().getBreedName());
                    if (listKids == null) {
                        missingKidsNumberByBreed.put(lg.getBreed().getBreedName(), listKids = new ArrayList<LocalGoat>());
                    }
                    listKids.add(lg);
                }
            }
        }
        it = missingGoatsNumberByBreed.entrySet().iterator();
        resultList2 += "\nКози по породи: \n";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            List<LocalGoat> temp = missingGoatsNumberByBreed.get(pair.getKey());
            resultList2 += pair.getKey().toString()+ "->" +temp.size()+"\n";
        }
        it = missingKidsNumberByBreed.entrySet().iterator();
        resultList2 += "\nЯрета по породи: \n";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            List<LocalGoat> temp = missingKidsNumberByBreed.get(pair.getKey());
            resultList2 += pair.getKey().toString()+ "->" +temp.size()+"\n";
        }
        results.add(resultList2);

        return results;
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

            listDataHeader = new ArrayList<String>();
            /*listDataHeader.add("Налични кози");
            listDataHeader.add("Кози липсващи при проверка");
            listDataHeader.add("Налични кози без промени");
            listDataHeader.add("Налични кози с променени ветеринарни марки, които се водят във ВетИС");
            listDataHeader.add("Кози с липсващи ветеринарни марки, които са вписани в родословна книга");
            listDataHeader.add("Нови кози с ветеринарни марки, които се водят във ВетИС");
            listDataHeader.add("Налични кози с ветеринарни марки, които не се водят във ВетИС");
            listDataHeader.add("Кози липсващи при проверка");
            listDataHeader.add("Кози с особени случаи, за допълнителна проверка");*/


            //ArrayList<Goat> goatsToProcess = new ArrayList<Goat>();
            List<LocalGoat> localReadyforProcess = new ArrayList<LocalGoat>();
            if(isProtocolSynced){
                try {
                    localReadyforProcess = new ArrayList<LocalGoat>(
                            dbHelper.getDaoLocalVisitProtocol().queryBuilder()
                                    .where()
                                    .eq("real_id", mVisitProtocol.getId())
                                    .queryForFirst().getLst_localGoat()
                    );

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    //List<LocalVisitProtocol> llvp = dbHelper.getDaoLocalVisitProtocol().queryForAll();

                    localReadyforProcess = new ArrayList<LocalGoat>(
                            dbHelper.getDaoLocalVisitProtocol().queryForId(mLocalVisitProtocol.getId()).getLst_localGoat());
                    if(localReadyforProcess.size()<1) localReadyforProcess = dbHelper.getDaoLocalGoat().queryBuilder()
                            .where().eq("farm_id", mLocalVisitProtocol.getFarm().getId()).query();
                    /*for(LocalGoat lg : localReadyforProces){
                        lg.getFarm().setLst_visitProtocol(null);
                        lg.setLocalVisitProtocol(null);
                        goatsToProcess.add(Globals.jsonToObject(
                                Globals.objectToJson(lg), Goat.class
                                ));
                    }*/

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            distributeLocalGoatsToListsByFarm(localReadyforProcess);

            if(listsByFarm.size()>0) {
                try {
                    Map.Entry pair = (Map.Entry) listsByFarm.entrySet().iterator().next();
                    List<LocalGoat> temp = listsByFarm.get(pair.getKey());
                    for (LocalGoat tlg : temp) {
                        //LocalGoat tlg = new LocalGoat(g);

                        QueryBuilder<GoatFromVetIs, Long> gvqb = dbHelper.getDaoGoatFromVetIs().queryBuilder();
                        //gvqb.selectColumns("firstVeterinaryNumber", "secondVeterinaryNumber");

                        String gv1 = "";
                        String gv2 = "";
                        if(tlg.getFirstVeterinaryNumber()!=null) gv1 = tlg.getFirstVeterinaryNumber();
                        if(tlg.getSecondVeterinaryNumber()!=null) gv2 = tlg.getSecondVeterinaryNumber();

                        if(gv1.length()>0){
                            gvqb.where()
                                    .eq("firstVeterinaryNumber", gv1)
                                    .or()
                                    .eq("secondVeterinaryNumber", gv1);
                        }


                        List<GoatFromVetIs> lgv = gvqb.query();
                        GoatFromVetIs tgv = null;
                        if(lgv.size()>0) {
                            tgv = lgv.get(0);
                            tlg.setInVetIS(true);
                        }
                        list1.add(tlg);
                    }
                    //list1.addAll(temp);
                    Farm f = dbHelper.getDaoFarm().queryForId((Long) pair.getKey());
                    f.setLst_visitProtocol(null);
                    processForListMissing(temp, f);
                    sortLists();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            listDataHeader = distributeGoatsToSpecialLists();

            listDataChild = new HashMap<String, List<LocalGoat>>();
            listDataChild.put(listDataHeader.get(0), list1);
            listDataChild.put(listDataHeader.get(1), list2);
            /*listDataChild.put(listDataHeader.get(2), list3);
            listDataChild.put(listDataHeader.get(3), list4);
            listDataChild.put(listDataHeader.get(4), list5);
            listDataChild.put(listDataHeader.get(5), list6);
            listDataChild.put(listDataHeader.get(6), list7);*/

            vpGLEAdapter = new VisitProtocolGoatListExpandableAdapter(VisitProtocolGoatsListsActivity.this,
                    listDataHeader, listDataChild);

            return success[0];
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(Boolean success) {

            //listViewGoats.setAdapter(vpGLadapter);
            listViewGoats.setAdapter(vpGLEAdapter);
            if(isProtocolSynced){
                setTitle("Списък с " + (vpGLEAdapter.getChildrenCount(0)
                        +vpGLEAdapter.getChildrenCount(1)
                        /*+vpGLEAdapter.getChildrenCount(2)
                        +vpGLEAdapter.getChildrenCount(3)
                        +vpGLEAdapter.getChildrenCount(4)
                        +vpGLEAdapter.getChildrenCount(5)
                        +vpGLEAdapter.getChildrenCount(6)*/)
                        + " кози за Протокол за посещение от " + Globals.getDateShort(mVisitProtocol.getVisitDate()));
            }else {
                setTitle("Списък с " + (vpGLEAdapter.getChildrenCount(0)
                        +vpGLEAdapter.getChildrenCount(1)
                        /*+vpGLEAdapter.getChildrenCount(2)
                        +vpGLEAdapter.getChildrenCount(3)
                        +vpGLEAdapter.getChildrenCount(4)
                        +vpGLEAdapter.getChildrenCount(5)
                        +vpGLEAdapter.getChildrenCount(6)*/)
                        + " кози за Протокол за посещение от " + Globals.getDateShort(mLocalVisitProtocol.getVisitDate()));
            }
            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
        }
    }
}
