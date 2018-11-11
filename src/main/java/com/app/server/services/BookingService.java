package com.app.server.services;

import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
//import com.app.server.models.Car;
import com.app.server.models.Booking;
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

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Services run as singletons
 */

public class BookingService {

    private static BookingService self;
    private ObjectWriter ow;
    private MongoCollection<Document> bookingCollection = null;

    private BookingService() {
        this.bookingCollection = MongoPool.getInstance().getCollection("bookings");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static BookingService getInstance(){
        if (self == null)
            self = new BookingService();
        return self;
    }

    public ArrayList<Booking> getAllBookings(String id) {
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        BasicDBObject query = new BasicDBObject();
        try{
            query.put("renterId", new ObjectId(id));
            FindIterable<Document> item = bookingCollection.find(query);
            for (Document document : item) {
                Booking booking = convertDocumentToBooking(document);
                bookings.add(booking);
            }

        }catch (Exception e) {
            query.put("ownerId", new ObjectId(id));
            FindIterable<Document> item = bookingCollection.find(query);
            for (Document document : item) {
                Booking booking = convertDocumentToBooking(document);
                bookings.add(booking);
            }
        }
        return bookings;
    }

    public Booking getOne(String id) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Document item = bookingCollection.find(query).first();
        if (item == null) {
            return null;
        }
        return convertDocumentToBooking(item);
    }

    public Booking create(Object request, String renterId) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            String carId = json.getString("carId");
            Booking booking = convertJsonToBooking(json);

            Document doc = convertBookingToDocument(booking);
            bookingCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            booking.setId(id.toString());
            booking.setRenterId(renterId);
            CarsService cs = CarsService.getInstance();
            Car car = cs.getOneCar(carId);
            String ownerId = car.getOwnerId();
            booking.setOwnerId(ownerId);
            booking.setCarId(carId);
            return booking;
        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public Object update(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("date"))
                doc.append("date",json.getString("date"));
            if (json.has("startTime"))
                doc.append("startTime",json.getString("startTime"));
            if (json.has("endTime"))
                doc.append("endTime",json.getString("endTime"));
            if (json.has("pickupAddress"))
                doc.append("pickupAddress",json.getString("pickupAddress"));

            Document set = new Document("$set", doc);
            bookingCollection.updateOne(query,set);
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


    public Object delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        bookingCollection.deleteOne(query);

        return new JSONObject();
    }


    public Object deleteAll() {

        bookingCollection.deleteMany(new BasicDBObject());

        return new JSONObject();
    }

    private Booking convertDocumentToBooking(Document item) {
        Booking booking = new Booking(
                item.getString("ownerId"),
                item.getString("renterId"),
                item.getString("carId"),
                item.getString("costOfCar"),
                item.getString("pickupAddress"),
                item.getString("status"),
                item.getString("startTime"),
                item.getString("endTime"),
                item.getString("date"));
        booking.setId(item.getObjectId("_id").toString());
        return booking;
    }

    private Document convertBookingToDocument(Booking booking){
        Document doc = new Document("ownerId", booking.getOwnerId())
                .append("renterId", booking.getRenterId())
                .append("carId", booking.getCarId())
                .append("costOfCar", booking.getCostOfCar())
                .append("date", booking.getDate())
                .append("startTime", booking.getStartTime())
                .append("endTime", booking.getEndTime())
                .append("pickupAddress", booking.getPickupAddress())
                .append("status", booking.getStatus());
        return doc;
    }

    private Booking convertJsonToBooking(JSONObject json){
        Booking booking = new Booking( json.getString("ownerId"),
                json.getString("renterId"),
                json.getString("carId"),
                json.getString("costOfCar"),
                json.getString("date"),
                json.getString("startTime"),
                json.getString("endTime"),
                json.getString("pickupAddress"),
                json.getString("status"));
        return booking;
    }

} // end of main()



