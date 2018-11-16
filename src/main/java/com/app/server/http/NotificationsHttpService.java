package com.app.server.http;

import com.app.server.http.exceptions.APPInternalServerException;
import com.app.server.http.exceptions.APPNotFoundException;
import com.app.server.http.utils.APPResponse;
import com.app.server.http.utils.PATCH;
import com.app.server.models.Booking;
import com.app.server.models.Renter;
import com.app.server.models.RenterPaymentMethod;
import com.app.server.services.BookingService;
import com.app.server.services.NotificationService;
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

@Path("notifications")

public class NotificationsHttpService {

        private ObjectWriter ow;
        private NotificationService serviceN;


        public NotificationsHttpService() {
            serviceN = NotificationService.getInstance();
            ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        }

    @OPTIONS
    @PermitAll
    public Response optionsById() {

        return Response.ok().build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {
        return new APPResponse(serviceN.createNotification(request));
    }
}
