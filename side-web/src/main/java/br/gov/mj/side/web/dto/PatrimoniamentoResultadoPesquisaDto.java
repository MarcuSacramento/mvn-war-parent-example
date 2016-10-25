package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.enums.EnumSituacaoPesquisaPatrimoniamento;
import br.gov.mj.side.entidades.enums.EnumTipoPatrimonio;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;

public class PatrimoniamentoResultadoPesquisaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Programa programa;
    private Bem bem;
    private ObjetoFornecimentoContrato objetoFornecimentoContrato;
    private EnumSituacaoPesquisaPatrimoniamento situacaoBem;
    private EnumTipoPatrimonio tipoPatrimonio;
    
    private List<PatrimoniamentoTipo> detalhePatrimonio = new ArrayList<PatrimoniamentoTipo>();

    
    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public EnumSituacaoPesquisaPatrimoniamento getSituacaoBem() {
        return situacaoBem;
    }

    public void setSituacaoBem(EnumSituacaoPesquisaPatrimoniamento situacaoBem) {
        this.situacaoBem = situacaoBem;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public ObjetoFornecimentoContrato getObjetoFornecimentoContrato() {
        return objetoFornecimentoContrato;
    }

    public void setObjetoFornecimentoContrato(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        this.objetoFornecimentoContrato = objetoFornecimentoContrato;
    }

    public List<PatrimoniamentoTipo> getDetalhePatrimonio() {
        return detalhePatrimonio;
    }

    public void setDetalhePatrimonio(List<PatrimoniamentoTipo> detalhePatrimonio) {
        this.detalhePatrimonio = detalhePatrimonio;
    }

    public EnumTipoPatrimonio getTipoPatrimonio() {
        return tipoPatrimonio;
    }

    public void setTipoPatrimonio(EnumTipoPatrimonio tipoPatrimonio) {
        this.tipoPatrimonio = tipoPatrimonio;
    }
}
