package com.armpk.goatregistrator.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RestConnection extends AsyncTask<Void, Integer, String> {

    private OnConnectionCompleted mConnectionListener;

    private String mUrl = "http://94.156.222.197:8081/goats/armpkrest/members/";
    private Context mContext;
    private static ProgressDialog mProgressDialog;
    private HashMap<String, String> mPostData;
    private JSONObject mJSONData;
    private Action mAction;
    private DataType mDataType;
    private String additionalDataArg;
    private boolean mIsBackground = false;

    //private DialogSwitcher dialogSwitcher; // 0 - no, 1 - open, 2 - close

    public enum Action{
        LOGIN,
        GET,
        PUT,
        POST,
        DELETE,
        SYNCHRONIZE
    }

    public enum DataType {
        USERROLES,
        LOGIN,
        CITIES,
        BREEDS,
        GOAT_STATUS,
        EXTERIOR_MARK,
        VISIT_ACTIVITY,
        FARMS,
        FARM_NEW,
        VISIT_PROTOCOL,
        VISIT_PROTOCOL_NEW,
        VISIT_PROTOCOL_UPDATE,
        VISIT_PROTOCOL_BYID,
        HERDS,
        HERDS_RANGE,
        HERDSBYFARM,
        GOATSBYFARM,
        MEASUREMENTS,
        GOATVETISBYFARM,
        BONITIROVKA
    }

    /*public static enum DialogSwitcher{
        NOT,
        OPENER,
        CLOSER
    }*/

    public RestConnection(Context context, DataType dataType, OnConnectionCompleted connectionListener){
        mContext = context;
        if(mProgressDialog==null) mProgressDialog = new ProgressDialog(mContext);
        mConnectionListener = connectionListener;
        mPostData = new HashMap<String, String>();
        mJSONData = new JSONObject();
        mDataType = dataType;
    }

    public RestConnection(Context context, DataType dataType, String arg, OnConnectionCompleted connectionListener){
        mContext = context;
        if(mProgressDialog==null) mProgressDialog = new ProgressDialog(mContext);
        mConnectionListener = connectionListener;
        mPostData = new HashMap<String, String>();
        mJSONData = new JSONObject();
        mDataType = dataType;
        additionalDataArg = arg;
    }

    @Override
    protected void onPreExecute() {
        if(!mProgressDialog.isShowing() && !mIsBackground) {
            mProgressDialog.setMessage("Свързване");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.setMax(2);
            mProgressDialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;

        if(hasInternetConnection()) {
            InputStream is = null;
            try {
                setDataType(additionalDataArg);
                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                conn.setUseCaches(false);
                setMethod(conn);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type","application/json");

                if(conn.getRequestMethod() == "POST" || conn.getRequestMethod() == "PUT" ) {
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(mJSONData.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                }
                // Starts the query
                conn.connect();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    publishProgress(1);
                    is = conn.getInputStream();
                    //long contentLength = Long.parseLong(conn.getHeaderField("Content-Length"));
                    result = readInputStream(is);
                    publishProgress(2);
                    //result = readInputStream(is, conn.getContentLength());
                }
            } catch (UnsupportedEncodingException | ProtocolException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if(!mIsBackground) updateProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {

        if(result==null){
            Toast.makeText(mContext, "Възникна грешка при свързване", Toast.LENGTH_LONG).show();
        }
        //if(result!=null){
            mConnectionListener.onResultReceived(mAction, mDataType, result);
        /*}else{
            Toast.makeText(mContext, "Възникна грешка при свързване", Toast.LENGTH_LONG).show();
            if(this.mProgressDialog.isShowing()){
                this.mProgressDialog.cancel();
            }
        }*/


    }

    public static void setProgressMax(int max){
        mProgressDialog.setMax(max);
    }

    private void updateProgress(int value){
        mProgressDialog.setProgress(value);
        if(value==2 && mProgressDialog.getMax()==2){
            mProgressDialog.setMessage("Данните са получени!");
        }
    }

    public static void incrementProgress(int value){
        mProgressDialog.incrementProgressBy(value);
    }

    public static void closeProgressDialog(){
        if(mProgressDialog.isShowing()){
            mProgressDialog.cancel();
        }
        mProgressDialog = null;
    }

    public boolean hasInternetConnection(){
        boolean active = false;
        ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        active = networkInfo != null && networkInfo.isConnected();
        return active;
    }

    public void setAction(Action action){
        mAction = action;
    }

    private void setMethod(HttpURLConnection conn) throws ProtocolException {
        switch (mAction){
            case LOGIN:
                conn.setRequestMethod("POST");
                break;
            case GET:
                conn.setRequestMethod("GET");
                break;
            case PUT:
                conn.setRequestMethod("PUT");
                break;
            case POST:
                conn.setRequestMethod("POST");
                break;
            case DELETE:
                conn.setRequestMethod("DELETE");
                break;
        }
    }

    public void addPostParameter(String key, String value){
        mPostData.put(key, value);
    }

    public void setJSONData(JSONObject jsonData){
        this.mJSONData = jsonData;
    }

    public void addJSONAttribute(String name, String value) throws JSONException {
        mJSONData.put(name, value);
    }
    public void addJSONAttribute(String name, boolean value) throws JSONException {
        mJSONData.put(name, value);
    }
    public void addJSONAttribute(String name, double value) throws JSONException {
        mJSONData.put(name, value);
    }
    public void addJSONAttribute(String name, int value) throws JSONException {
        mJSONData.put(name, value);
    }
    public void addJSONAttribute(String name, long value) throws JSONException {
        mJSONData.put(name, value);
    }
    public void addJSONAttribute(String name, Object value) throws JSONException {
        mJSONData.put(name, value);
    }

    private String getPostDataString() throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : mPostData.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        mPostData.clear();
        return result.toString();
    }

    private void setDataType(String arg){
        switch (mDataType){
            case LOGIN:
                appendUrl("login");
                break;
            case USERROLES:
                appendUrl("userroles");
                break;
            case CITIES:
                appendUrl("cities");
                break;
            case BREEDS:
                appendUrl("breeds");
                break;
            case GOAT_STATUS:
                appendUrl("statuses");
                break;
            case EXTERIOR_MARK:
                appendUrl("exteriormarks");
                break;
            case VISIT_ACTIVITY:
                appendUrl("visit_activities");
                break;
            case FARMS:
                appendUrl("farms");
                break;
            case FARM_NEW:
                appendUrl("newFarm");
                break;
            case VISIT_PROTOCOL:
                appendUrl("visit_protocols");
                break;
            case VISIT_PROTOCOL_UPDATE:
                appendUrl("updateVisitProtocol");
                break;
            case VISIT_PROTOCOL_NEW:
                appendUrl("newVisitProtocol");
                break;
            case VISIT_PROTOCOL_BYID:
                appendUrl("visit_protocols/");
                if(arg!=null) appendUrl(arg);
                break;
            case HERDS:
                appendUrl("herds");
                break;
            case HERDS_RANGE:
                appendUrl("herds_range");
                break;
            case HERDSBYFARM:
                appendUrl("herdsbyfarm");
                break;
            case GOATSBYFARM:
                appendUrl("goatsbyfarm");
                break;
            case MEASUREMENTS:
                appendUrl("measurements");
                break;
            case GOATVETISBYFARM:
                appendUrl("vetis_data_by_farm/");
                if(arg!=null) appendUrl(arg);
                break;
            case BONITIROVKA:
                appendUrl("goats/");
                if(arg!=null) appendUrl(arg);
                appendUrl("/measurements/bonitirovka");
                break;
        }
    }

    public DataType getDataType(){
        return this.mDataType;
    }

    public void setIsBackground(boolean mIsBackground) {
        this.mIsBackground = mIsBackground;
    }

    /*public void setDialogSwitcher(DialogSwitcher dialogSwitcher) {
        this.dialogSwitcher = dialogSwitcher;
    }*/

    public void appendUrl(String url){
        mUrl += url;
    }

    private String readInputStream(InputStream stream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int c;
        for (c = stream.read(); c != '\n' && c != -1 ; c = stream.read()) {
            byteArrayOutputStream.write(c);
        }
        if (c == -1 && byteArrayOutputStream.size() == 0) {
            return null;
        }
        return byteArrayOutputStream.toString("UTF-8");
        /*Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);*/
    }

    public interface OnConnectionCompleted{
        void onResultReceived(Action action, DataType dataType, String result);
    }
}
