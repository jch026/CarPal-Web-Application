package com.app.server.services;

import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.models.OwnerPaymentMethod;
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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Services run as singletons
 */

public class OwnerPaymentMethodService {

    private static OwnerPaymentMethodService self;
    private ObjectWriter ow;
    private MongoCollection<Document> ownerPaymentCollection = null;

    private OwnerPaymentMethodService() {
        this.ownerPaymentCollection = MongoPool.getInstance().getCollection("ownerPayment");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static OwnerPaymentMethodService getInstance(){
        if (self == null)
            self = new OwnerPaymentMethodService();
        return self;
    }

//    public ArrayList<OwnerPaymentMethod> getAll() {
//        ArrayList<OwnerPaymentMethod> ownerPaymentList = new ArrayList<OwnerPaymentMethod>();
//
//        FindIterable<Document> results = this.ownerPaymentCollection.find();
//        if (results == null) {
//            return ownerPaymentList;
//        }
//        for (Document item : results) {
//            OwnerPaymentMethod ownerPayment = convertDocumentToOwnerPayment(item);
//            ownerPaymentList.add(ownerPayment);
//        }
//        return ownerPaymentList;
//    }

    public ArrayList<OwnerPaymentMethod> getAllOwnerPayments(String id) {
        ArrayList<OwnerPaymentMethod> ownerPaymentList = new ArrayList<OwnerPaymentMethod>();

        FindIterable<Document> results = this.ownerPaymentCollection.find();
        if (results == null) {
            return ownerPaymentList;
        }
        for (Document item : results) {

            OwnerPaymentMethod ownerPayment = convertDocumentToOwnerPayment(item);
            if(ownerPayment.getOwnerId().equals(id)) {
                ownerPaymentList.add(ownerPayment);
            }
        }
        return ownerPaymentList;
    }

//    public OwnerPaymentMethod getOne(String id) {
//        BasicDBObject query = new BasicDBObject();
//        query.put("_id", new ObjectId(id));
//        //query.put("ownerId", id);
//
//        Document item = ownerPaymentCollection.find(query).first();
//        if (item == null) {
//            return null;
//        }
//        return convertDocumentToOwnerPayment(item);
//    }


    public OwnerPaymentMethod getOneOwnerPayment(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("ownerId", id);

        Document item = ownerPaymentCollection.find(query).first();
        if (item == null) {
            return null;
        }
        return convertDocumentToOwnerPayment(item);
    }

    public OwnerPaymentMethod createOwnerPayment(Object request, String ownerId) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            String bookingId = json.getString("bookingId");
            OwnerPaymentMethod ownerPayment = convertJsonToOwnerPayment(json);
            Document doc = convertOwnerPaymentToDocument(ownerPayment);
            ownerPaymentCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            ownerPayment.setId(id.toString());
            ownerPayment.setOwnerId(ownerId);
            ownerPayment.setBookingId(bookingId);
            return ownerPayment;
        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public Object updateOwnerPayment(String id, Object request) {
        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("bankAccount"))
                doc.append("bankAccount",json.getString("bankAccount"));

            Document set = new Document("$set", doc);
            ownerPaymentCollection.updateOne(query,set);
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


//    public Object delete(String id) {
//        BasicDBObject query = new BasicDBObject();
//        query.put("_id", new ObjectId(id));
//
//        ownerPaymentCollection.deleteOne(query);
//
//        return new JSONObject();
//    }
//
//
//    public Object deleteAll() {
//
//        ownerPaymentCollection.deleteMany(new BasicDBObject());
//
//        return new JSONObject();
//    }

    private OwnerPaymentMethod convertDocumentToOwnerPayment(Document item) {
        OwnerPaymentMethod ownerPayment = new OwnerPaymentMethod(
                item.getString("ownerId"),
                item.getString("bankAccount"),
                item.getString("bookingId")
        );
        ownerPayment.setId(item.getObjectId("_id").toString());
        return ownerPayment;
    }

    private Document convertOwnerPaymentToDocument(OwnerPaymentMethod ownerPayment){
        Document doc = new Document("ownerId", ownerPayment.getOwnerId())
                .append("bankAccount", ownerPayment.getBankAccount())
                .append("bookingId", ownerPayment.getBookingId());
        return doc;
    }

    private OwnerPaymentMethod convertJsonToOwnerPayment(JSONObject json){
        OwnerPaymentMethod ownerPayment = new OwnerPaymentMethod( json.getString("ownerId"),
                json.getString("bankAccount"),
                json.getString("bookingId"));
        return ownerPayment;
    }




} // end of main()

