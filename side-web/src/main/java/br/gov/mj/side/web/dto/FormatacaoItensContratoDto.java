package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;

public class FormatacaoItensContratoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Contrato contrato;
    private Bem bem;
    private Boolean contratoSelecionado;

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Boolean isContratoSelecionado() {
        return contratoSelecionado;
    }

    public void setContratoSelecionado(Boolean contratoSelecionado) {
        this.contratoSelecionado = contratoSelecionado;
    }

}
