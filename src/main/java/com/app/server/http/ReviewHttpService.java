package com.app.server.http;

import com.app.server.http.utils.APPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.app.server.services.ReviewService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("reviews")
public class ReviewHttpService {
    private ReviewService serviceR;
    private ObjectWriter ow;


    public ReviewHttpService() {
        serviceR = ReviewService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    //System Admin
    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {

        return new APPResponse(serviceR.delete(id));
    }
}
