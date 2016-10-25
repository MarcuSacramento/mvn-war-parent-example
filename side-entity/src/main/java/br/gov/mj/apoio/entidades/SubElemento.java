package br.gov.mj.apoio.entidades;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.Bem;

@Entity
@Table(name = "tb_sue_subelemento", schema = "apoio")
public class SubElemento extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "sue_id_subelemento")
    @NotNull
    private Long id;

    @Column(name = "sue_no_subelemento")
    @NotNull
    private String nomeSubElemento;

    @ManyToOne
    @JoinColumn(name = "sue_fk_elm_id_elemento")
    @NotNull
    private Elemento elemento;

    @OneToMany(mappedBy = "subElemento")
    private List<Bem> bens = new ArrayList<Bem>();

    public SubElemento() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeSubElemento() {
        return nomeSubElemento;
    }

    public void setNomeSubElemento(String nomeSubElemento) {
        this.nomeSubElemento = nomeSubElemento;
    }

    public List<Bem> getBens() {
        return bens;
    }

    public void setBens(List<Bem> bens) {
        this.bens = bens;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    public String getNomeECodigo() {
        return (this.id + " - " + this.nomeSubElemento).toUpperCase();
    }

}