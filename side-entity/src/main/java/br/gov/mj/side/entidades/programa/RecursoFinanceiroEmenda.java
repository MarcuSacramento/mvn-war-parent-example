package br.gov.mj.side.entidades.programa;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.EmendaParlamentar;

@Entity
@Table(name = "tb_rfe_recurso_financeiro_emenda_parlamentar", schema = "side")
public class RecursoFinanceiroEmenda extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rfe_id_recurso_financeiro_emenda_parlamentar")
    @SequenceGenerator(name = "tb_rfe_recurso_financeiro_emenda_parlamentar_generator", sequenceName = "side.seq_tb_rfe_recurso_financeiro_emenda_parlamentar", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rfe_recurso_financeiro_emenda_parlamentar_generator")
    @NotNull
    private Long id;

    @Column(name = "rfe_vl_valor_utilizar")
    @NotNull
    private BigDecimal valorUtilizar;

    @ManyToOne
    @JoinColumn(name = "rfe_fk_prf_id_programa_recurso_financeiro")
    @NotNull
    private ProgramaRecursoFinanceiro recursoFinanceiro;

    @ManyToOne
    @JoinColumn(name = "rfe_fk_epa_id_emenda_parlamentar")
    @NotNull
    private EmendaParlamentar emendaParlamentar;

    public RecursoFinanceiroEmenda() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorUtilizar() {
        return valorUtilizar;
    }

    public void setValorUtilizar(BigDecimal valorUtilizar) {
        this.valorUtilizar = valorUtilizar;
    }

    public ProgramaRecursoFinanceiro getRecursoFinanceiro() {
        return recursoFinanceiro;
    }

    public void setRecursoFinanceiro(ProgramaRecursoFinanceiro recursoFinanceiro) {
        this.recursoFinanceiro = recursoFinanceiro;
    }

    public EmendaParlamentar getEmendaParlamentar() {
        return emendaParlamentar;
    }

    public void setEmendaParlamentar(EmendaParlamentar emendaParlamentar) {
        this.emendaParlamentar = emendaParlamentar;
    }

}