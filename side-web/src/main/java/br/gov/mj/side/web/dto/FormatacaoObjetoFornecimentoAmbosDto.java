package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.enums.EnumAbaFaseAnalise;
import br.gov.mj.side.entidades.enums.EnumResponsavelPreencherFormatacaoItem;
import br.gov.mj.side.entidades.enums.EnumStatusInscricao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoObjetoFornecimento;

public class FormatacaoObjetoFornecimentoAmbosDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private EnumResponsavelPreencherFormatacaoItem responsavelFormacatao;
    private FormatacaoObjetoFornecimento formatacaoFornecedor;
    private FormatacaoObjetoFornecimento formatacaoBeneficiario;
    
    public FormatacaoObjetoFornecimento getFormatacaoFornecedor() {
        return formatacaoFornecedor;
    }
    public void setFormatacaoFornecedor(FormatacaoObjetoFornecimento formatacaoFornecedor) {
        this.formatacaoFornecedor = formatacaoFornecedor;
    }
    public FormatacaoObjetoFornecimento getFormatacaoBeneficiario() {
        return formatacaoBeneficiario;
    }
    public void setFormatacaoBeneficiario(FormatacaoObjetoFornecimento formatacaoBeneficiario) {
        this.formatacaoBeneficiario = formatacaoBeneficiario;
    }
    public EnumResponsavelPreencherFormatacaoItem getResponsavelFormacatao() {
        return responsavelFormacatao;
    }
    public void setResponsavelFormacatao(EnumResponsavelPreencherFormatacaoItem responsavelFormacatao) {
        this.responsavelFormacatao = responsavelFormacatao;
    }
}
