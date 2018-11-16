package com.app.server.services;

import com.app.server.http.exceptions.APPBadRequestException;
import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.exceptions.APPUnauthorizedException;
import com.app.server.http.utils.APPCrypt;
import com.app.server.models.Notification;
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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private static NotificationService self;
    private static RentersService rentersService;
    private ObjectWriter ow;
    private MongoCollection<Document> notificationCollection = null;

    private NotificationService() {
        this.notificationCollection = MongoPool.getInstance().getCollection("notifications");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        rentersService = RentersService.getInstance();
    }

    public static NotificationService getInstance(){
        if (self == null)
            self = new NotificationService();
        return self;
    }

    public ArrayList<Notification> getNotifications(HttpHeaders headers, String id, int offset, int count) {

        ArrayList<Notification> notificationList = new ArrayList<Notification>();

        try {
            rentersService.checkAuthentication(headers, id);
            BasicDBObject query = new BasicDBObject();
            query.put("userId", id);

            long resultCount = notificationCollection.count(query);
            FindIterable<Document> results = notificationCollection.find(query).skip(offset).limit(count);
            for (Document item : results) {
                String notificationType = item.getString("notificationType");
                Notification notification = new Notification(
                        notificationType,
                        item.getString("userId")
                );
                notification.setId(item.getObjectId("_id").toString());
                notificationList.add(notification);
            }

            return notificationList;

        } catch (Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99, e.getMessage());
        }
    }

    public Object createNotification(Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        if (!json.has("notificationType"))
            throw new APPBadRequestException(55,"missing notification type");

        Document doc = new Document("notificationType", json.getString("notificationType"))
                .append("userId", json.getString("userId"));
        notificationCollection.insertOne(doc);
        return request;
    }

    public void addNotification(String id,String notificationType) {
        JSONObject json = null;

        Document doc = new Document("notificationType", notificationType)
                .append("userId", id);
        notificationCollection.insertOne(doc);

    }
}
