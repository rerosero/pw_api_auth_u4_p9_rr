package uce.edu.web.api.auth.application;

import java.util.List;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uce.edu.web.api.auth.application.representation.UsuarioRepresentation;
import uce.edu.web.api.auth.application.representation.UsuarioRequest;
import uce.edu.web.api.auth.domain.Usuario;
import uce.edu.web.api.auth.infraestructure.UsuarioRepository;

@ApplicationScoped
@Transactional
public class UsuarioService {
    @Inject
    private UsuarioRepository usuarioRepository;
    
    //Autenticar usuario comparando el password con el hash almacenado
    public Usuario auntentica(String usuario, String password) {
        Usuario user = usuarioRepository.findByUsername(usuario);
        if (user == null) {
            return null;
        } else if (!BcryptUtil.matches(password, user.password)) {
            return null;
        }
        return user;
    }
    //Convierte datos que vienen del cliente en una entidad de base de datos
    private Usuario fromUsuarioRequest(UsuarioRequest ur) {
        Usuario u = new Usuario();
        u.usuario = ur.usuario;
        u.rol = ur.rol;
        u.password = BcryptUtil.bcryptHash(ur.password);
        return u;
    }
    //Convierte una entidad de base de datos en una representacion para el cliente
    private UsuarioRepresentation toUsuarioRepresentation(Usuario u) {
        UsuarioRepresentation ur = new UsuarioRepresentation();
        ur.id = u.id;
        ur.usuario = u.usuario;
        ur.rol = u.rol;
        return ur;
    }
    //crear un nuevo usuario
    @Transactional
    public void crearUsuario(UsuarioRequest ur) {
        Usuario u = this.fromUsuarioRequest(ur);
        usuarioRepository.persist(u);
    }
    //Listar todos los usuarios
    public List<UsuarioRepresentation> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.listAll();
        return usuarios.stream()
                .map(this::toUsuarioRepresentation)
                .toList();
    }
    //Buscar un usuario por su id
    @Transactional
    public UsuarioRepresentation buscarUsuarioPorId(Integer id) {
        Usuario u = usuarioRepository.findById(id.longValue());
        if (u == null) {
            return null;
        }
        return this.toUsuarioRepresentation(u);
    }
    //Eliminar un usuario por su id
    @Transactional
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id.longValue());
    }
    //Actualizar un usuario existente
    @Transactional
    public void actualizarUsuario(Integer id, UsuarioRequest ur) {
        Usuario u = usuarioRepository.findById(id.longValue());
        if (u != null) {
            u.usuario = ur.usuario;
            u.rol = ur.rol;
            //Actualizar el password solo si se proporciona uno nuevo
            if (ur.password != null && !ur.password.isEmpty()) {
                u.password = BcryptUtil.bcryptHash(ur.password);
            }
        }
    }
    //actualizar parcial
    @Transactional
    public void actualizarParcial(Integer id, UsuarioRequest ur) {
        Usuario u = usuarioRepository.findById(id.longValue());
        if (ur.usuario != null) {
            u.usuario = ur.usuario;
        }
        if (ur.rol != null) {
            u.rol = ur.rol;
        }
        if (ur.password != null && !ur.password.isEmpty()) {
            u.password = BcryptUtil.bcryptHash(ur.password);
        }
    }
}
