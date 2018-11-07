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
    public APPResponse getOne(@PathParam("id") String id) {
        try {
            Renter r = service.getOne(id);
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
    public APPResponse getRenterPayment(@PathParam("id") String id) {
        try {
            ArrayList<RenterPaymentMethod> rp = serviceRP.getAllFromMain(id);
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
    public APPResponse getRenterPaymentOne(@PathParam("renterPaymentId") String id) {
        try {
            RenterPaymentMethod rp = serviceRP.getOne(id);
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
    public APPResponse getBookings(@PathParam("id") String id) {
        try {
            ArrayList<Booking> rp = serviceBooking.getAllBookings(id);
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
    public APPResponse getOneBooking(@PathParam("bookingId") String id) {
        try {
            Booking rp = serviceBooking.getOne(id);
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


    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {
        return new APPResponse(service.create(request));
    }


    @POST
    @Path("{id}/renterPayment")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createRenterPaymentMethod(Object request, @PathParam("id") String renterId) {
        return new APPResponse(serviceRP.create(request));
    }

    @POST
    @Path("{id}/bookings")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createBookingMethod(Object request, @PathParam("id") String renterId, String carId) {
        return new APPResponse(serviceBooking.create(request, renterId, carId));
    }

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request){

        return new APPResponse(service.update(id,request));

    }

    @PATCH
    @Path("{id}/renterPayment/{renterPaymentId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateRenterPayment(@PathParam("renterPaymentId") String renterPaymentId, Object request){

        return new APPResponse(serviceRP.update(renterPaymentId,request));

    }

    @PATCH
    @Path("{id}/bookings/{bookingId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateBooking(@PathParam("bookingId") String bookingId, Object request){

        return new APPResponse(serviceRP.update(bookingId,request));

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

}
