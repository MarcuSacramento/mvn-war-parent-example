package br.gov.mj.side.entidades.programa.licitacao.contrato;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumStatusFormatacao;

@Entity
@Table(name = "tb_fof_formatacao_objeto_fornecimento", schema = "side")
public class FormatacaoObjetoFornecimento extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "fof_id_formatacao_objeto_fornecimento")
    @SequenceGenerator(name = "seq_tb_fof_formatacao_objeto_fornecimento_generator", sequenceName = "side.seq_tb_fof_formatacao_objeto_fornecimento", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_fof_formatacao_objeto_fornecimento_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fof_fk_ofo_id_objeto_fornecimento_contrato")
    @NotNull
    private ObjetoFornecimentoContrato objetoFornecimento;

    @ManyToOne
    @JoinColumn(name = "fof_fk_fic_id_formatacao_itens_contrato")
    @NotNull
    private FormatacaoItensContrato formatacao;

    @ManyToOne
    @JoinColumn(name = "fof_fk_fir_id_formatacao_itens_contrato_resposta")
    private FormatacaoItensContratoResposta formatacaoResposta;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumPerfilEntidade", name = "enumClassName"))
    @Column(name = "fof_tp_responsavel_formatacao")
    @NotNull
    private EnumPerfilEntidade responsavelFormatacao;

    @Override
    public Long getId() {
        return id;
    }

    public ObjetoFornecimentoContrato getObjetoFornecimento() {
        return objetoFornecimento;
    }

    public void setObjetoFornecimento(ObjetoFornecimentoContrato objetoFornecimento) {
        this.objetoFornecimento = objetoFornecimento;
    }

    public FormatacaoItensContrato getFormatacao() {
        return formatacao;
    }

    public void setFormatacao(FormatacaoItensContrato formatacao) {
        this.formatacao = formatacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FormatacaoItensContratoResposta getFormatacaoResposta() {
        return formatacaoResposta;
    }

    public void setFormatacaoResposta(FormatacaoItensContratoResposta formatacaoResposta) {
        this.formatacaoResposta = formatacaoResposta;
    }

    public EnumPerfilEntidade getResponsavelFormatacao() {
        return responsavelFormatacao;
    }

    public void setResponsavelFormatacao(EnumPerfilEntidade responsavelFormatacao) {
        this.responsavelFormatacao = responsavelFormatacao;
    }
}
