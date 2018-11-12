package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.models.Booking;
import com.app.server.models.Renter;
import com.app.server.models.RenterPaymentMethod;
import com.app.server.services.BookingService;
import com.app.server.services.RenterPaymentMethodService;
import com.app.server.services.RentersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

//import com.app.server.http.RenterPaymentMethodHttpService;

@Path("renters")

public class RentersHttpService {
    private RentersService service;
    private ObjectWriter ow;
    private RenterPaymentMethodService serviceRP;
    private BookingService serviceBooking;


    public RentersHttpService() {
        service = RentersService.getInstance();
        serviceRP = RenterPaymentMethodService.getInstance();
        serviceBooking = BookingService.getInstance();
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
        try {
            Renter r = service.getOne(id, headers);
            if (r == null)
                throw new APPNotFoundException(56,"Renter not found");
            return new APPResponse(r);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(56,"Renter not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

    @GET
    @Path("{id}/renterPayment")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getRenterPayment(@Context HttpHeaders headers, @PathParam("id") String id) {
        try {
            ArrayList<RenterPaymentMethod> rp = serviceRP.getAllFromMain(id, headers);
            if (rp == null)
                throw new APPNotFoundException(57,"RenterPaymentMethod not found");
            return new APPResponse(rp);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(57,"RenterPaymentMethod not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

    @GET
    @Path("{id}/renterPayment/{renterPaymentId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getRenterPaymentOne(@Context HttpHeaders headers, @PathParam("renterPaymentId") String id) {
        try {
            RenterPaymentMethod rp = serviceRP.getOne(id, headers);
            if (rp == null)
                throw new APPNotFoundException(57,"RenterPaymentMethod not found");
            return new APPResponse(rp);
        }
        catch(IllegalArgumentException e){
            throw new APPNotFoundException(57,"RenterPaymentMethod not found");
        }
        catch (Exception e) {
            throw new APPInternalServerException(0,"Something happened. Come back later.");
        }

    }

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
    public APPResponse getOneBooking(@Context HttpHeaders headers, @PathParam("bookingId") String bookingId,@PathParam("id") String mainId) {
        try {
            Booking rp = serviceBooking.getOne(mainId,bookingId,headers);
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

/*
    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {
        return new APPResponse(service.create(request));
    }
*/

/************Subresource Renter Payment********************/

    @POST
    @Path("{id}/renterPayment")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createRenterPaymentMethod(@Context HttpHeaders headers, Object request, @PathParam("id") String renterId) {
        return new APPResponse(serviceRP.create(headers, request, renterId));
    }

    @POST
    @Path("{id}/bookings")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createBookingMethod(@Context HttpHeaders headers, Object request, @PathParam("id") String renterId) {
        return new APPResponse(serviceBooking.create(headers, request, renterId));
    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@Context HttpHeaders headers, @PathParam("id") String id, Object request){

        return new APPResponse(service.update(id,request, headers));

    }

    @PATCH
    @Path("{id}/renterPayment/{renterPaymentId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateRenterPayment(@Context HttpHeaders headers, @PathParam("id") String mainId, @PathParam("renterPaymentId") String renterPaymentId, Object request){

        return new APPResponse(serviceRP.update(headers, mainId, renterPaymentId,request));

    }

    @PATCH
    @Path("{id}/bookings/{bookingId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateBooking(@Context HttpHeaders headers, @PathParam("id") String mainId, @PathParam("bookingId") String bookingId, Object request){

        return new APPResponse(serviceBooking.update(headers, mainId, bookingId, request));

    }

    /*@DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {

        return new APPResponse(serviceRP.delete(id));
    }

    @DELETE
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete() {

        return new APPResponse(serviceRP.deleteAll());
    }*/

}
