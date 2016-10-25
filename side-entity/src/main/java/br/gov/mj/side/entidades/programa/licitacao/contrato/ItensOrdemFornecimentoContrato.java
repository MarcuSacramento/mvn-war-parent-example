package br.gov.mj.side.entidades.programa.licitacao.contrato;

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
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;

@Entity
@Table(name = "tb_iof_itens_ordem_fornecimento_contrato", schema = "side")
public class ItensOrdemFornecimentoContrato extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "iof_id_itens_ordem_fornecimento_contrato")
    @SequenceGenerator(name = "seq_tb_iof_itens_ordem_fornecimento_contrato_generator", sequenceName = "side.seq_tb_iof_itens_ordem_fornecimento_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_iof_itens_ordem_fornecimento_contrato_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "iof_fk_ofc_id_ordem_fornecimento_contrato")
    @NotNull
    private OrdemFornecimentoContrato ordemFornecimento;

    @ManyToOne
    @JoinColumn(name = "iof_fk_lee_id_local_entrega_entidade")
    @NotNull
    private LocalEntregaEntidade localEntrega;

    @ManyToOne
    @JoinColumn(name = "iof_fk_bem_id_bem")
    @NotNull
    private Bem item;

    @Column(name = "iof_qt_quantidade")
    @NotNull
    private Integer quantidade;

    @Override
    public Long getId() {
        return id;
    }

    public OrdemFornecimentoContrato getOrdemFornecimento() {
        return ordemFornecimento;
    }

    public void setOrdemFornecimento(OrdemFornecimentoContrato ordemFornecimento) {
        this.ordemFornecimento = ordemFornecimento;
    }

    public LocalEntregaEntidade getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(LocalEntregaEntidade localEntrega) {
        this.localEntrega = localEntrega;
    }

    public Bem getItem() {
        return item;
    }

    public void setItem(Bem item) {
        this.item = item;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}
