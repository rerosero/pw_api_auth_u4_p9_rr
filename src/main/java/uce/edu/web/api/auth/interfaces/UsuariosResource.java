package uce.edu.web.api.auth.interfaces;

import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import uce.edu.web.api.auth.application.UsuarioService;
import uce.edu.web.api.auth.application.representation.UsuarioRepresentation;
import uce.edu.web.api.auth.application.representation.UsuarioRequest;

@Path("/usuarios")
public class UsuariosResource {
    @Inject
    private UsuarioService usuarioService;
    //Crear un nuevo usuario
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardar (UsuarioRequest ur){
        this.usuarioService.crearUsuario(ur);
        return Response.status(Response.Status.CREATED).entity(ur).build();
    }
    //Actualizar un usuario existente
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizar(@PathParam("id") Integer id, UsuarioRequest ur){
        this.usuarioService.actualizarUsuario(id, ur);
        return Response.status(Response.Status.OK).entity(ur).build();
    }
    //Actualizar parcialmente un usuario existente
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminar(@PathParam("id") Integer id){
        this.usuarioService.eliminarUsuario(id);
        return Response.status(Response.Status.OK).build();
    }
    //Buscar un usuario por su id
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Integer id){
        UsuarioRepresentation ur = this.usuarioService.buscarUsuarioPorId(id);
        return Response.status(Response.Status.OK).entity(ur).build();
    }
    //Listar todos los usuarios
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarUsuarios(){
        return Response.status(Response.Status.OK).entity(this.usuarioService.listarUsuarios()).build();
    }
}
