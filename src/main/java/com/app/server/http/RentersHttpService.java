package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.models.Renter;
import com.app.server.models.RenterPaymentMethod;
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


    public RentersHttpService() {
        service = RentersService.getInstance();
        serviceRP = RenterPaymentMethodService.getInstance();
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
    @Path("{id}/renterPayment/{idtwo}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getRenterPaymentOne(@PathParam("idtwo") String id) {
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

    @PATCH
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request){

        return new APPResponse(service.update(id,request));

    }

    @PATCH
    @Path("{id}/renterPayment/{idtwo}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse updateRenterPayment(@PathParam("idtwo") String idtwo, Object request){

        return new APPResponse(serviceRP.update(idtwo,request));

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
