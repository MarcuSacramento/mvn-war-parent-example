package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.enums.EnumTipoObjeto;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;

public class ObjetoFornecimentoContratoDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private ObjetoFornecimentoContrato objetoFornecimentoContrato;
    private EnumTipoObjeto tipoObjeto;

    public ObjetoFornecimentoContrato getObjetoFornecimentoContrato() {
        return objetoFornecimentoContrato;
    }

    public void setObjetoFornecimentoContrato(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        this.objetoFornecimentoContrato = objetoFornecimentoContrato;
    }

    public EnumTipoObjeto getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(EnumTipoObjeto tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

}
