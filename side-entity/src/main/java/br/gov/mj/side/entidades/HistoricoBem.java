package br.gov.mj.side.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

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

import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_hbm_historico_bem", schema = "side")
public class HistoricoBem extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "hbm_id_historico_bem")
    @SequenceGenerator(name = "TB_HBM_HISTORICO_BEM_ID_GENERATOR", sequenceName = "SIDE.SEQ_TB_HBM_HISTORICO_BEM", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_HBM_HISTORICO_BEM_ID_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "hbm_vl_valor_bem")
    @NotNull
    private BigDecimal valorEstimado;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "hbm_dt_data_estimativa")
    private LocalDate dataEstimativa;

    @ManyToOne
    @JoinColumn(name = "hbm_fk_bem_id_bem")
    private Bem bem;

    public HistoricoBem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public LocalDate getDataEstimativa() {
        return dataEstimativa;
    }

    public void setDataEstimativa(LocalDate dataEstimativa) {
        this.dataEstimativa = dataEstimativa;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
