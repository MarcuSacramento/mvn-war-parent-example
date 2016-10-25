package br.gov.mj.side.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_cve_codigo_verificacao", schema = "side")
public class CodigoVerificacao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cve_id_codigo_verificacao")
    @SequenceGenerator(name = "tb_cve_codigo_verificacao_GENERATOR", sequenceName = "side.seq_tb_cve_codigo_verificacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_cve_codigo_verificacao_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "cve_ds_descricao_codigo_verificacao")
    @NotNull
    private String descricaoCodigoVerificacao;

    public CodigoVerificacao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getDescricaoCodigoVerificacao() {
        return descricaoCodigoVerificacao;
    }

    public void setDescricaoCodigoVerificacao(String descricaoCodigoVerificacao) {
        this.descricaoCodigoVerificacao = descricaoCodigoVerificacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

}