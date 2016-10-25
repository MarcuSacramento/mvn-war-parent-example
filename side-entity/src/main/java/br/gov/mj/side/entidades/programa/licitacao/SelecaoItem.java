package br.gov.mj.side.entidades.programa.licitacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Table(name = "tb_sit_selecao_item", schema = "side")
public class SelecaoItem extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "sit_id_selecao_item")
    @SequenceGenerator(name = "tb_sit_selecao_item_generator", sequenceName = "side.seq_tb_sit_selecao_item", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_sit_selecao_item_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sit_fk_agl_id_agrupamento_licitacao")
    @NotNull
    private AgrupamentoLicitacao agrupamentoLicitacao;

    @Column(name = "sit_no_unidade_medida")
    @NotNull
    private String unidadeMedida;

    @Column(name = "sit_qt_quantidade_imediata")
    private Long quantidadeImediata;

    @Column(name = "sit_vl_valor_unitario")
    @NotNull
    private BigDecimal valorUnitario;

    @Column(name = "sit_vl_valor_total_registrar")
    @NotNull
    private BigDecimal valorTotalARegistrar;
    
    @Column(name = "sit_vl_valor_total_imediato")
    @NotNull
    private BigDecimal valorTotalImediato;

    @OneToMany(mappedBy = "selecaoItem", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<BemUf> listaBemUf = new ArrayList<BemUf>();

    public SelecaoItem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AgrupamentoLicitacao getAgrupamentoLicitacao() {
        return agrupamentoLicitacao;
    }

    public void setAgrupamentoLicitacao(AgrupamentoLicitacao agrupamentoLicitacao) {
        this.agrupamentoLicitacao = agrupamentoLicitacao;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

  
    public List<BemUf> getListaBemUf() {
        return listaBemUf;
    }

    public void setListaBemUf(List<BemUf> listaBemUf) {
        this.listaBemUf = listaBemUf;
    }

    public BigDecimal getValorTotalARegistrar() {
        return valorTotalARegistrar;
    }

    public void setValorTotalARegistrar(BigDecimal valorTotalARegistrar) {
        this.valorTotalARegistrar = valorTotalARegistrar;
    }

    public BigDecimal getValorTotalImediato() {
        return valorTotalImediato;
    }

    public void setValorTotalImediato(BigDecimal valorTotalImediato) {
        this.valorTotalImediato = valorTotalImediato;
    }

    public Long getQuantidadeImediata() {
        return quantidadeImediata;
    }

    public void setQuantidadeImediata(Long quantidadeImediata) {
        this.quantidadeImediata = quantidadeImediata;
    }

}