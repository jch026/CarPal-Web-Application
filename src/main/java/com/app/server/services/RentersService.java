package com.app.server.services;

import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.models.Renter;
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

public class RentersService {

    private static RentersService self;
    private ObjectWriter ow;
    private MongoCollection<Document> rentersCollection = null;

    private RentersService() {
        this.rentersCollection = MongoPool.getInstance().getCollection("renters");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static RentersService getInstance(){
        if (self == null)
            self = new RentersService();
        return self;
    }

    public ArrayList<Renter> getAll() {
        ArrayList<Renter> renterList = new ArrayList<Renter>();

        FindIterable<Document> results = this.rentersCollection.find();
        if (results == null) {
            return renterList;
        }
        for (Document item : results) {
            Renter renter = convertDocumentToRenter(item);
            renterList.add(renter);
        }
        return renterList;
    }

    public Renter getOne(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Document item = rentersCollection.find(query).first();
        if (item == null) {
            return null;
        }
        return convertDocumentToRenter(item);
    }

    public Renter create(Object request) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Renter renter = convertJsonToRenter(json);
            Document doc = convertRenterToDocument(renter);
            rentersCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            renter.setId(id.toString());
            return renter;
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
            if (json.has("firstName"))
                doc.append("firstName",json.getString("firstName"));
            if (json.has("lastName"))
                doc.append("lastName",json.getString("lastName"));
            if (json.has("username"))
                doc.append("username",json.getString("username"));
            if (json.has("password"))
                doc.append("password",json.getString("password"));
            if (json.has("phoneno"))
                doc.append("phoneno",json.getString("phoneno"));
            if (json.has("license"))
                doc.append("license",json.getString("license"));
            if (json.has("email"))
                doc.append("email",json.getString("email"));

            Document set = new Document("$set", doc);
            rentersCollection.updateOne(query,set);
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

        rentersCollection.deleteOne(query);

        return new JSONObject();
    }


    public Object deleteAll() {

        rentersCollection.deleteMany(new BasicDBObject());

        return new JSONObject();
    }

    private Renter convertDocumentToRenter(Document item) {
        Renter renter = new Renter(
                item.getString("firstName"),
                item.getString("lastName"),
                item.getString("username"),
                item.getString("password"),
                item.getString("phoneno"),
                item.getString("license"),
                item.getString("email")
        );
        renter.setId(item.getObjectId("_id").toString());
        return renter;
    }

    private Document convertRenterToDocument(Renter renter){
        Document doc = new Document("firstName", renter.getFirstName())
                .append("lastName", renter.getLastName())
                .append("username", renter.getUsername())
                .append("password", renter.getPassword())
                .append("phoneno", renter.getPhoneno())
                .append("license", renter.getLicense())
                .append("email", renter.getEmail());
        return doc;
    }

    private Renter convertJsonToRenter(JSONObject json){
        Renter renter = new Renter( json.getString("firstName"),
                json.getString("lastName"),
                json.getString("username"),
                json.getString("password"),
                json.getString("phoneno"),
                json.getString("license"),
                json.getString("email"));
        return renter;
    }




} // end of main()
