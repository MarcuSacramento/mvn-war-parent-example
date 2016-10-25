package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;

public class NotaRemessaPesquisaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotaRemessaOrdemFornecimentoContrato notaRemessa;
    private ItensNotaRemessaOrdemFornecimentoContrato itemNotaRemessa;

    public NotaRemessaOrdemFornecimentoContrato getNotaRemessa() {
        return notaRemessa;
    }

    public void setNotaRemessa(NotaRemessaOrdemFornecimentoContrato notaRemessa) {
        this.notaRemessa = notaRemessa;
    }

    public ItensNotaRemessaOrdemFornecimentoContrato getItemNotaRemessa() {
        return itemNotaRemessa;
    }

    public void setItemNotaRemessa(ItensNotaRemessaOrdemFornecimentoContrato itemNotaRemessa) {
        this.itemNotaRemessa = itemNotaRemessa;
    }
}
