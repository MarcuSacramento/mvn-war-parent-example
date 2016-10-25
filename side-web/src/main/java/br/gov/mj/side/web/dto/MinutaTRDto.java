package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.List;

import br.gov.mj.side.web.dto.minuta.GrupoMinutaDto;
import br.gov.mj.side.web.dto.minuta.ItemMinutaDto;

public class MinutaTRDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String objeto;
    private String justificativa;
    private String fundamentacaoLegal;
    private String especificacoes;
    private String propostaPreco;
    private String habitacao;
    private String sustentabilidadeAmbiental;
    private String recebimentoAceitacao;
    private String prazo;
    private String metodologia;
    private List<MinutaTrRecursoFinanceiro> listaDeRecursos;
    private String custosEstimados;
    private String garantia;
    private String obrigacoesContratada;
    private String obrigacoesContratante;
    private String acompanhamentoFiscalizacao;
    private String condicoesPagamento;
    private String sansoes;
    private String subcontratacao;
    private String empenho;
    private String validadeAta;
    private List<GrupoMinutaDto> listaDeGrupos;
    private List<ItemMinutaDto> listaDeItens;

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getFundamentacaoLegal() {
        return fundamentacaoLegal;
    }

    public void setFundamentacaoLegal(String fundamentacaoLegal) {
        this.fundamentacaoLegal = fundamentacaoLegal;
    }

    public String getEspecificacoes() {
        return especificacoes;
    }

    public void setEspecificacoes(String especificacoes) {
        this.especificacoes = especificacoes;
    }

    public String getPropostaPreco() {
        return propostaPreco;
    }

    public void setPropostaPreco(String propostaPreco) {
        this.propostaPreco = propostaPreco;
    }

    public String getHabitacao() {
        return habitacao;
    }

    public void setHabitacao(String habitacao) {
        this.habitacao = habitacao;
    }

    public String getSustentabilidadeAmbiental() {
        return sustentabilidadeAmbiental;
    }

    public void setSustentabilidadeAmbiental(String sustentabilidadeAmbiental) {
        this.sustentabilidadeAmbiental = sustentabilidadeAmbiental;
    }

    public String getRecebimentoAceitacao() {
        return recebimentoAceitacao;
    }

    public void setRecebimentoAceitacao(String recebimentoAceitacao) {
        this.recebimentoAceitacao = recebimentoAceitacao;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public String getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }

    public List<MinutaTrRecursoFinanceiro> getListaDeRecursos() {
        return listaDeRecursos;
    }

    public void setListaDeRecursos(List<MinutaTrRecursoFinanceiro> listaDeRecursos) {
        this.listaDeRecursos = listaDeRecursos;
    }

    public String getCustosEstimados() {
        return custosEstimados;
    }

    public void setCustosEstimados(String custosEstimados) {
        this.custosEstimados = custosEstimados;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public String getObrigacoesContratada() {
        return obrigacoesContratada;
    }

    public void setObrigacoesContratada(String obrigacoesContratada) {
        this.obrigacoesContratada = obrigacoesContratada;
    }

    public String getObrigacoesContratante() {
        return obrigacoesContratante;
    }

    public void setObrigacoesContratante(String obrigacoesContratante) {
        this.obrigacoesContratante = obrigacoesContratante;
    }

    public String getAcompanhamentoFiscalizacao() {
        return acompanhamentoFiscalizacao;
    }

    public void setAcompanhamentoFiscalizacao(String acompanhamentoFiscalizacao) {
        this.acompanhamentoFiscalizacao = acompanhamentoFiscalizacao;
    }

    public String getCondicoesPagamento() {
        return condicoesPagamento;
    }

    public void setCondicoesPagamento(String condicoesPagamento) {
        this.condicoesPagamento = condicoesPagamento;
    }

    public String getSansoes() {
        return sansoes;
    }

    public void setSansoes(String sansoes) {
        this.sansoes = sansoes;
    }

    public String getSubcontratacao() {
        return subcontratacao;
    }

    public void setSubcontratacao(String subcontratacao) {
        this.subcontratacao = subcontratacao;
    }

    public String getEmpenho() {
        return empenho;
    }

    public void setEmpenho(String empenho) {
        this.empenho = empenho;
    }

    public String getValidadeAta() {
        return validadeAta;
    }

    public void setValidadeAta(String validadeAta) {
        this.validadeAta = validadeAta;
    }

    public List<GrupoMinutaDto> getListaDeGrupos() {
        return listaDeGrupos;
    }

    public void setListaDeGrupos(List<GrupoMinutaDto> listaDeGrupos) {
        this.listaDeGrupos = listaDeGrupos;
    }

    public List<ItemMinutaDto> getListaDeItens() {
        return listaDeItens;
    }

    public void setListaDeItens(List<ItemMinutaDto> listaDeItens) {
        this.listaDeItens = listaDeItens;
    }
}
