package com.armpk.goatregistrator.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Globals {

    public static final String EXTRA_SECONDARY_MENU_CONTEXT = "extra_secondary_menu_context";

    public static final String SETTING_LAST_USER_NAME = "setting_last_user_name";
    public static final String SETTING_ACTIVE_USER_NAME = "setting_active_user_name";
    public static final String SETTING_ACTIVE_USER_ID = "setting_active_user_id";
    public static final String SETTING_ACTIVE_USER_FIRSTNAME = "setting_active_user_firstname";
    public static final String SETTING_ACTIVE_USER_FAMILYNAME = "setting_active_user_familyname";
    public static final String SETTING_USER_PASS = "setting_user_pass";
    public static final String SETTING_SIGNED_IN = "setting_signed_in";

    public static final String SYNC_FARMS_LAST_DATE = "sync_farms_last_date";
    public static final String SYNC_VISIT_ACTIVITIES_LAST_DATE = "sync_visit_activities_last_date";
    public static final String SYNC_VISIT_PROTOCOLS_LAST_DATE = "sync_visit_protocols_last_update";

    /*public static final String TEMPORARY_VISIT_PROTOCOL = "temporary_visit_protocol";
    public static final String TEMPORARY_VISIT_PROTOCOL_GOATS = "temporary_visit_protocol_goat_ids";
    public static final String TEMPORARY_VISIT_PROTOCOL_GOATS_MEASUREMENTS = "temporary_visit_protocol_goat_measurements";*/

    public static final String TEMPORARY_VISIT_PROTOCOLS = "temporary_visit_protocols";
    public static final String TEMPORARY_ACTIVITIES_FOR_PROTOCOL = "va_for_vp_";
    public static final String TEMPORARY_GOATS_FOR_PROTOCOL = "goats_for_vp_";

    public static enum MenuContext {
        FARM,
        USER,
        GOAT,
        VISIT_PROTOCOL;
    }

    public static final UUID UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //public static final UUID UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static void savePreferences(String key, String value, Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void savePreferences(String key, Boolean value, Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void savePreferences(String key, int value, Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void savePreferences(String key, long value, Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void savePreferences(String key, Set<String> value, Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDateTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss", new Locale("bg","BG"));
        String date = simpleDateFormat.format(time);
        return date;
    }

    public static String getDateTime(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss", new Locale("bg","BG"));
        String date = simpleDateFormat.format(time);
        return date;
    }

    public static String getDate(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("bg","BG"));
        String date = simpleDateFormat.format(time);
        return date;
    }

    public static String getDateShort(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("bg","BG"));
        return simpleDateFormat.format(time);
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static <T> T jsonToObject(JSONObject jsonObject, Class<T> classOfT){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                })
                .create();
        return gson.fromJson(jsonObject.toString(), classOfT);
    }

    public static <T> JSONObject objectToJson(T objT) throws JSONException {
        /*Gson gson = new Gson();
        return new JSONObject(gson.toJson(objT));*/
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                    context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, ser).create();
        return new JSONObject(gson.toJson(objT));
    }
}
