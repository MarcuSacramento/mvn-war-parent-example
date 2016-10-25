package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.programa.licitacao.contrato.FormatacaoItensContratoResposta;

public class PanelCompararDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private FormatacaoItensContratoResposta respostaFornecedor;
    private FormatacaoItensContratoResposta respostaBeneficiario;

    public FormatacaoItensContratoResposta getRespostaFornecedor() {
        return respostaFornecedor;
    }

    public void setRespostaFornecedor(FormatacaoItensContratoResposta respostaFornecedor) {
        this.respostaFornecedor = respostaFornecedor;
    }

    public FormatacaoItensContratoResposta getRespostaBeneficiario() {
        return respostaBeneficiario;
    }

    public void setRespostaBeneficiario(FormatacaoItensContratoResposta respostaBeneficiario) {
        this.respostaBeneficiario = respostaBeneficiario;
    }

}
