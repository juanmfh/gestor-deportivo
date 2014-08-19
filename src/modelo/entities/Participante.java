
package modelo.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JuanM
 */
@Entity
@Table(name = "PARTICIPANTE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Participante.findAll", query = "SELECT p FROM Participante p"),
    @NamedQuery(name = "Participante.findById", query = "SELECT p FROM Participante p WHERE p.id = :id"),
    @NamedQuery(name = "Participante.findByApellidos", query = "SELECT p FROM Participante p WHERE p.apellidos = :apellidos"),
    @NamedQuery(name = "Participante.findByNombre", query = "SELECT p FROM Participante p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Participante.findByEdad", query = "SELECT p FROM Participante p WHERE p.edad = :edad"),
    @NamedQuery(name = "Participante.findBySexo", query = "SELECT p FROM Participante p WHERE p.sexo = :sexo"),
    @NamedQuery(name = "Participante.findByDorsal", query = "SELECT p FROM Participante p WHERE p.dorsal = :dorsal"),
    
    @NamedQuery(name = "Participante.findByGrupo", query = "SELECT p FROM Participante p WHERE p.grupoId.id = :grupoid"),
    @NamedQuery(name = "Participante.findByGrupoPruebaAsignada", query = "SELECT p FROM Participante p WHERE p.grupoId.id = :grupoid AND p.pruebaasignada = :pruebaAsignada"),
    @NamedQuery(name = "Participante.findByEquipo", query = "SELECT p FROM Participante p WHERE p.equipoId.id = :equipoid"),
    @NamedQuery(name = "Participante.findByDorsalAndCompeticion", query = "SELECT p FROM Participante p WHERE p.dorsal = :dorsal AND p IN (SELECT p2 FROM Participante p2 WHERE p2.grupoId IN (SELECT g FROM Grupo g JOIN g.inscripcionCollection i WHERE i.competicionId.id = :competicionid))")
})
public class Participante implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "APELLIDOS")
    private String apellidos;
    @Basic(optional = false)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "EDAD")
    private Integer edad;
    @Column(name = "SEXO")
    private Integer sexo;
    @Basic(optional = false)
    @Column(name = "DORSAL")
    private int dorsal;
    @JoinColumn(name = "PRUEBAASIGNADA", referencedColumnName = "ID")
    @ManyToOne
    private Prueba pruebaasignada;
    @JoinColumn(name = "GRUPO_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Grupo grupoId;
    @JoinColumn(name = "EQUIPO_ID", referencedColumnName = "ID")
    @ManyToOne
    private Equipo equipoId;
    @OneToMany(mappedBy = "participanteId")
    private Collection<Registro> registroCollection;

    public Participante() {
    }

    public Participante(Integer id) {
        this.id = id;
    }

    public Participante(Integer id, String apellidos, String nombre, int dorsal) {
        this.id = id;
        this.apellidos = apellidos;
        this.nombre = nombre;
        this.dorsal = dorsal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Integer getSexo() {
        return sexo;
    }

    public void setSexo(Integer sexo) {
        this.sexo = sexo;
    }

    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    public Prueba getPruebaasignada() {
        return pruebaasignada;
    }

    public void setPruebaasignada(Prueba pruebaasignada) {
        this.pruebaasignada = pruebaasignada;
    }

    public Grupo getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Grupo grupoId) {
        this.grupoId = grupoId;
    }

    public Equipo getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Equipo equipoId) {
        this.equipoId = equipoId;
    }

    @XmlTransient
    public Collection<Registro> getRegistroCollection() {
        return registroCollection;
    }

    public void setRegistroCollection(Collection<Registro> registroCollection) {
        this.registroCollection = registroCollection;
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
        if (!(object instanceof Participante)) {
            return false;
        }
        Participante other = (Participante) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Participante[ id=" + id + " ]";
    }
    
}
