package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.http.utils.APPResponse;
import com.app.server.models.Review;
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
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.server.services.RentersService;
import com.app.server.services.CarsService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ReviewService {

    private static ReviewService self;
    private static RentersService rentersService;
    private static NotificationService serviceN;
    private ObjectWriter ow;
    private MongoCollection<Document> reviewCollection = null;

    private ReviewService() {
        this.reviewCollection = MongoPool.getInstance().getCollection("reviews");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        rentersService = RentersService.getInstance();
        serviceN = NotificationService.getInstance();
    }

    public static ReviewService getInstance() {
        if (self == null)
            self = new ReviewService();
        return self;
    }

    public ArrayList<Review> getAllReviews(HttpHeaders headers,String renterId, String bookingId){

        ArrayList<Review> reviewsList = new ArrayList<Review>();
        BasicDBObject query = new BasicDBObject();
        query.put("bookingId", bookingId);


        try {
            rentersService.checkAuthentication(headers,renterId);
            FindIterable<Document> results = reviewCollection.find(query);
            for (Document item : results) {
                String textContent = item.getString("textContent");
                Review review = new Review(
                        textContent,
                        item.getInteger("ratings"),
                        item.getString("userId"),
                        item.getString("bookingId")
                );
                review.setId(item.getObjectId("_id").toString());
                review.setBookingId(item.getString("bookingId"));
                reviewsList.add(review);
            }
            if(reviewsList.size()==0){
                throw new APPNotFoundException(0,"Sorry, no review found");
            }
            return reviewsList;

        }   catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"Sorry, no review found");
        }
        catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }

    public Object createReview(HttpHeaders headers, Object request, String renterId, String bookingId) {
        JSONObject json = null;
        BasicDBObject query = new BasicDBObject();

        try {
            rentersService.checkAuthentication(headers, renterId);

            json = new JSONObject(ow.writeValueAsString(request));
/*            query.put("bookingId", bookingId);
            Document item = reviewCollection.find(query).first();
            if (item == null) {
                return null;
                //throw new APPNotFoundException(0, "No reviews found");
            }
        }catch(APPBadRequestException e){
            throw new APPBadRequestException(45, "Sorry, doesn't look like a mongoDB Id");
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(45, "Sorry, doesn't look like a mongoDB Id");
        }
        catch(APPUnauthorizedException e) {
            throw new APPUnauthorizedException(71, "Unauthorized access. Please sign in to access!");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new APPInternalServerException(99,"Something went wrong, Internal server exception!");
        }*/

        if (!json.has("textContent"))
            throw new APPBadRequestException(55,"missing review content");
        if (!json.has("ratings"))
            throw new APPBadRequestException(55,"missing ratings");
        //try {
            Document doc = new Document("textContent", json.getString("textContent"))
                    .append("ratings", json.getInt("ratings"))
                    .append("bookingId", bookingId)
                    .append("userId", renterId);
            reviewCollection.insertOne(doc);
        } catch (IllegalArgumentException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        catch (Exception e) {
            throw new APPInternalServerException(99,"Something went wrong, Internal server exception!");
        }
        serviceN.addNotification(renterId,"Added a Review");
        return request;
    }

    public Object delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Document item = reviewCollection.find(query).first();
        if (item == null) {
            throw new APPNotFoundException(0, "Sorry, this review doesn't exist");
        }

        DeleteResult deleteResult = reviewCollection.deleteOne(query);
        if (deleteResult.getDeletedCount() < 1)
            throw new APPNotFoundException(66,"Could not delete");

        return new JSONObject();
    }
}
