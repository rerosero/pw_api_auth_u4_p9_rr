package uce.edu.web.api.auth.interfaces;

import java.time.Instant;
import java.util.Set;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import uce.edu.web.api.auth.application.UsuarioService;
import uce.edu.web.api.auth.domain.Usuario;

@Path("/auth")
public class AuthResource {
    @Inject
    private UsuarioService usuarioService;

    @ConfigProperty(name = "auth.issuer", defaultValue = "matricula-auth")
    String issuer;

    @ConfigProperty(name = "auth.token.ttl", defaultValue = "3600")
    long ttl;

    @GET
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResponse token(
            @QueryParam("user") String user,
            @QueryParam("password") String password) {

        // Validar con la base de datos usando el servicio
        Usuario u = usuarioService.auntentica(user, password);
        if (u == null) {
            // Excepcion de autenticacion
            throw new WebApplicationException(Response.status(401).build());
        }
        // si el usuario no tiene asignado un rol, se le asigna el rol "user" por defecto
        String role = u.rol != null ? u.rol : "user";
        // Solo los usuarios con rol "admin" pueden generar tokens, si el rol del usuario no es "admin", se lanza una excepcion de autorizacion
        if (!"admin".equalsIgnoreCase(u.rol)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("No autorizado: solo admin puede generar token")
                            .build());
        }
        
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttl);

        String jwt = Jwt.issuer(issuer)
                .subject(user)
                .groups(Set.of(role)) // roles: user / admin
                .issuedAt(now)
                .expiresAt(exp)
                .sign();

        return new TokenResponse(jwt, exp.getEpochSecond(), role);
    }

    public static class TokenResponse {
        public String accessToken;
        public long expiresAt;
        public String role;

        public TokenResponse() {
        }

        public TokenResponse(String accessToken, long expiresAt, String role) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
            this.role = role;
        }
    }

}
