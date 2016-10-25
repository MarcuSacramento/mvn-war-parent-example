package br.gov.mj.apoio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_mae_motivo_analise", schema = "apoio")
public class MotivoAnalise extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "mae_id_motivo_analise")
    @NotNull
    private Long id;

    @Column(name = "mae_no_motivo_analise")
    @NotNull
    private String nomeMotivo;

    public MotivoAnalise() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public String getNomeECodigo() {
        return this.id + " - " + this.nomeMotivo.toUpperCase();
    }

}