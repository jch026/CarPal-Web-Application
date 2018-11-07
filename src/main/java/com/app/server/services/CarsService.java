package com.app.server.services;

import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Services run as singletons
 */

public class CarsService {

    private static CarsService self;
    private ObjectWriter ow;
    private MongoCollection<Document> carsCollection = null;

    private CarsService() {
        this.carsCollection = MongoPool.getInstance().getCollection("cars");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static CarsService getInstance(){
        if (self == null)
            self = new CarsService();
        return self;
    }

    public ArrayList<Car> getAllCars(String ownerId) {
        ArrayList<Car> cars = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("ownerId", new ObjectId(ownerId));

        FindIterable<Document> item = carsCollection.find(query);
        for (Document document : item) {
            Car car = convertDocumentToCar(document);
            cars.add(car);

        }

        return cars;
    }

    public Car getOneCar(String id) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Document item = carsCollection.find(query).first();
        if (item == null) {
            return null;
        }
        return convertDocumentToCar(item);
    }

    public Car createCar(Object request, String ownerId) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Car car = convertJsonToCar(json);
            Document doc = convertCarToDocument(car);
            carsCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            car.setId(id.toString());

            car.setOwnerId(ownerId.toString());

            return car;
        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public Object updateCar(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("ownerId"))
                doc.append("ownerId",json.getString("ownerId"));
            if (json.has("carManufacturer"))
                doc.append("carManufacturer",json.getString("carManufacturer"));
            if (json.has("carModel"))
                doc.append("carModel",json.getString("carModel"));
            if (json.has("carType"))
                doc.append("carType",json.getString("carType"));
            if (json.has("carYear"))
                doc.append("carYear",json.getString("carYear"));
            if (json.has("carRegistration"))
                doc.append("carRegistration",json.getString("carRegistration"));
            if (json.has("costOfCar"))
                doc.append("costOfCar",json.getString("costOfCar"));
            if (json.has("carLocation"))
                doc.append("carLocation",json.getString("carLocation"));

            Document set = new Document("$set", doc);
            carsCollection.updateOne(query,set);
            return request;

        } catch(JSONException e) {
            System.out.println("Failed to update a document");
            return null;


        }
        catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public Object deleteCar(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        carsCollection.deleteOne(query);

        return new JSONObject();
    }


    public Object deleteAllCars() {

        carsCollection.deleteMany(new BasicDBObject());

        return new JSONObject();
    }

    private Car convertDocumentToCar(Document item) {
        Car car = new Car(
                item.getString("ownerId"),
                item.getString("carManufacturer"),
                item.getString("carModel"),
                item.getString("carType"),
                item.getString("carYear"),
                item.getString("carRegistration"),
                item.getString("costOfCar"),
                item.getString("carLocation")
        );
        car.setId(item.getObjectId("_id").toString());
        return car;
    }

    private Document convertCarToDocument(Car car){
        Document doc = new Document("ownerId", car.getOwnerId())
                .append("carManufacturer", car.getCarManufacturer())
                .append("carModel", car.getCarModel())
                .append("carType", car.getCarType())
                .append("carYear", car.getCarYear())
                .append("carRegistration", car.getCarRegistration())
                .append("costOfCar", car.getCostOfCar())
                .append("carLocation", car.getCarLocation());
        return doc;
    }

    private Car convertJsonToCar(JSONObject json){
        Car car = new Car( json.getString("ownerId"),
                json.getString("carManufacturer"),
                json.getString("carModel"),
                json.getString("carType"),
                json.getString("carYear"),
                json.getString("carRegistration"),
                json.getString("costOfCar"),
                json.getString("carLocation"));
        return car;
    }

} // end of main()
