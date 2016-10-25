package br.gov.mj.side.entidades.programa;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.AcaoOrcamentaria;

@Entity
@Table(name = "tb_prf_programa_recurso_financeiro", schema = "side")
public class ProgramaRecursoFinanceiro extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "prf_id_programa_recurso_financeiro")
    @SequenceGenerator(name = "tb_prf_programa_recurso_financeiro_generator", sequenceName = "side.seq_tb_prf_programa_recurso_financeiro", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_prf_programa_recurso_financeiro_generator")
    @NotNull
    private Long id;

    @Column(name = "prf_vl_valor_utilizar")
    @NotNull
    private BigDecimal valorUtilizar;

    @ManyToOne
    @JoinColumn(name = "prf_fk_aco_id_acao_orcamentaria")
    @NotNull
    private AcaoOrcamentaria acaoOrcamentaria;

    @ManyToOne
    @JoinColumn(name = "prf_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "recursoFinanceiro", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<RecursoFinanceiroEmenda> recursoFinanceiroEmendas = new ArrayList<RecursoFinanceiroEmenda>();

    public ProgramaRecursoFinanceiro() {
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

    public AcaoOrcamentaria getAcaoOrcamentaria() {
        return acaoOrcamentaria;
    }

    public void setAcaoOrcamentaria(AcaoOrcamentaria acaoOrcamentaria) {
        this.acaoOrcamentaria = acaoOrcamentaria;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public List<RecursoFinanceiroEmenda> getRecursoFinanceiroEmendas() {
        return recursoFinanceiroEmendas;
    }

    public void setRecursoFinanceiroEmendas(List<RecursoFinanceiroEmenda> recursoFinanceiroEmendas) {
        this.recursoFinanceiroEmendas = recursoFinanceiroEmendas;
    }

    @Transient
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(getTotalEmendas());
        total = total.add(getValorUtilizar()==null?BigDecimal.ZERO:getValorUtilizar());
        return total;
    }
    
    @Transient
    public BigDecimal getTotalEmendas(){
        BigDecimal total = BigDecimal.ZERO;
        if(!recursoFinanceiroEmendas.isEmpty()){
            for (RecursoFinanceiroEmenda recursoFinanceiroEmenda : recursoFinanceiroEmendas) {
                total = total.add(recursoFinanceiroEmenda.getValorUtilizar()==null?BigDecimal.ZERO:recursoFinanceiroEmenda.getValorUtilizar());
            }
        }
        return total;
    }

}