package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.models.Car;
import com.app.server.services.CarsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.JSONObject;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/owners/{ownerId}/cars")

public class CarsHttpService {
    private CarsService service;
    private ObjectWriter ow;


    public CarsHttpService() {
        service = CarsService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @OPTIONS
    @PermitAll
    public Response optionsById() {

        return Response.ok().build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@PathParam("ownerId") String ownerId) {
        return new APPResponse(service.getAllCars(ownerId));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        try{
            Car c = service.getOneCar(id);
            if (c == null)
                throw new APPNotFoundException(25, "Car not found");
            return new APPResponse(service.getOneCar(id));
        }
        catch(IllegalArgumentException e) {
            throw new APPNotFoundException(25, "Car not found");
        }
        catch(Exception e){
            throw new APPInternalServerException(50, "Something happened. Come back later.");
        }
    }

//    @POST
//    @Consumes({ MediaType.APPLICATION_JSON})
//    @Produces({ MediaType.APPLICATION_JSON})
//    public APPResponse create(Object request) {
//        return new APPResponse(service.createCar(request));
//    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request, @PathParam("ownerId") String ownerId){

        return new APPResponse(service.updateCar(id,request));
    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {

        return new APPResponse(service.deleteCar(id));
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete() {

        return new APPResponse(service.deleteAllCars());
    }

}
