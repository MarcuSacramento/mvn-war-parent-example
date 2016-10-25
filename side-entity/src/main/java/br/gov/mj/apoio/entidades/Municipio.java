package br.gov.mj.apoio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_mun_municipio", schema = "apoio")
public class Municipio extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "mun_id_municipio")
    @NotNull
    private Long id;

    @Column(name = "mun_no_municipio")
    @NotNull
    private String nomeMunicipio;

    @ManyToOne
    @JoinColumn(name = "mun_fk_euf_id_estado")
    @NotNull
    private Uf uf;

    public Municipio() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public void setNomeMunicipio(String nomeMunicipio) {
        this.nomeMunicipio = nomeMunicipio;
    }

    public Uf getUf() {
        return uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
    }

    @Transient
    public String getNomeMunicipioUf() {
        return (this.nomeMunicipio + " - " + getUf().getSiglaUf()).toUpperCase();
    }
}