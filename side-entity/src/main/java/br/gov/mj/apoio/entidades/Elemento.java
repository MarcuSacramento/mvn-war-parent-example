package br.gov.mj.apoio.entidades;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_elm_elemento", schema = "apoio")
public class Elemento extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "elm_id_elemento")
    @NotNull
    private Long id;

    @Column(name = "elm_no_elemento")
    @NotNull
    private String nomeElemento;

    @OneToMany(mappedBy = "elemento")
    private List<SubElemento> subElementos = new ArrayList<SubElemento>();

    public Elemento() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeElemento() {
        return nomeElemento;
    }

    public void setNomeElemento(String nomeElemento) {
        this.nomeElemento = nomeElemento;
    }

    public List<SubElemento> getSubElementos() {
        return subElementos;
    }

    public void setSubElementos(List<SubElemento> subElementos) {
        this.subElementos = subElementos;
    }

    @Transient
    public String getNomeECodigo() {
        return this.id + " - " + this.nomeElemento.toUpperCase();
    }

}