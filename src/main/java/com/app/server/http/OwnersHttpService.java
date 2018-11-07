package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;

import com.app.server.models.Owner;
import com.app.server.models.Car;
import com.app.server.models.OwnerPaymentMethod;

import com.app.server.services.OwnersService;
import com.app.server.services.CarsService;
import com.app.server.services.OwnerPaymentMethodService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("owners")

public class OwnersHttpService {
    private OwnersService service;
    private CarsService serviceCS;
    private OwnerPaymentMethodService servicePM;
    private ObjectWriter ow;


    public OwnersHttpService() {
        service = OwnersService.getInstance();
        serviceCS = CarsService.getInstance();
        servicePM = OwnerPaymentMethodService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @OPTIONS
    @PermitAll
    public Response optionsById() {

        return Response.ok().build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        return new APPResponse(service.getAll());
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        try{
            Owner o = service.getOne(id);
            if (o == null)
                throw new APPNotFoundException(25, "Owner not found");
            return new APPResponse(service.getOne(id));
        }
        catch(IllegalArgumentException e) {
            throw new APPNotFoundException(25, "Owner not found");
        }
        catch(Exception e){
            throw new APPInternalServerException(50, "Something happened. Come back later.");
        }
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {
        return new APPResponse(service.create(request));
    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request){

        return new APPResponse(service.update(id,request));

    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {

        return new APPResponse(service.delete(id));
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete() {

        return new APPResponse(service.deleteAll());
    }

    /*************Subresource Methods for Car******************/

    @GET
    @Path("{id}/cars")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getRenterPayment(@PathParam("id") String id) {
        try {
            ArrayList<Car> cs = serviceCS.getAllCars(id);
            if (cs == null)
                throw new APPNotFoundException(57,"RenterPaymentMethod not found");
            return new APPResponse(cs);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(57,"RenterPaymentMethod not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

    @GET
    @Path("{id}/cars/{idtwo}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOneCar(@PathParam("idtwo") String id) {
        try {
            Car cs = serviceCS.getOneCar(id);
            if (cs == null)
                throw new APPNotFoundException(57,"RenterPaymentMethod not found");
            return new APPResponse(cs);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(57,"RenterPaymentMethod not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

    @POST
    @Path("{id}/cars")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createCar(Object request, @PathParam("id") String ownerId) {
        return new APPResponse(serviceCS.createCar(request, ownerId));
    }

    @PATCH
    @Path("{id}/cars/{idtwo}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateCar(@PathParam("idtwo") String idtwo, Object request){

        return new APPResponse(serviceCS.updateCar(idtwo,request));

    }


//    @DELETE
//    @Path("{id}")
//    @Produces({ MediaType.APPLICATION_JSON})
//    public APPResponse deleteCar(@PathParam("id") String id) {
//
//        return new APPResponse(serviceCS.deleteCar(id));
//    }

//    @DELETE
//    @Produces({ MediaType.APPLICATION_JSON})
//    public APPResponse deleteAllCars() {
//
//        return new APPResponse(serviceCS.deleteAllCars());
//    }



    /*************Subresource Methods for OwnerPaymentMethod******************/

    @GET
    @Path("{id}/ownerPayment")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOwnerPayment(@PathParam("id") String id) {
        try {
            ArrayList<OwnerPaymentMethod> pm = servicePM.getAllOwnerPayments(id);
            if (pm == null)
                throw new APPNotFoundException(57,"OwnerPaymentMethod not found");
            return new APPResponse(pm);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(57,"OwnerPaymentMethod not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

    @GET
    @Path("{id}/ownerPayment/{id3}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOnePaymentMethod(@PathParam("id3") String id) {
        try {
            OwnerPaymentMethod pm = servicePM.getOneOwnerPayment(id);
            if (pm == null)
                throw new APPNotFoundException(57,"RenterPaymentMethod not found");
            return new APPResponse(pm);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(57,"RenterPaymentMethod not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

    @POST
    @Path("{id}/ownerPayment")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createOwnerPayment(Object request, @PathParam("id") String ownerId) {
        return new APPResponse(servicePM.createOwnerPayment(request));
    }

    @PATCH
    @Path("{id}/ownerPayment/{id3}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateOwnerPayment(@PathParam("id3") String id3, Object request){

        return new APPResponse(servicePM.updateOwnerPayment(id3,request));

    }



}
