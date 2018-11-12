package com.app.server.services;

import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.models.Renter;
import com.app.server.models.RenterPaymentMethod;
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

public class RenterPaymentMethodService {

    private static RenterPaymentMethodService self;
    private static RentersService renterService;
    private ObjectWriter ow;
    private MongoCollection<Document> renterPaymentCollection = null;

    private RenterPaymentMethodService() {
        this.renterPaymentCollection = MongoPool.getInstance().getCollection("renterPayment");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        renterService = RentersService.getInstance();
    }

    public static RenterPaymentMethodService getInstance(){
        if (self == null)
            self = new RenterPaymentMethodService();
        return self;
    }

    public ArrayList<RenterPaymentMethod> getAll() {
        ArrayList<RenterPaymentMethod> renterPaymentList = new ArrayList<RenterPaymentMethod>();

        FindIterable<Document> results = this.renterPaymentCollection.find();
        if (results == null) {
            return renterPaymentList;
        }
        for (Document item : results) {
            RenterPaymentMethod renterPayment = convertDocumentToRenterPayment(item);
            renterPaymentList.add(renterPayment);
        }
        return renterPaymentList;
    }

    public ArrayList<RenterPaymentMethod> getAllFromMain(String id, HttpHeaders headers) {
        ArrayList<RenterPaymentMethod> renterPaymentList = new ArrayList<RenterPaymentMethod>();
        try {
            renterService.checkAuthentication(headers,id);
            FindIterable<Document> results = this.renterPaymentCollection.find();
            if (results == null) {
                return renterPaymentList;
            }
            for (Document item : results) {

                RenterPaymentMethod renterPayment = convertDocumentToRenterPayment(item);
                if(renterPayment.getRenterId().equals(id)) {
                    renterPaymentList.add(renterPayment);
                }
            }
            return renterPaymentList;
        }catch(Exception e){
            throw new APPNotFoundException(25,"Not found");
        }

    }

    public RenterPaymentMethod getOne(String id, HttpHeaders headers) {

        Document item;
        try {

            renterService.checkAuthentication(headers, id);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            //query.put("renterId", id);

            item = renterPaymentCollection.find(query).first();
            if (item == null) {
                return null;
            }
        }catch (Exception e){
            throw new APPNotFoundException(25,"Not found");
        }
        return convertDocumentToRenterPayment(item);
    }


    /*public RenterPaymentMethod getOneFromMain(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("renterId", id);

        Document item = renterPaymentCollection.find(query).first();
        if (item == null) {
            return null;
        }
        return convertDocumentToRenterPayment(item);
    }*/

    public RenterPaymentMethod create(HttpHeaders headers, Object request, String renterId) {

        try {
            renterService.checkAuthentication(headers, renterId);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            RenterPaymentMethod renterPayment = convertJsonToRenterPayment(json);
            Document doc = convertRenterPaymentToDocument(renterPayment);
            renterPaymentCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            renterPayment.setId(id.toString());
            renterPayment.setRenterId(renterId);
            return renterPayment;

        } catch(Exception e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }


    public Object update(HttpHeaders headers, String mainId, String rpId, Object request) {
        try {
            renterService.checkAuthentication(headers, mainId);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(rpId));

            Document doc = new Document();
            if (json.has("paymentMode"))
                doc.append("paymentMode", json.getString("paymentMode"));
            if (json.has("billingAddress"))
                doc.append("billingAddress", json.getString("billingAddress"));
            if (json.has("creditCardNo"))
                doc.append("creditCardNo", json.getString("creditCardNo"));

            Document set = new Document("$set", doc);
            renterPaymentCollection.updateOne(query, set);
            return request;

        } catch (Exception e) {
            System.out.println("Failed to update a document");
            return null;
        }
    }


    public Object delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        renterPaymentCollection.deleteOne(query);

        return new JSONObject();
    }


    public Object deleteAll() {

        renterPaymentCollection.deleteMany(new BasicDBObject());

        return new JSONObject();
    }

    private RenterPaymentMethod convertDocumentToRenterPayment(Document item) {
        RenterPaymentMethod renterPayment = new RenterPaymentMethod(
                item.getString("paymentMode"),
                item.getString("billingAddress"),
                item.getString("creditCardNo")
        );
        renterPayment.setId(item.getObjectId("_id").toString());
        return renterPayment;
    }

    private Document convertRenterPaymentToDocument(RenterPaymentMethod renterPayment){
        Document doc = new Document("paymentMode", renterPayment.getPaymentMode())
                .append("billingAddress", renterPayment.getBillingAddress())
                .append("creditCardNo", renterPayment.getCreditCardNo());
        return doc;
    }

    private RenterPaymentMethod convertJsonToRenterPayment(JSONObject json){
        RenterPaymentMethod renterPayment = new RenterPaymentMethod( json.getString("paymentMode"),
                json.getString("billingAddress"),
                json.getString("creditCardNo"));
        return renterPayment;
    }

}
