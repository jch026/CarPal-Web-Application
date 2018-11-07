package com.app.server.services;

import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
//import com.app.server.models.Car;
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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Services run as singletons
 */

public class OwnersService {

    private static OwnersService self;
    private ObjectWriter ow;
    private MongoCollection<Document> ownersCollection = null;

    private OwnersService() {
        this.ownersCollection = MongoPool.getInstance().getCollection("owners");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    public static OwnersService getInstance(){
        if (self == null)
            self = new OwnersService();
        return self;
    }

    public ArrayList<Owner> getAll() {
        ArrayList<Owner> ownerList = new ArrayList<Owner>();

        FindIterable<Document> results = this.ownersCollection.find();
        if (results == null) {
            return ownerList;
        }
        for (Document item : results) {
            Owner owner = convertDocumentToOwner(item);
            ownerList.add(owner);
        }
        return ownerList;
    }

    public Owner getOne(String id) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        Document item = ownersCollection.find(query).first();
        if (item == null) {
            return null;
        }
        return convertDocumentToOwner(item);
    }

    public Owner create(Object request) {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Owner owner = convertJsonToOwner(json);
            Document doc = convertOwnerToDocument(owner);
            ownersCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            owner.setId(id.toString());
            return owner;
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
            if (json.has("phoneNumber"))
                doc.append("phoneNumber",json.getString("phoneNumber"));
            if (json.has("username"))
                doc.append("username",json.getString("username"));
            if (json.has("password"))
                doc.append("password",json.getString("password"));
            if (json.has("email"))
                doc.append("email",json.getString("email"));
            if (json.has("license"))
                doc.append("license",json.getString("license"));
            if (json.has("accountNumber"))
                doc.append("accountNumber",json.getString("accountNumber"));

            Document set = new Document("$set", doc);
            ownersCollection.updateOne(query,set);
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

        ownersCollection.deleteOne(query);

        return new JSONObject();
    }


    public Object deleteAll() {

        ownersCollection.deleteMany(new BasicDBObject());

        return new JSONObject();
    }

    private Owner convertDocumentToOwner(Document item) {
        Owner owner = new Owner(
                item.getString("firstName"),
                item.getString("lastName"),
                item.getString("phoneNumber"),
                item.getString("username"),
                item.getString("password"),
                item.getString("email"),
                item.getString("license"),
                item.getString("accountNumber")
        );
        owner.setId(item.getObjectId("_id").toString());
        return owner;
    }

    private Document convertOwnerToDocument(Owner owner){
        Document doc = new Document("firstName", owner.getFirstName())
                .append("lastName", owner.getLastName())
                .append("phoneNumber", owner.getPhoneNumber())
                .append("username", owner.getUsername())
                .append("password", owner.getPassword())
                .append("email", owner.getEmail())
                .append("license", owner.getLicense())
                .append("accountNumber", owner.getAccountNumber());
        return doc;
    }

    private Owner convertJsonToOwner(JSONObject json){
        Owner owner = new Owner( json.getString("firstName"),
                json.getString("lastName"),
                json.getString("phoneNumber"),
                json.getString("username"),
                json.getString("password"),
                json.getString("email"),
                json.getString("license"),
                json.getString("accountNumber"));
        return owner;
    }

} // end of main()



