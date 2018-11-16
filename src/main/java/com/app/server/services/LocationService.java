package com.app.server.services;

import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.http.utils.APPResponse;
import com.app.server.models.Car;
import com.app.server.models.Booking;
import com.app.server.models.Location;
import com.app.server.models.Car;
import com.app.server.util.MongoPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.server.services.RentersService;
import com.app.server.services.CarsService;

import javax.ws.rs.core.HttpHeaders;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Services run as singletons
 */

public class LocationService {

    private static LocationService self;
    private static CarsService carsService;
    private static RentersService renterService;
    private ObjectWriter ow;
    private MongoCollection<Document> locationCollection = null;
    private MongoCollection<Document> carsCollection = null;

    private LocationService() {
        this.locationCollection = MongoPool.getInstance().getCollection("locations");
        this.carsCollection = MongoPool.getInstance().getCollection("cars");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        carsService = CarsService.getInstance();
        renterService = RentersService.getInstance();
    }

    public static LocationService getInstance(){
        if (self == null)
            self = new LocationService();
        return self;
    }

    public ArrayList<Car> getAllCarByLoc(String id) {
        ArrayList<Car> cars = new ArrayList<Car>();
        BasicDBObject query = new BasicDBObject();

        try{
            Location loc = getOne(id);
            String locationName = loc.getLocationName();
            query.put("carLocation", locationName);
            FindIterable<Document> item = carsCollection.find(query);
            for (Document document : item) {
                Car car = carsService.convertDocumentToCar(document);
                cars.add(car);
            }

        }catch (Exception e) {
                throw new APPNotFoundException(25,"Not found");
            }
        return cars;
    }

    public Location getOne(String id){
        Document item;
        try{
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            item = locationCollection.find(query).first();
            if (item == null) {
                return null;
            }
        }catch (Exception e){
            throw new APPNotFoundException(25,"Not found");
        }
        return convertDocumentToLocation(item);
    }

    public Location create(Object request) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            Location location = convertJsonToLocation(json);
            Document doc = convertLocationToDocument(location);
            locationCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            location.setId(id.toString());
            return location;
        } catch(Exception e) {
            System.out.println("Failed to create a document");
            throw new APPUnauthorizedException(30, "Error");
        }
    }


    private Location convertDocumentToLocation(Document item) {
        Location location = new Location(
                item.getString("locationName"));
        location.setId(item.getObjectId("_id").toString());
        return location;
    }

    private Document convertLocationToDocument(Location location){
        Document doc = new Document("locationName", location.getLocationName());
        return doc;
    }

    private Location convertJsonToLocation(JSONObject json){
        Location location = new Location(
                json.getString("locationName"));
        return location;
    }


} // end of main()



