package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.http.utils.APPCrypt;
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
import javax.ws.rs.core.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

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

    public Renter getOne(String id, HttpHeaders headers) {
        Document item;
        try{
            checkAuthentication(headers, id);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            item = rentersCollection.find(query).first();
            if (item == null) {
                return null;
            }
        }catch(Exception e){
            throw new APPUnauthorizedException(55, "Unauthorized access");
        }

        return convertDocumentToRenter(item);
    }

    public Renter create(Object request) throws Exception {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Document doc = convertRenterJSONToDocument(json);
            rentersCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            Renter renter = convertDocumentToRenter(doc);
            renter.setId(id.toString());
            return renter;
        } catch(JsonProcessingException e) {
            System.out.println("Failed to create a document");
            return null;
        }
    }

    public Object update(String id, Object request, HttpHeaders headers) {
        try {
            checkAuthentication(headers, id);
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document set = new Document("$set", convertRenterJSONToDocument(json));
            rentersCollection.updateOne(query,set);
            return request;

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

    /*public Object delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        rentersCollection.deleteOne(query);
        return new JSONObject();
    }


    public Object deleteAll() {
        rentersCollection.deleteMany(new BasicDBObject());
        return new JSONObject();
    }*/

    public Renter convertDocumentToRenter(Document item) {
        Renter renter = new Renter(
                item.getString("firstName"),
                item.getString("lastName"),
                item.getString("username"),
                item.getString("phoneno"),
                item.getString("license"),
                item.getString("email")
        );
        renter.setId(item.getObjectId("_id").toString());
        return renter;
    }

    public Document convertRenterJSONToDocument(JSONObject json) throws Exception {
        Document doc = new Document();
        if (json.has("firstName"))
            doc.append("firstName",json.getString("firstName"));
        if (json.has("lastName"))
            doc.append("lastName",json.getString("lastName"));
        if (json.has("username"))
            doc.append("username",json.getString("username"));
        if (json.has("password"))
            doc.append("password", APPCrypt.encrypt(json.getString("password")));
        if (json.has("phoneno"))
            doc.append("phoneno",json.getString("phoneno"));
        if (json.has("license"))
            doc.append("license",json.getString("license"));
        if (json.has("email"))
            doc.append("email",json.getString("email"));
        return doc;
    }

    void checkAuthentication(HttpHeaders headers,String id) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        if (id.compareTo(clearToken) != 0) {
            throw new APPUnauthorizedException(71,"Invalid token. Please try getting a new token");
        }
    }
} // end of main()
