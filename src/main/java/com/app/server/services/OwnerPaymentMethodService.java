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

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Services run as singletons
 */

public class OwnerPaymentMethodService {

    private static OwnerPaymentMethodService self;
    private static OwnersService ownersService;
    private ObjectWriter ow;
    private MongoCollection<Document> ownerPaymentCollection = null;

    private OwnerPaymentMethodService() {
        this.ownerPaymentCollection = MongoPool.getInstance().getCollection("ownerPayment");
        ownersService = OwnersService.getInstance();
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

    public ArrayList<OwnerPaymentMethod> getAllOwnerPayments(HttpHeaders headers, String id) {
        try{
            ownersService.checkAuthentication(headers, id);
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

        } catch(Exception e) {
        System.out.println("Failed to update a document");
        return null;

        }

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


    public OwnerPaymentMethod getOneOwnerPayment(HttpHeaders headers, String mainId, String opId) {
        try {
            ownersService.checkAuthentication(headers, mainId);
            BasicDBObject query = new BasicDBObject();
            query.put("ownerId", opId);

            Document item = ownerPaymentCollection.find(query).first();
            if (item == null) {
                return null;
            }
            return convertDocumentToOwnerPayment(item);
        } catch(Exception E){
            System.out.println("Failed to create a document");
            return null;
        }
    }

    public OwnerPaymentMethod createOwnerPayment(HttpHeaders headers, Object request, String ownerId) {

        try {
            ownersService.checkAuthentication(headers, ownerId);
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
        } catch(Exception e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public Object updateOwnerPayment(HttpHeaders headers, String mainId, String opId, Object request) {
        try {
            ownersService.checkAuthentication(headers, mainId);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(opId));

            Document doc = new Document();
            if (json.has("bankAccount"))
                doc.append("bankAccount",json.getString("bankAccount"));

            Document set = new Document("$set", doc);
            ownerPaymentCollection.updateOne(query,set);
            return request;

        } catch(Exception e) {
            System.out.println("Failed to update a document");
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
                item.getString("bankAccount"),
                item.getString("bookingId")
        );
        ownerPayment.setId(item.getObjectId("_id").toString());
        return ownerPayment;
    }

    private Document convertOwnerPaymentToDocument(OwnerPaymentMethod ownerPayment){
        Document doc = new Document("bankAccount", ownerPayment.getBankAccount())
                .append("bookingId", ownerPayment.getBookingId());
        return doc;
    }

    private OwnerPaymentMethod convertJsonToOwnerPayment(JSONObject json){
        OwnerPaymentMethod ownerPayment = new OwnerPaymentMethod(json.getString("bankAccount"),
                json.getString("bookingId"));
        return ownerPayment;
    }
}

