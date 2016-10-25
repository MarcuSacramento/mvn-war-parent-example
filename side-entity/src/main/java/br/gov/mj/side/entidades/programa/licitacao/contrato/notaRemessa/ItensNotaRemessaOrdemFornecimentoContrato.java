package br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa;

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
import br.gov.mj.side.entidades.programa.licitacao.contrato.ItensOrdemFornecimentoContrato;

@Entity
@Table(name = "tb_inr_item_nota_remessa_of_contrato", schema = "side")
public class ItensNotaRemessaOrdemFornecimentoContrato extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "inr_id_item_nota_remessa_of_contrato")
    @SequenceGenerator(name = "seq_tb_inr_item_nota_remessa_of_contrato_generator", sequenceName = "side.seq_tb_inr_item_nota_remessa_of_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_inr_item_nota_remessa_of_contrato_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inr_fk_nrc_id_nota_remessa_ordem_fornecimento_contrato")
    @NotNull
    private NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimento;

    @ManyToOne
    @JoinColumn(name = "inr_fk_iof_id_itens_ordem_fornecimento_contrato")
    @NotNull
    private ItensOrdemFornecimentoContrato itemOrdemFornecimentoContrato;

    @ManyToOne
    @JoinColumn(name = "inr_fk_trd_id_termo_recebimento_definitivo")
    private TermoRecebimentoDefinitivo termoRecebimentoDefinitivo;

    @Override
    public Long getId() {
        return id;
    }

    public NotaRemessaOrdemFornecimentoContrato getNotaRemessaOrdemFornecimento() {
        return notaRemessaOrdemFornecimento;
    }

    public void setNotaRemessaOrdemFornecimento(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimento) {
        this.notaRemessaOrdemFornecimento = notaRemessaOrdemFornecimento;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItensOrdemFornecimentoContrato getItemOrdemFornecimentoContrato() {
        return itemOrdemFornecimentoContrato;
    }

    public void setItemOrdemFornecimentoContrato(ItensOrdemFornecimentoContrato itemOrdemFornecimentoContrato) {
        this.itemOrdemFornecimentoContrato = itemOrdemFornecimentoContrato;
    }

    public TermoRecebimentoDefinitivo getTermoRecebimentoDefinitivo() {
        return termoRecebimentoDefinitivo;
    }

    public void setTermoRecebimentoDefinitivo(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        this.termoRecebimentoDefinitivo = termoRecebimentoDefinitivo;
    }

}
