package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.http.utils.APPCrypt;
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
import javax.ws.rs.core.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

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

    public Owner getOne(String id, HttpHeaders headers) {
        Document item;

        try {
            checkAuthentication(headers, id);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            item = ownersCollection.find(query).first();
            if (item == null) {
                return null;
            }
        }catch(Exception e){
            throw new APPUnauthorizedException(55, "Unauthorized access");
        }
        return convertDocumentToOwner(item);
    }

    public Owner create(Object request) throws Exception {

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            Document doc = convertOwnerJSONToDocument(json);
            ownersCollection.insertOne(doc);
            ObjectId id = (ObjectId)doc.get( "_id" );
            Owner owner = convertDocumentToOwner(doc);
            owner.setId(id.toString());
            return owner;
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

            Document set = new Document("$set", convertOwnerJSONToDocument(json));
            ownersCollection.updateOne(query,set);
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
        ownersCollection.deleteOne(query);
        return new JSONObject();
    }

    public Object deleteAll() {

        ownersCollection.deleteMany(new BasicDBObject());

        return new JSONObject();
    }*/

    public Owner convertDocumentToOwner(Document item) {
        Owner owner = new Owner(
                item.getString("firstName"),
                item.getString("lastName"),
                item.getString("phoneNumber"),
                item.getString("username"),
                item.getString("email"),
                item.getString("license"),
                item.getString("accountNumber")
        );
        owner.setId(item.getObjectId("_id").toString());
        return owner;
    }

    public Document convertOwnerJSONToDocument(JSONObject json) throws Exception {

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
            doc.append("password", APPCrypt.encrypt(json.getString("password")));
        if (json.has("email"))
            doc.append("email",json.getString("email"));
        if (json.has("license"))
            doc.append("license",json.getString("license"));
        if (json.has("accountNumber"))
            doc.append("accountNumber",json.getString("accountNumber"));

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



