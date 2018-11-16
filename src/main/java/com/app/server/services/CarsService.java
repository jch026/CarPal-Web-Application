package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.models.Car;
import com.app.server.models.Owner;
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

import javax.print.Doc;
import javax.ws.rs.core.HttpHeaders;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Services run as singletons
 */

public class CarsService {

    private static CarsService self;
    private static OwnersService ownersService;
    private ObjectWriter ow;
    private MongoCollection<Document> carsCollection = null;

    private CarsService() {
        this.carsCollection = MongoPool.getInstance().getCollection("cars");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ownersService = OwnersService.getInstance();
    }

    public static CarsService getInstance(){
        if (self == null)
            self = new CarsService();
        return self;
    }


    public ArrayList<Car> getAll() {
        ArrayList<Car> cars = new ArrayList<>();
        FindIterable<Document> results = this.carsCollection.find();
        if (results == null) {
            return cars;
        }

        for (Document item : results) {
            Car car = convertDocumentToCar(item);
            cars.add(car);
        }
        return cars;
    }


    public ArrayList<Car> getAllCars(String ownerId, HttpHeaders headers) {
        try{
            ownersService.checkAuthentication(headers,ownerId);
            ArrayList<Car> cars = new ArrayList<>();
            BasicDBObject query = new BasicDBObject();
            query.put("ownerId", new ObjectId(ownerId));

            FindIterable<Document> item = carsCollection.find(query);
            for (Document document : item) {
                Car car = convertDocumentToCar(document);
                cars.add(car);
            }
            return cars;
        }catch (Exception e){
            throw new APPNotFoundException(25,"Not found");
        }
    }

    public Car getOneCar(String carId) {
        Document item;
        try{
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(carId));

            item = carsCollection.find(query).first();
            if (item == null) {
                return null;
            }
        }catch (Exception e){
            throw new APPNotFoundException(25,"Not found");
        }

        return convertDocumentToCar(item);
    }

    public Car getOneCar(String carId, String mainId, HttpHeaders headers) {
        Document item;
        try{
            ownersService.checkAuthentication(headers,mainId);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(carId));

            item = carsCollection.find(query).first();
            if (item == null) {
                return null;
            }
        }catch (Exception e){
            throw new APPNotFoundException(25,"Not found");
        }

        return convertDocumentToCar(item);
    }

    public Car createCar(HttpHeaders headers,Object request, String ownerId) {

        try {
            ownersService.checkAuthentication(headers,ownerId);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Car car = convertJsonToCar(json);
            car.setOwnerId(ownerId);
            Document doc = convertCarToDocument(car);
            carsCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            car.setId(id.toString());

            return car;
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
         } catch (APPBadRequestException e) {
            throw e;
        } catch (APPNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new APPInternalServerException(0, e.getMessage());
        }
    }


    public Object updateCar(HttpHeaders headers,String mainId, String carId, Object request) {
        try {
            ownersService.checkAuthentication(headers,mainId);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(carId));

            Document doc = new Document();
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

        } catch(Exception e) {
            System.out.println("Failed to update a document");
            return null;
        }
    }


    public Object deleteCar(HttpHeaders headers, String mainId, String carId) {
        try {
            ownersService.checkAuthentication(headers, mainId);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(carId));

            carsCollection.deleteOne(query);
            return new JSONObject();

        } catch(Exception e) {
            System.out.println("Failed to update a document");
            return null;
        }
    }


//    public Object deleteAllCars() {
//
//        carsCollection.deleteMany(new BasicDBObject());
//        return new JSONObject();
//    }

    public Car convertDocumentToCar(Document item) {
        Car car = new Car(
                item.getString("carManufacturer"),
                item.getString("carModel"),
                item.getString("carType"),
                item.getString("carYear"),
                item.getString("carRegistration"),
                item.getString("costOfCar"),
                item.getString("carLocation")
        );
        car.setId(item.getObjectId("_id").toString());
        car.setOwnerId(item.getString("ownerId"));
        return car;
    }

    private Document convertCarToDocument(Car car){
        Document doc = new Document("carManufacturer", car.getCarManufacturer())
                .append("carModel", car.getCarModel())
                .append("ownerId", car.getOwnerId())
                .append("carType", car.getCarType())
                .append("carYear", car.getCarYear())
                .append("carRegistration", car.getCarRegistration())
                .append("costOfCar", car.getCostOfCar())
                .append("carLocation", car.getCarLocation());
        return doc;
    }

    private Car convertJsonToCar(JSONObject json){
        Car car = new Car(
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
