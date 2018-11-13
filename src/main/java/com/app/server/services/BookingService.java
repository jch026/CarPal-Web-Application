package com.app.server.services;

import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.exceptions.APPUnauthorizedException;
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

import javax.ws.rs.core.HttpHeaders;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Services run as singletons
 */

public class BookingService {

    private static BookingService self;
    private static RentersService renterService;
    private ObjectWriter ow;
    private MongoCollection<Document> bookingCollection = null;

    private BookingService() {
        this.bookingCollection = MongoPool.getInstance().getCollection("bookings");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        renterService = RentersService.getInstance();

    }

    public static BookingService getInstance(){
        if (self == null)
            self = new BookingService();
        return self;
    }

    public ArrayList<Booking> getAllBookings(String id, HttpHeaders headers) {
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        BasicDBObject query = new BasicDBObject();
        try{
            renterService.checkAuthentication(headers,id);
            query.put("renterId", new ObjectId(id));
            FindIterable<Document> item = bookingCollection.find(query);
            for (Document document : item) {
                Booking booking = convertDocumentToBooking(document);
                bookings.add(booking);
            }

        }catch (Exception e) {
            try{
                renterService.checkAuthentication(headers,id);
                query.put("ownerId", new ObjectId(id));
                FindIterable<Document> item = bookingCollection.find(query);
                for (Document document : item) {
                    Booking booking = convertDocumentToBooking(document);
                    bookings.add(booking);
                }
            }catch (Exception e1){
                throw new APPNotFoundException(25,"Not found");
            }

        }
        return bookings;
    }

    public Booking getOne(String mainId,String bookingId, HttpHeaders headers) {
        Document item;
        try{
            renterService.checkAuthentication(headers,mainId);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(bookingId));

            item = bookingCollection.find(query).first();
            if (item == null) {
                return null;
            }
        }catch (Exception e){
            throw new APPNotFoundException(25,"Not found");
        }
        return convertDocumentToBooking(item);
    }

    public Booking create(HttpHeaders headers, Object request, String renterId) {

        try {
            renterService.checkAuthentication(headers,renterId);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            String carId = json.getString("carId");
            CarsService cs = CarsService.getInstance();
            Car car = cs.getOneCar(carId);
            String ownerId = car.getOwnerId();

            Booking booking = convertJsonToBooking(json);
            booking.setOwnerId(ownerId);
            booking.setCarId(carId);
            booking.setRenterId(renterId);

            Document doc = convertBookingToDocument(booking);
            bookingCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            booking.setId(id.toString());
            return booking;
        } catch(Exception e) {
            System.out.println("Failed to create a document");
            throw new APPUnauthorizedException(30, "Error");
        }
    }


    public Object update(HttpHeaders headers, String mainId, String bookingId, Object request) {
        try {
            renterService.checkAuthentication(headers, mainId);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(bookingId));

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

        } catch(Exception e) {
            System.out.println("Failed to update a document");
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
                item.getString("carId"),
                item.getString("costOfCar"),
                item.getString("pickupAddress"),
                item.getString("status"),
                item.getString("startTime"),
                item.getString("endTime"),
                item.getString("date"));
        booking.setId(item.getObjectId("_id").toString());
        booking.setOwnerId(item.getString("ownerId"));
        booking.setRenterId(item.getString("renterId"));
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
        Booking booking = new Booking(
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



