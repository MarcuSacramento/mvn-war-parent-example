package br.gov.mj.apoio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_par_parametro", schema = "apoio")
public class Parametro extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "par_id_parametro")
    @NotNull
    private Long id;

    @Column(name = "par_sg_sigla_parametro")
    @NotNull
    private String chaveSigla;

    @Column(name = "par_vl_valor_parametro")
    @NotNull
    private String valor;

    public Parametro() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChaveSigla() {
        return chaveSigla;
    }

    public void setChaveSigla(String chaveSigla) {
        this.chaveSigla = chaveSigla;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}