package org.globolist.resource;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.globolist.model.User;
import io.smallrye.jwt.build.JwtClaimsBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {


    @POST
    @Path("/login")
    @PermitAll
    public Uni<Response> login(User creds) {
        return User.authenticateUser(creds)
                .map(user -> {
                    String token = generateToken(user);
                    return Response.ok().header("Authorization", "Bearer " + token).build();
                })
                .onFailure()
                .recoverWithItem(failure -> Response.status(Response.Status.UNAUTHORIZED).build());
    }
    @GET
    @RolesAllowed({"ADMIN"})
    public Uni<List<User>> listUsers() {
        return User.listAll();
    }

    @GET
    @Path("/{username}")
    public Uni<User> getUser(@PathParam("username") String username) {
        return User.findByUsername(username)
                .onItem().ifNull().failWith(new NotFoundException());
    }

    private String generateToken(User user) {
        JwtClaimsBuilder claimsBuilder = Jwt.claims();
        claimsBuilder.subject(user.username);
        claimsBuilder.groups(user.role);
        // Add more claims as needed

        return claimsBuilder
                .sign();
    }
}
