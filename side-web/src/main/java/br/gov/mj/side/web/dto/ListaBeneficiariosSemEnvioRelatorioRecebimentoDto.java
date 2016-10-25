package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;

public class ListaBeneficiariosSemEnvioRelatorioRecebimentoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    List<NotaRemessaOrdemFornecimentoContrato> lista5Dias = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    List<NotaRemessaOrdemFornecimentoContrato> lista10Dias = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    List<NotaRemessaOrdemFornecimentoContrato> lista15Dias = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    List<NotaRemessaOrdemFornecimentoContrato> lista20Dias = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    List<NotaRemessaOrdemFornecimentoContrato> lista25Dias = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();
    List<NotaRemessaOrdemFornecimentoContrato> lista30Dias = new ArrayList<NotaRemessaOrdemFornecimentoContrato>();

    public List<NotaRemessaOrdemFornecimentoContrato> getLista5Dias() {
        return lista5Dias;
    }

    public void setLista5Dias(List<NotaRemessaOrdemFornecimentoContrato> lista5Dias) {
        this.lista5Dias = lista5Dias;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> getLista10Dias() {
        return lista10Dias;
    }

    public void setLista10Dias(List<NotaRemessaOrdemFornecimentoContrato> lista10Dias) {
        this.lista10Dias = lista10Dias;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> getLista15Dias() {
        return lista15Dias;
    }

    public void setLista15Dias(List<NotaRemessaOrdemFornecimentoContrato> lista15Dias) {
        this.lista15Dias = lista15Dias;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> getLista20Dias() {
        return lista20Dias;
    }

    public void setLista20Dias(List<NotaRemessaOrdemFornecimentoContrato> lista20Dias) {
        this.lista20Dias = lista20Dias;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> getLista25Dias() {
        return lista25Dias;
    }

    public void setLista25Dias(List<NotaRemessaOrdemFornecimentoContrato> lista25Dias) {
        this.lista25Dias = lista25Dias;
    }

    public List<NotaRemessaOrdemFornecimentoContrato> getLista30Dias() {
        return lista30Dias;
    }

    public void setLista30Dias(List<NotaRemessaOrdemFornecimentoContrato> lista30Dias) {
        this.lista30Dias = lista30Dias;
    }
}
