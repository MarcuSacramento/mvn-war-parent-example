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

@Entity
@Table(name = "tb_icf_itens_contrato_formatacao", schema = "side")
public class ItensFormatacao extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "icf_id_itens_contrato_formatacao")
    @SequenceGenerator(name = "tb_icf_itens_contrato_formatacao_generator", sequenceName = "side.seq_tb_icf_itens_contrato_formatacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_icf_itens_contrato_formatacao_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "itc_fk_foc_id_formatacao_contrato")
    @NotNull
    private FormatacaoContrato formatacao;

    @ManyToOne
    @JoinColumn(name = "itc_fk_bem_id_bem")
    @NotNull
    private Bem item;

    public ItensFormatacao() {
    }

    @Override
    public Long getId() {

        return id;
    }

    public FormatacaoContrato getFormatacao() {
        return formatacao;
    }

    public void setFormatacao(FormatacaoContrato formatacao) {
        this.formatacao = formatacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bem getItem() {
        return item;
    }

    public void setItem(Bem item) {
        this.item = item;
    }

}
