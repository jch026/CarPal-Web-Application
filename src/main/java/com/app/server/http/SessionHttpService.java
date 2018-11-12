package com.app.server.http;

import com.app.server.http.utils.APPResponse;
import com.app.server.services.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("sessions")

public class SessionHttpService {
    private SessionService service;
    private ObjectWriter ow;


    public SessionHttpService() {
        service = SessionService.getInstance();
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @OPTIONS
    @PermitAll
    public Response optionsById() {

        return Response.ok().build();
    }

    @POST
    @Path("/renters/login")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse renterSignIn(Object request) {

        return new APPResponse(service.createRenterSession(request));
    }

    @POST
    @Path("/renters/signup")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse renterSignUp(Object request) {

        return new APPResponse(service.renterSignUp(request));
    }

    @POST
    @Path("/owners/login")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse ownerSignIn(Object request) {

        return new APPResponse(service.createOwnerSession(request));
    }


    @POST
    @Path("/owners/signup")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse ownerSignUp(Object request) {

        return new APPResponse(service.ownerSignUp(request));
    }



}
