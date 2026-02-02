package uce.edu.web.api.auth.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Table(name = "usuarios")
@Entity
@SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_seq", allocationSize = 1)
public class Usuario extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    public Integer id;
    public String usuario;
    @Column(name = "password_hash")
    public String password;
    public String rol;
}
