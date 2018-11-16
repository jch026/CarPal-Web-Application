package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;

import com.app.server.models.Booking;
import com.app.server.models.Owner;
import com.app.server.models.Car;
import com.app.server.models.OwnerPaymentMethod;

import com.app.server.services.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("owners")

public class OwnersHttpService {
    private OwnersService service;
    private CarsService serviceCS;
    private OwnerPaymentMethodService servicePM;
    private BookingService serviceBooking;
    private NotificationService serviceN;
    private ObjectWriter ow;


    public OwnersHttpService() {
        service = OwnersService.getInstance();
        serviceCS = CarsService.getInstance();
        servicePM = OwnerPaymentMethodService.getInstance();
        serviceBooking = BookingService.getInstance();
        serviceN = NotificationService.getInstance();
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
    public APPResponse getOne(@Context HttpHeaders headers, @PathParam("id") String id) {
        try{
            Owner o = service.getOne(id, headers);
            if (o == null)
                throw new APPNotFoundException(25, "Owner not found");
            return new APPResponse(service.getOne(id, headers));
        }
        catch(IllegalArgumentException e) {
            throw new APPNotFoundException(25, "Owner not found");
        }
        catch(Exception e){
            throw new APPInternalServerException(50, "Something happened. Come back later.");
        }
    }
/*
    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {
        return new APPResponse(service.create(request));
    }
*/

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@Context HttpHeaders headers, @PathParam("id") String id, Object request){

        return new APPResponse(service.update(id, request, headers));

    }

/*    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {

        return new APPResponse(service.delete(id));
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete() {

        return new APPResponse(service.deleteAll());
    }*/

    /*************Subresource Methods for Car******************/

    @GET
    @Path("{id}/cars")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getRenterPayment(@Context HttpHeaders headers, @PathParam("id") String id) {
        try {
            ArrayList<Car> cs = serviceCS.getAllCars(id, headers);
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
    public APPResponse getOneCar(@Context HttpHeaders headers,@PathParam("idtwo") String carId, @PathParam("id") String mainId) {
        try {
            Car cs = serviceCS.getOneCar(carId,mainId,headers);
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
    public APPResponse createCar(@Context HttpHeaders headers,Object request, @PathParam("id") String ownerId) {
        return new APPResponse(serviceCS.createCar(headers,request, ownerId));
    }

    @PATCH
    @Path("{id}/cars/{idtwo}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateCar(@Context HttpHeaders headers,@PathParam("id") String mainId,@PathParam("idtwo") String carId, Object request){

        return new APPResponse(serviceCS.updateCar(headers,mainId,carId,request));

    }


    @DELETE
    @Path("{id}/cars/{idtwo}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse deleteCar(@Context HttpHeaders headers, @PathParam("id") String mainId, @PathParam("idtwo") String carId) {

        return new APPResponse(serviceCS.deleteCar(headers, mainId,carId));
    }

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
    public APPResponse getOwnerPayment(@Context HttpHeaders headers, @PathParam("id") String id) {
        try {
            ArrayList<OwnerPaymentMethod> pm = servicePM.getAllOwnerPayments(headers, id);
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
    @Path("{id}/ownerPayment/{ownerPaymentId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOnePaymentMethod(@Context HttpHeaders headers, @PathParam("id") String mainId, @PathParam("ownerPaymentId") String opId) {
        try {
            OwnerPaymentMethod pm = servicePM.getOneOwnerPayment(headers, mainId, opId);
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

    @POST
    @Path("{id}/ownerPayment")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createOwnerPayment(@Context HttpHeaders headers, Object request, @PathParam("id") String ownerId) {
        return new APPResponse(servicePM.createOwnerPayment(headers, request, ownerId));
    }

    @PATCH
    @Path("{id}/ownerPayment/{ownerPaymentId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateOwnerPayment(@Context HttpHeaders headers, @PathParam("id") String mainId,@PathParam("ownerPaymentId") String opId, Object request){

        return new APPResponse(servicePM.updateOwnerPayment(headers, mainId, opId,request));

    }

    /*************Subresource Methods for Bookings******************/

    @GET
    @Path("{id}/bookings")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getBookings(@Context HttpHeaders headers, @PathParam("id") String id) {
        try {
            ArrayList<Booking> rp = serviceBooking.getAllBookings(id,headers);
            if (rp == null)
                throw new APPNotFoundException(57,"Bookings not found");
            return new APPResponse(rp);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(57,"Bookings not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

    @GET
    @Path("{id}/bookings/{bookingId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOneBooking(@Context HttpHeaders headers,@PathParam("bookingId") String bookingId, @PathParam("id") String mainId) {
        try {
            Booking rp = serviceBooking.getOne(mainId,bookingId, headers);
            if (rp == null)
                throw new APPNotFoundException(57,"Booking not found");
            return new APPResponse(rp);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(57,"Booking not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

    @GET
    @Path("{id}/notifications")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getNotificationsforUser(@Context HttpHeaders headers, @PathParam("id") String renterId, @QueryParam("offset") int offset, @DefaultValue("20") @QueryParam("count") int count) {
        return new APPResponse(serviceN.getNotifications(headers, renterId, offset, count));
    }



}
