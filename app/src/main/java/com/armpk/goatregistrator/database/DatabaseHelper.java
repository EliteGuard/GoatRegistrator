package com.armpk.goatregistrator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.enums.LocationType;
import com.armpk.goatregistrator.database.mobile.LocalGoat;
import com.armpk.goatregistrator.database.mobile.LocalVisitProtocol;
import com.armpk.goatregistrator.database.mobile.LocalVisitProtocolVisitActivity;
import com.armpk.goatregistrator.utilities.Globals;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "armpk_goat_registrator.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 12;

    // the DAO object we use to access the SimpleData table
    private Dao<Address, Long> daoAddress = null;
    private Dao<Breed, Integer> daoBreed = null;
    private Dao<City, Long> daoCity = null;
    private Dao<ExteriorMark, Integer> daoExteriorMark = null;
    private Dao<Farm, Long> daoFarm = null;
    private Dao<FarmGoat, Long> daoFarmGoat = null;
    private Dao<FarmHerd, Long> daoFarmHerd = null;
    private Dao<FertilityState, Long> daoFertilityState = null;
    private Dao<Goat, Long> daoGoat = null;
    private Dao<GoatExteriorMark, Long> daoGoatExteriorMark = null;
    private Dao<GoatFromVetIs, Long> daoGoatFromVetIs = null;
    private Dao<GoatMeasurement, Long> daoGoatMeasurement = null;
    private Dao<GoatStatus, Integer> daoGoatStatus = null;
    private Dao<Herd, Long> daoHerd = null;
    private Dao<Lactation, Long> daoLactation = null;
    private Dao<Measurement, Integer> daoMeasurement = null;
    private Dao<MilkControl, Long> daoMilkControl = null;
    private Dao<Phone, Long> daoPhone = null;
    private Dao<User, Long> daoUser = null;
    private Dao<UserRole, Integer> daoUserRole = null;
    private Dao<VisitActivity, Integer> daoVisitActivity = null;
    private Dao<VisitProtocol, Long> daoVisitProtocol = null;
    private Dao<VisitProtocolVisitActivity, Long> daoVisitProtocolVisitActivity = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Address.class);
            TableUtils.createTable(connectionSource, Breed.class);
            TableUtils.createTable(connectionSource, City.class);
            TableUtils.createTable(connectionSource, ExteriorMark.class);
            TableUtils.createTable(connectionSource, Farm.class);
            TableUtils.createTable(connectionSource, FarmGoat.class);
            TableUtils.createTable(connectionSource, FarmHerd.class);
            TableUtils.createTable(connectionSource, FertilityState.class);
            TableUtils.createTable(connectionSource, Goat.class);
            TableUtils.createTable(connectionSource, GoatExteriorMark.class);
            TableUtils.createTable(connectionSource, GoatFromVetIs.class);
            TableUtils.createTable(connectionSource, GoatMeasurement.class);
            TableUtils.createTable(connectionSource, GoatStatus.class);
            TableUtils.createTable(connectionSource, Herd.class);
            TableUtils.createTable(connectionSource, Lactation.class);
            TableUtils.createTable(connectionSource, Measurement.class);
            TableUtils.createTable(connectionSource, MilkControl.class);
            TableUtils.createTable(connectionSource, Phone.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, UserRole.class);
            TableUtils.createTable(connectionSource, VisitActivity.class);
            TableUtils.createTable(connectionSource, VisitProtocol.class);
            TableUtils.createTable(connectionSource, VisitProtocolVisitActivity.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        /*initBreeds();
        initGoatStatuses();
        initUserRole();
        initCity();*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            while (++oldVersion <= newVersion) {
                switch (oldVersion) {
                    case 12: {

                        TableUtils.dropTable(connectionSource, LocalGoat.class, true);
                        TableUtils.dropTable(connectionSource, LocalVisitProtocol.class, true);
                        TableUtils.dropTable(connectionSource, LocalVisitProtocolVisitActivity.class, true);
                        //TableUtils.dropTable(connectionSource, LocalGoatMeasurement.class, false);

                        /*TableUtils.createTableIfNotExists(connectionSource, LocalGoat.class);
                        TableUtils.createTableIfNotExists(connectionSource, LocalVisitProtocol.class);
                        TableUtils.createTableIfNotExists(connectionSource, LocalVisitProtocolVisitActivity.class);
                        //TableUtils.createTableIfNotExists(connectionSource, LocalGoatMeasurement.class);
                        applyUpdate2();*/
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Address, Long> getDaoAddress() throws SQLException {
        if (daoAddress == null) {
            daoAddress = getDao(Address.class);
        }
        return daoAddress;
    }

    public Dao<Breed, Integer> getDaoBreed() throws SQLException {
        if (daoBreed == null) {
            daoBreed = getDao(Breed.class);
        }
        return daoBreed;
    }

    public Dao<City, Long> getDaoCity() throws SQLException {
        if (daoCity == null) {
            daoCity = getDao(City.class);
        }
        return daoCity;
    }

    public Dao<ExteriorMark, Integer> getDaoExteriorMark() throws SQLException {
        if (daoExteriorMark == null) {
            daoExteriorMark = getDao(ExteriorMark.class);
        }
        return daoExteriorMark;
    }

    public Dao<Farm, Long> getDaoFarm(){
        if (daoFarm == null) {
            try {
                daoFarm = getDao(Farm.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoFarm;
    }

    public Dao<FarmGoat, Long> getDaoFarmGoat() throws SQLException {
        if (daoFarmGoat == null) {
            daoFarmGoat = getDao(FarmGoat.class);
        }
        return daoFarmGoat;
    }

    public Dao<FarmHerd, Long> getDaoFarmHerd() throws SQLException {
        if (daoFarmHerd == null) {
            daoFarmHerd = getDao(FarmHerd.class);
        }
        return daoFarmHerd;
    }

    public Dao<FertilityState, Long> getDaoFertilityState() throws SQLException {
        if (daoFertilityState == null) {
            daoFertilityState = getDao(FertilityState.class);
        }
        return daoFertilityState;
    }

    public Dao<Goat, Long> getDaoGoat() throws SQLException {
        if (daoGoat == null) {
            daoGoat = getDao(Goat.class);
        }
        return daoGoat;
    }

    public Dao<GoatExteriorMark, Long> getDaoGoatExteriorMark() throws SQLException {
        if (daoGoatExteriorMark == null) {
            daoGoatExteriorMark = getDao(GoatExteriorMark.class);
        }
        return daoGoatExteriorMark;
    }

    public Dao<GoatFromVetIs, Long> getDaoGoatFromVetIs() throws SQLException {
        if (daoGoatFromVetIs == null) {
            daoGoatFromVetIs = getDao(GoatFromVetIs.class);
        }
        return daoGoatFromVetIs;
    }

    public Dao<GoatMeasurement, Long> getDaoGoatMeasurement() throws SQLException {
        if (daoGoatMeasurement == null) {
            daoGoatMeasurement = getDao(GoatMeasurement.class);
        }
        return daoGoatMeasurement;
    }

    public Dao<GoatStatus, Integer> getDaoGoatStatus() throws SQLException {
        if (daoGoatStatus == null) {
            daoGoatStatus = getDao(GoatStatus.class);
        }
        return daoGoatStatus;
    }

    public Dao<Herd, Long> getDaoHerd() throws SQLException {
        if (daoHerd == null) {
            daoHerd = getDao(Herd.class);
        }
        return daoHerd;
    }

    public Dao<Lactation, Long> getDaoLactation() throws SQLException {
        if (daoLactation == null) {
            daoLactation = getDao(Lactation.class);
        }
        return daoLactation;
    }

    public Dao<Measurement, Integer> getDaoMeasurement() throws SQLException {
        if (daoMeasurement == null) {
            daoMeasurement = getDao(Measurement.class);
        }
        return daoMeasurement;
    }

    public Dao<MilkControl, Long> getDaoMilkControl() throws SQLException {
        if (daoMilkControl == null) {
            daoMilkControl = getDao(MilkControl.class);
        }
        return daoMilkControl;
    }

    public Dao<Phone, Long> getDaoPhone() throws SQLException {
        if (daoPhone == null) {
            daoPhone = getDao(Phone.class);
        }
        return daoPhone;
    }

    public Dao<User, Long> getDaoUser() throws SQLException {
        if (daoUser == null) {
            daoUser = getDao(User.class);
        }
        return daoUser;
    }

    public Dao<UserRole, Integer> getDaoUserRole() throws SQLException {
        if (daoUserRole == null) {
            daoUserRole = getDao(UserRole.class);
        }
        return daoUserRole;
    }

    public Dao<VisitActivity, Integer> getDaoVisitActivity() throws SQLException {
        if (daoVisitActivity == null) {
            daoVisitActivity = getDao(VisitActivity.class);
        }
        return daoVisitActivity;
    }

    public Dao<VisitProtocol, Long> getDaoVisitProtocol() throws SQLException {
        if (daoVisitProtocol == null) {
            daoVisitProtocol = getDao(VisitProtocol.class);
        }
        return daoVisitProtocol;
    }

    public Dao<VisitProtocolVisitActivity, Long> getDaoVisitProtocolVisitActivity() throws SQLException {
        if (daoVisitProtocolVisitActivity == null) {
            daoVisitProtocolVisitActivity = getDao(VisitProtocolVisitActivity.class);
        }
        return daoVisitProtocolVisitActivity;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {

        super.close();

        daoAddress = null;
        daoBreed = null;
        daoCity = null;
        daoExteriorMark = null;
        daoFarm = null;
        daoFarmGoat = null;
        daoFarmHerd = null;
        daoFertilityState = null;
        daoGoat = null;
        daoGoatExteriorMark = null;
        daoGoatFromVetIs = null;
        daoGoatMeasurement = null;
        daoGoatStatus = null;
        daoHerd = null;
        daoLactation = null;
        daoMeasurement = null;
        daoMilkControl = null;
        daoPhone = null;
        daoUser = null;
        daoUserRole = null;
        daoVisitActivity = null;
        daoVisitProtocol = null;
        daoVisitProtocolVisitActivity = null;
    }

    public void initBreeds(){
        String [] breeds = {"Англо-нубийска", "Българска бяла млечна", "Местна", "Санска", "Тогенбургска"};
        try {
            daoBreed = getDaoBreed();
            for(String s : breeds){
                Breed breed = new Breed();
                breed.setBreedName(s);
                daoBreed.create(breed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initGoatStatuses(){
        String [] statusN = {"Живо", "Заклано", "Продадено", "Изгубено"};
        String [] statusCN = {"Живо", "Заклано", "Продадено", "Изгубено"};
        String [] statusD = {"Живо", "Заклано", "Продадено", "Изгубено"};
        try {
            daoGoatStatus = getDaoGoatStatus();
            for(int i=0; i<statusN.length; i++){
                GoatStatus gs = new GoatStatus();
                gs.setStatusName(statusN[i]);
                gs.setCodeName(statusCN[i]);
                gs.setDescription(statusD[i]);
                daoGoatStatus.create(gs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initUserRole(){
        String [] statusDN = {"Фермер", "Администратор"};
        String [] statusCN = {"Фермер", "Администратор"};
        String [] statusD = {"Фермер", "Администратор"};
        try {
            daoUserRole = getDaoUserRole();
            for(int i=0; i<statusDN.length; i++) {
                UserRole ur = new UserRole();
                ur.setDisplayName(statusDN[i]);
                ur.setCodeName(statusCN[i]);
                ur.setDescription(statusD[i]);
                daoUserRole.create(ur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initCity(){
        try {
            daoCity = getDaoCity();

            City c = new City();
            c.setName("Русе");
            c.setLocationType(LocationType.CITY);
            c.setArea("Русе");
            c.setMunicipality("Русе");
            c.setPostcode("7000");
            daoCity.create(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Farm> getFarms(){
        try {
            return getDaoFarm().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createVisitProtocol(VisitProtocol visitProtocol){
        boolean success = false;
        try {
            daoVisitProtocol = getDaoVisitProtocol();
            daoVisitProtocol.create(visitProtocol);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    /*public boolean createOrUpdateUserRole(JSONObject userRole){
        boolean success = false;
        try {
            daoUserRole = getDaoUserRole();
            Gson gson = new Gson();
            UserRole ur = gson.fromJson(userRole.toString(), UserRole.class);
            daoUserRole.createOrUpdate(ur);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }*/

    public boolean createOrUpdateUser(JSONObject user){
        boolean success = false;
        try{
            daoUser = getDaoUser();
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();
            User usr = gson.fromJson(user.toString(), User.class);
            daoUser.createOrUpdate(usr);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean createOrUpdateCities(JSONObject city){
        boolean success = false;
        try {
            daoCity = getDaoCity();
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();
            City c = gson.fromJson(city.toString(), City.class);
            daoCity.createOrUpdate(c);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean createOrUpdateBreeds(JSONObject breed){
        boolean success = false;
        try {
            daoBreed = getDaoBreed();
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();
            Breed b = gson.fromJson(breed.toString(), Breed.class);
            daoBreed.createOrUpdate(b);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean createOrUpdateGoatStatuses(JSONObject goatStatus){
        boolean success = false;
        try {
            daoGoatStatus = getDaoGoatStatus();
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();
            GoatStatus gs = gson.fromJson(goatStatus.toString(), GoatStatus.class);
            daoGoatStatus.createOrUpdate(gs);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean createOrUpdateExteriorMarks(JSONObject exteriorMark){
        boolean success = false;
        try {
            daoExteriorMark = getDaoExteriorMark();
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();
            ExteriorMark em = gson.fromJson(exteriorMark.toString(), ExteriorMark.class);
            daoExteriorMark.createOrUpdate(em);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean createOrUpdateVisitActivities(JSONObject visitActivity){
        boolean success = false;
        try {
            daoVisitActivity = getDaoVisitActivity();
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();
            VisitActivity va = gson.fromJson(visitActivity.toString(), VisitActivity.class);
            daoVisitActivity.createOrUpdate(va);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean insertFarm(Farm farm){
        boolean success = true;
        try{
            daoFarm = getDaoFarm();
            daoFarm.create(farm);
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public boolean insertGoat(Goat goat){
        boolean success = true;
        try{
            daoGoat = getDaoGoat();
            daoGoat.create(goat);
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public boolean insertCity(City city){
        boolean success = true;
        try{
            daoCity = getDaoCity();
            daoCity.createIfNotExists(city);
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public boolean insertAddress(Address address){
        boolean success = true;
        try{
            daoAddress = getDaoAddress();
            daoAddress.createIfNotExists(address);
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateUserByDate(User user){
        boolean success = false;
        try{
            daoUser = getDaoUser();
            User localUser;
            if(user!=null) {
                localUser = daoUser.queryForId(user.getId());
                updateUserRoleByDate(user.getUserRole());
                updateCityByDate(user.getCity());
                //updateUserByDate(user.getLastUpdatedByUser());
                if (localUser != null && localUser.getDateLastUpdated().before(user.getDateLastUpdated())) {
                    daoUser.update(user);
                    success = true;
                } else if (localUser == null) {
                    daoUser.create(user);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    ////----------------   NOT WORKING TEMPORARILY
    public boolean updateUserRoleByDate(UserRole userRole, User user){
        boolean success = false;
        try{
            daoUserRole = getDaoUserRole();
            UserRole localUserRole;
            if(userRole!=null) {
                localUserRole = daoUserRole.queryForId(userRole.getId());
                /*if(userRole.getLastUpdatedByUser()!=null && !user.getId().equals(userRole.getLastUpdatedByUser().getId())) {
                    updateUserByDate(userRole.getLastUpdatedByUser());
                }*/
                if (localUserRole != null && localUserRole.getDateLastUpdated().before(userRole.getDateLastUpdated())) {
                    daoUserRole.update(userRole);
                    success = true;
                } else if (localUserRole == null) {
                    daoUserRole.create(userRole);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }
    public boolean updateUserRoleByDate(UserRole userRole){
        boolean success = false;
        try{
            daoUserRole = getDaoUserRole();
            UserRole localUserRole;
            if(userRole!=null) {
                localUserRole = daoUserRole.queryForId(userRole.getId());
                if (localUserRole != null && localUserRole.getDateLastUpdated().before(userRole.getDateLastUpdated())) {
                    daoUserRole.update(userRole);
                    success = true;
                } else if (localUserRole == null) {
                    daoUserRole.create(userRole);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateCityByDate(City city){
        boolean success = false;
        try{
            daoCity = getDaoCity();
            City localCity = null;
            if(city!=null){
                localCity = daoCity.queryForId(city.getId());
                updateUserByDate(city.getLastUpdatedByUser());
                if(localCity!=null && localCity.getDateLastUpdated().before(city.getDateLastUpdated())) {
                    daoCity.update(city);
                    success = true;
                }else if (localCity==null){
                    daoCity.create(city);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateFarmByDate(Farm farm){
        boolean success = false;

        try{
            daoFarm = getDaoFarm();
            Farm localFarm;
            if(farm!=null) {
                localFarm = daoFarm.queryForId(farm.getId());
                updateUserByDate(farm.getFarmer());
                updateAddressByDate(farm.getManagementAddress());
                updateAddressByDate(farm.getCorespondenceAddress());
                updateAddressByDate(farm.getBreedingPlaceAddress());
                if(farm.getLst_contactPhones()!=null) {
                    for (Phone p : farm.getLst_contactPhones()) {
                        updatePhoneByDate(p);
                    }
                }
                if(farm.getLst_visitProtocol()!=null){
                    for(VisitProtocol vp : farm.getLst_visitProtocol()){
                        updateVisitProtocolByDate(Globals.objectToJson(vp));
                    }
                }
                updateUserByDate(farm.getLastUpdatedByUser());

                if (localFarm != null && localFarm.getDateLastUpdated().before(farm.getDateLastUpdated())) {
                    daoFarm.update(farm);
                    success = true;
                } else if (localFarm == null) {
                    daoFarm.create(farm);
                    success = true;
                }
            }
        } catch (SQLException | JSONException e){
            e.printStackTrace();
        }

        return success;
    }

    public boolean updateHerdByDate(JSONObject herdJson){
        boolean success = false;
        try{
            daoHerd = getDaoHerd();
            Herd herd = Globals.jsonToObject(herdJson, Herd.class);
            if(herd!=null) {
                List<Farm> currentFarms = new ArrayList<Farm>();
                JSONArray farmsJson = herdJson.getJSONArray("lst_farms");
                if (farmsJson != null) {
                    for (int i = 0; i < farmsJson.length(); i++) {
                        Farm f = Globals.jsonToObject(new JSONObject(farmsJson.get(i).toString()), Farm.class);
                        updateFarmByDate(f);
                        currentFarms.add(f);
                    }
                } else {
                    for (Farm f : getFarmsForHerd(herd)) {
                        updateFarmByDate(f);
                        currentFarms.add(f);
                    }
                }

                Herd localHerd = daoHerd.queryForId(herd.getId());
                updateUserByDate(herd.getLastUpdatedByUser());
                if (localHerd != null && localHerd.getDateLastUpdated().before(herd.getDateLastUpdated())) {
                    daoHerd.update(herd);
                    for (Farm f : currentFarms) {
                        FarmHerd fh = new FarmHerd();
                        fh.setHerd(herd);
                        fh.setFarm(f);
                        updateFarmHerd(fh);
                    }
                    success = true;
                } else if (localHerd == null) {
                    daoHerd.create(herd);
                    for (Farm f : currentFarms) {
                        FarmHerd fh = new FarmHerd();
                        fh.setHerd(herd);
                        fh.setFarm(f);
                        updateFarmHerd(fh);
                    }
                    success = true;
                }
            }
        } catch (SQLException | JSONException e){
            e.printStackTrace();
        }
        return success;
    }

    public void updateFarmHerd(FarmHerd fh){
        try {
            QueryBuilder<FarmHerd, Long> qb = getDaoFarmHerd().queryBuilder();
            long rows = qb.where().eq("farm_id",fh.getFarm().getId())
                    .and()
                    .eq("herd_id",fh.getHerd().getId())
                    .countOf();
            if(rows<1){
                getDaoFarmHerd().create(fh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateAddressByDate(Address address){
        boolean success = false;
        try{
            daoAddress = getDaoAddress();
            Address localAddress;
            if(address!=null) {
                localAddress = daoAddress.queryForId(address.getId());
                updateCityByDate(address.getCity());
                updateUserByDate(address.getLastUpdatedByUser());
                if (localAddress != null && localAddress.getDateLastUpdated().before(address.getDateLastUpdated())) {
                    daoAddress.update(address);
                    success = true;
                } else if (localAddress == null) {
                    daoAddress.create(address);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean updatePhoneByDate(Phone phone){
        boolean success = false;
        try{
            daoPhone = getDaoPhone();
            Phone localPhone;
            if(phone!=null) {
                localPhone = daoPhone.queryForId(phone.getId());
                updateUserByDate(phone.getLastUpdatedByUser());
                if (localPhone != null && localPhone.getDateLastUpdated().before(phone.getDateLastUpdated())) {
                    daoPhone.update(phone);
                    success = true;
                } else if (localPhone == null) {
                    daoPhone.create(phone);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateMeasurementByDate(JSONObject measurement){
        boolean success = false;

        try{
            daoMeasurement = getDaoMeasurement();
            Measurement m = Globals.jsonToObject(measurement, Measurement.class);
            if(m!=null) {
                updateUserByDate(m.getLastUpdatedByUser());
                Measurement localMeasurement = daoMeasurement.queryForId(m.getId());
                if (localMeasurement != null && localMeasurement.getDateLastUpdated().before(m.getDateLastUpdated())) {
                    daoMeasurement.update(m);
                    success = true;
                } else if (localMeasurement == null) {
                    daoMeasurement.create(m);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateGoatStatusByDate(JSONObject goatStatus){
        boolean success = false;
        try{
            daoGoatStatus = getDaoGoatStatus();
            GoatStatus gs = Globals.jsonToObject(goatStatus, GoatStatus.class);
            if(gs!=null) {
                updateUserByDate(gs.getLastUpdatedByUser());
                GoatStatus localGoatStatus = daoGoatStatus.queryForId(gs.getId());
                if (localGoatStatus != null && localGoatStatus.getDateLastUpdated().before(gs.getDateLastUpdated())) {
                    daoGoatStatus.update(gs);
                    success = true;
                } else if (localGoatStatus == null) {
                    daoGoatStatus.create(gs);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateBreedByDate(JSONObject breed){
        boolean success = false;
        try{
            daoBreed = getDaoBreed();
            Breed b = Globals.jsonToObject(breed, Breed.class);
            if(b!=null) {
                updateUserByDate(b.getLastUpdatedByUser());
                Breed localBreed = daoBreed.queryForId(b.getId());
                if (localBreed != null && localBreed.getDateLastUpdated().before(b.getDateLastUpdated())) {
                    daoBreed.update(b);
                    success = true;
                } else if (localBreed == null) {
                    daoBreed.create(b);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateGoatByDate(JSONObject goat, Farm farm){
        boolean success = false;
        try{
            daoGoat = getDaoGoat();
            Goat g = Globals.jsonToObject(goat, Goat.class);
            if(g!=null) {
                updateUserByDate(g.getLastUpdatedByUser());
                Goat localGoat = daoGoat.queryForId(g.getId());
                if (localGoat != null && localGoat.getDateLastUpdated().before(g.getDateLastUpdated())) {
                    daoGoat.update(g);
                    FarmGoat fg = new FarmGoat();
                    fg.setFarm(farm);
                    fg.setGoat(g);
                    updateFarmGoat(fg);
                } else if (localGoat == null) {
                    daoGoat.create(g);
                    FarmGoat fg = new FarmGoat();
                    fg.setFarm(farm);
                    fg.setGoat(g);
                    updateFarmGoat(fg);
                }
                success = true;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateGoatBonitirovka(JSONObject bonitirovkaJson){
        boolean success = false;
        try{
            daoGoatMeasurement = getDaoGoatMeasurement();
            GoatMeasurement gm = Globals.jsonToObject(bonitirovkaJson, GoatMeasurement.class);
            if(gm!=null) {
                GoatMeasurement localGoatMeasurement = daoGoatMeasurement.queryForId(gm.getId());
                if (localGoatMeasurement != null) {
                    daoGoatMeasurement.update(gm);
                    success = true;
                } else {
                    daoGoatMeasurement.create(gm);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public void updateFarmGoat(FarmGoat fg){
        try {
            QueryBuilder<FarmGoat, Long> qb = getDaoFarmGoat().queryBuilder();
            long rows = qb.where().eq("farm_id",fg.getFarm().getId())
                    .and()
                    .eq("goat_id",fg.getGoat().getId())
                    .countOf();
            if(rows<1){
                getDaoFarmGoat().create(fg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateGoatFromVetisByDate(JSONObject goat){
        boolean success = false;
        try{
            daoGoatFromVetIs = getDaoGoatFromVetIs();
            GoatFromVetIs gfv = Globals.jsonToObject(goat, GoatFromVetIs.class);
            if(gfv!=null) {
                GoatFromVetIs localGoatFromVetIs = daoGoatFromVetIs.queryForId(gfv.getId());
                if (localGoatFromVetIs != null && localGoatFromVetIs.getDateLastModified().before(gfv.getDateLastModified())) {
                    daoGoatFromVetIs.update(gfv);
                    success = true;
                } else if (localGoatFromVetIs == null) {
                    daoGoatFromVetIs.create(gfv);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }


    public boolean updateExteriorMarkByDate(ExteriorMark em){
        boolean success = false;

        try{
            daoExteriorMark = getDaoExteriorMark();
            ExteriorMark localExteriorMark;
            if(em!=null) {
                localExteriorMark = daoExteriorMark.queryForId(em.getId());
                updateUserByDate(em.getLastUpdatedByUser());
                if (localExteriorMark != null && localExteriorMark.getDateLastUpdated().before(em.getDateLastUpdated())) {
                    daoExteriorMark.update(em);
                    success = true;
                } else if (localExteriorMark == null) {
                    daoExteriorMark.create(em);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return success;
    }

    public boolean updateVisitActivityByDate(VisitActivity va){
        boolean success = false;

        try{
            daoVisitActivity = getDaoVisitActivity();
            VisitActivity localVisitActivity;
            if(va!=null) {
                localVisitActivity = daoVisitActivity.queryForId(va.getId());
                updateUserByDate(va.getLastUpdatedByUser());
                if (localVisitActivity != null && localVisitActivity.getDateLastUpdated().before(va.getDateLastUpdated())) {
                    daoVisitActivity.update(va);
                    success = true;
                } else if (localVisitActivity == null) {
                    daoVisitActivity.create(va);
                    success = true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return success;
    }

    public boolean updateVisitProtocolByDate(JSONObject visitProtocol){
        boolean success = false;

        try{
            daoVisitProtocol = getDaoVisitProtocol();
            VisitProtocol vp = Globals.jsonToObject(visitProtocol, VisitProtocol.class);

            if(vp!=null) {
                List<VisitActivity> currentVisitActivities = new ArrayList<VisitActivity>();
                JSONArray visitActivitiesJson = visitProtocol.getJSONArray("lst_visitActivities");
                if (visitActivitiesJson != null) {
                    for (int i = 0; i < visitActivitiesJson.length(); i++) {
                        VisitActivity va = Globals.jsonToObject(new JSONObject(visitActivitiesJson.get(i).toString()), VisitActivity.class);
                        updateVisitActivityByDate(va);
                        currentVisitActivities.add(va);
                    }
                } else {
                    for (VisitActivity va : getActivitiesForProtocols(vp)) {
                        updateVisitActivityByDate(va);
                        currentVisitActivities.add(va);
                    }
                }
                updateFarmByDate(vp.getFarm());
                updateUserByDate(vp.getEmployFirst());
                updateUserByDate(vp.getEmploySecond());
                updateUserByDate(vp.getLastUpdatedByUser());
                VisitProtocol localVisitProtocol = daoVisitProtocol.queryForId(vp.getId());
                if (localVisitProtocol != null && localVisitProtocol.getDateLastUpdated().before(vp.getDateLastUpdated())) {
                    daoVisitProtocol.update(vp);
                    for (VisitActivity va : currentVisitActivities) {
                        VisitProtocolVisitActivity vpva = new VisitProtocolVisitActivity();
                        vpva.setVisitProtocol(vp);
                        vpva.setVisitActivity(va);
                        updateVisitProtocolsVisitActivities(vpva);
                    }
                    success = true;
                } else if (localVisitProtocol == null) {
                    daoVisitProtocol.create(vp);
                    for (VisitActivity va : currentVisitActivities) {
                        VisitProtocolVisitActivity vpva = new VisitProtocolVisitActivity();
                        vpva.setVisitProtocol(vp);
                        vpva.setVisitActivity(va);
                        updateVisitProtocolsVisitActivities(vpva);
                    }
                    success = true;
                }
            }
        } catch (SQLException | JSONException e){
            e.printStackTrace();
        }
        return success;
    }

    public void updateVisitProtocolsVisitActivities(VisitProtocolVisitActivity vpva){
        try {
            QueryBuilder<VisitProtocolVisitActivity, Long> qb = getDaoVisitProtocolVisitActivity().queryBuilder();
            long rows = qb.where().eq("visitProtocol_id",vpva.getVisitProtocol().getId())
                        .and()
                        .eq("visitActivity_id",vpva.getVisitActivity().getId())
                    .countOf();
            if(rows<1){
                getDaoVisitProtocolVisitActivity().create(vpva);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<VisitActivity> getActivitiesForProtocols(VisitProtocol visitProtocol) throws SQLException {
        PreparedQuery<VisitActivity> activitiesForProtocolQuery = makeActivitiesForProtocolsQuery();
        activitiesForProtocolQuery.setArgumentHolderValue(0, visitProtocol.getId());
        return getDaoVisitActivity().query(activitiesForProtocolQuery);
    }

    public List<VisitProtocol> getProtocolsForActivities(VisitActivity visitActivity) throws SQLException {
        PreparedQuery<VisitProtocol>protocolsForActivityQuery = makeProtocolsForActivitiesQuery();
        protocolsForActivityQuery.setArgumentHolderValue(0, visitActivity);
        return getDaoVisitProtocol().query(protocolsForActivityQuery);
    }

    /**
     * Build our query for VisitActivities objects that match a VisitProtocol.
     */
    private PreparedQuery<VisitActivity> makeActivitiesForProtocolsQuery() throws SQLException {
        QueryBuilder<VisitProtocolVisitActivity, Long> visitProtocolVisitActivityLongQb = getDaoVisitProtocolVisitActivity().queryBuilder();
        visitProtocolVisitActivityLongQb.selectColumns("visitActivity_id");
        SelectArg protocolSelectArg = new SelectArg();
        visitProtocolVisitActivityLongQb.where().eq("visitProtocol_id", protocolSelectArg);
        QueryBuilder<VisitActivity, Integer> visitActivityQb = getDaoVisitActivity().queryBuilder();
        visitActivityQb.where().in("id", visitProtocolVisitActivityLongQb);
        return visitActivityQb.prepare();
    }

    /**
     * Build our query for VisitProtocol objects that match a VisitActivities.
     */
    private PreparedQuery<VisitProtocol> makeProtocolsForActivitiesQuery() throws SQLException {
        QueryBuilder<VisitProtocolVisitActivity, Long> userPostQb = getDaoVisitProtocolVisitActivity().queryBuilder();
        userPostQb.selectColumns("visitProtocol_id");
        SelectArg activitySelectArg = new SelectArg();
        userPostQb.where().eq("visitActivity_id", activitySelectArg);
        QueryBuilder<VisitProtocol, Long> visitProtocolQb = getDaoVisitProtocol().queryBuilder();
        visitProtocolQb.where().in("_id", userPostQb);
        visitProtocolQb.where().in("_id", userPostQb);
        return visitProtocolQb.prepare();
    }

    public List<Herd> getHerdsForFarm(Farm farm) throws SQLException {
        PreparedQuery<Herd> herdsForFarmQuery = makeHerdsForFarmsQuery();
        herdsForFarmQuery.setArgumentHolderValue(0, farm.getId());
        return getDaoHerd().query(herdsForFarmQuery);
    }

    public List<Farm> getFarmsForHerd(Herd herd) throws SQLException {
        PreparedQuery<Farm>farmsForHerdQuery = makeFarmsForHerdsQuery();
        farmsForHerdQuery.setArgumentHolderValue(0, herd);
        return getDaoFarm().query(farmsForHerdQuery);
    }

    private PreparedQuery<Herd> makeHerdsForFarmsQuery() throws SQLException {
        QueryBuilder<FarmHerd, Long> farmHerdLongQb = getDaoFarmHerd().queryBuilder();
        farmHerdLongQb.selectColumns("herd_id");
        SelectArg protocolSelectArg = new SelectArg();
        farmHerdLongQb.where().eq("farm_id", protocolSelectArg);
        QueryBuilder<Herd, Long> herdQb = getDaoHerd().queryBuilder();
        herdQb.selectColumns("_id", "herdNumber").where().in("_id", farmHerdLongQb);
        return herdQb.prepare();
    }
    private PreparedQuery<Farm> makeFarmsForHerdsQuery() throws SQLException {
        QueryBuilder<FarmHerd, Long> farmHerdQb = getDaoFarmHerd().queryBuilder();
        farmHerdQb.selectColumns("farm_id");
        SelectArg activitySelectArg = new SelectArg();
        farmHerdQb.where().eq("herd_id", activitySelectArg);
        QueryBuilder<Farm, Long> farmQb = getDaoFarm().queryBuilder();
        farmQb.where().in("_id", farmHerdQb);
        return farmQb.prepare();
    }

    public List<Goat> getGoatsForFarm(Farm farm){
        List<Goat> lg = null;
        try {
            PreparedQuery<Goat> goatsForFarmQuery = makeGoatsForFarmsQuery();
            goatsForFarmQuery.setArgumentHolderValue(0, farm.getId());
            lg = getDaoGoat().query(goatsForFarmQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lg;
    }

    public List<Farm> getFarmsForGoat(Goat goat) throws SQLException {
        PreparedQuery<Farm>farmsForGoatQuery = makeFarmsForGoatsQuery();
        farmsForGoatQuery.setArgumentHolderValue(0, goat);
        return getDaoFarm().query(farmsForGoatQuery);
    }

    public PreparedQuery<Goat> makeGoatsForFarmsQuery() throws SQLException {
        QueryBuilder<FarmGoat, Long> farmGoatLongQb = getDaoFarmGoat().queryBuilder();
        farmGoatLongQb.selectColumns("goat_id");
        SelectArg protocolSelectArg = new SelectArg();
        farmGoatLongQb.where().eq("farm_id", protocolSelectArg);
        QueryBuilder<Goat, Long> goatQb = getDaoGoat().queryBuilder();
        goatQb.where().in("_id", farmGoatLongQb);
        return goatQb.prepare();
    }
    private PreparedQuery<Farm> makeFarmsForGoatsQuery() throws SQLException {
        QueryBuilder<FarmGoat, Long> farmGoatQb = getDaoFarmGoat().queryBuilder();
        farmGoatQb.selectColumns("farm_id");
        SelectArg activitySelectArg = new SelectArg();
        farmGoatQb.where().eq("goat_id", activitySelectArg);
        QueryBuilder<Farm, Long> farmQb = getDaoFarm().queryBuilder();
        farmQb.where().in("_id", farmGoatQb);
        return farmQb.prepare();
    }

    public QueryBuilder<Goat, Long> makeGoatsForFarmsQB() throws SQLException {
        QueryBuilder<FarmGoat, Long> farmGoatLongQb = getDaoFarmGoat().queryBuilder();
        farmGoatLongQb.selectColumns("goat_id");
        SelectArg protocolSelectArg = new SelectArg();
        farmGoatLongQb.where().eq("farm_id", protocolSelectArg);
        QueryBuilder<Goat, Long> goatQb = getDaoGoat().queryBuilder();
        goatQb.where().in("_id", farmGoatLongQb);
        return goatQb;
    }

    public List<Goat> getGoatsForFarmSelectedColumns(Farm farm){
        List<Goat> lg = null;
        try {
            PreparedQuery<Goat> goatsForFarmQuery = makeGoatsForFarmsSelectedcolumnsQuery();
            goatsForFarmQuery.setArgumentHolderValue(0, farm.getId());
            lg = getDaoGoat().query(goatsForFarmQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lg;
    }
    private PreparedQuery<Goat> makeGoatsForFarmsSelectedcolumnsQuery() throws SQLException {
        QueryBuilder<FarmGoat, Long> farmGoatLongQb = getDaoFarmGoat().queryBuilder();
        farmGoatLongQb.selectColumns("goat_id");
        SelectArg protocolSelectArg = new SelectArg();
        farmGoatLongQb.where().eq("farm_id", protocolSelectArg);
        QueryBuilder<Goat, Long> goatQb = getDaoGoat().queryBuilder();
        goatQb.selectColumns("_id", "appliedForSelectionControlYear", "breed_id",
                "firstVeterinaryNumber", "secondVeterinaryNumber", "firstBreedingNumber", "secondBreedingNumber",
                "birthDate", "sex",
                "certificateNumber", "numberInCertificate", "wastageProtocolNumber", "numberInWastageProtocol")
                .where().in("_id", farmGoatLongQb);
        return goatQb.prepare();
    }

}
