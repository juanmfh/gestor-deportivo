
package modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JuanM
 */
@Entity
@Table(name = "COMPETICION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Competicion.findAll", query = "SELECT c FROM Competicion c"),
    @NamedQuery(name = "Competicion.findById", query = "SELECT c FROM Competicion c WHERE c.id = :id"),
    @NamedQuery(name = "Competicion.findByNombre", query = "SELECT c FROM Competicion c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Competicion.findByCiudad", query = "SELECT c FROM Competicion c WHERE c.ciudad = :ciudad"),
    @NamedQuery(name = "Competicion.findByPais", query = "SELECT c FROM Competicion c WHERE c.pais = :pais"),
    @NamedQuery(name = "Competicion.findByFechainicio", query = "SELECT c FROM Competicion c WHERE c.fechainicio = :fechainicio"),
    @NamedQuery(name = "Competicion.findByFechafin", query = "SELECT c FROM Competicion c WHERE c.fechafin = :fechafin"),
    @NamedQuery(name = "Competicion.findByOrganizador", query = "SELECT c FROM Competicion c WHERE c.organizador = :organizador"),
    @NamedQuery(name = "Competicion.findByImagen", query = "SELECT c FROM Competicion c WHERE c.imagen = :imagen"),
    
    @NamedQuery(name = "Competicion.findAllNames", query = "SELECT c.nombre FROM Competicion c")})
public class Competicion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "CIUDAD")
    private String ciudad;
    @Column(name = "PAIS")
    private String pais;
    @Column(name = "FECHAINICIO")
    @Temporal(TemporalType.DATE)
    private Date fechainicio;
    @Column(name = "FECHAFIN")
    @Temporal(TemporalType.DATE)
    private Date fechafin;
    @Column(name = "ORGANIZADOR")
    private String organizador;
    @Column(name = "IMAGEN")
    private String imagen;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competicionId")
    private Collection<Compuesta> compuestaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competicionId")
    private Collection<Inscripcion> inscripcionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competicionId")
    private Collection<Administrado> administradoCollection;

    public Competicion() {
    }

    public Competicion(Integer id) {
        this.id = id;
    }

    public Competicion(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @XmlTransient
    public Collection<Compuesta> getCompuestaCollection() {
        return compuestaCollection;
    }

    public void setCompuestaCollection(Collection<Compuesta> compuestaCollection) {
        this.compuestaCollection = compuestaCollection;
    }

    @XmlTransient
    public Collection<Inscripcion> getInscripcionCollection() {
        return inscripcionCollection;
    }

    public void setInscripcionCollection(Collection<Inscripcion> inscripcionCollection) {
        this.inscripcionCollection = inscripcionCollection;
    }

    @XmlTransient
    public Collection<Administrado> getAdministradoCollection() {
        return administradoCollection;
    }

    public void setAdministradoCollection(Collection<Administrado> administradoCollection) {
        this.administradoCollection = administradoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Competicion)) {
            return false;
        }
        Competicion other = (Competicion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Competicion[ id=" + id + " ]";
    }
    
}
