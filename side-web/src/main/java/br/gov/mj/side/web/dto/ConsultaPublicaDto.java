package br.gov.mj.side.web.dto;

import java.util.List;

public class ConsultaPublicaDto {

    private String nomePrograma;
    private String nomeFantasia;
    private String codigoPrograma;
    private String descricaoPrograma;
    private String funcao;
    private String subFuncao;
    private String orgao;
    private String ano;
    private String numeroSei;
    private String unidadeExecutora;
    private List<AcaoOrcamentariaDto> listaAcoes;
    private String valorTotal;
    private List<PotencialBeneficiarioDto> listaBeneficiarios;
    private String regimeJuridico;
    private List<BemDto> listaBem;
    private List<KitDto> listaKit;
    private List<CriterioElegibilidadeDto> listaElegibilidade;
    private List<CriterioAvaliacaoDto> listaAvaliacao;
    private List<CriterioAcompanhamentoDto> listaAcompanhamento;
    private List<HistoricoDto> historico;
    private String periodoPropostas;
    private String status;
    private String valorMaximoProposta;

    public String getNomePrograma() {
        return nomePrograma;
    }

    public void setNomePrograma(String nome) {
        this.nomePrograma = nome;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCodigoPrograma() {
        return codigoPrograma;
    }

    public void setCodigoPrograma(String codigoPrograma) {
        this.codigoPrograma = codigoPrograma;
    }

    public String getDescricaoPrograma() {
        return descricaoPrograma;
    }

    public void setDescricaoPrograma(String descricaoPrograma) {
        this.descricaoPrograma = descricaoPrograma;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(String subFuncao) {
        this.subFuncao = subFuncao;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidadeExecutora() {
        return unidadeExecutora;
    }

    public void setUnidadeExecutora(String unidadeExecutora) {
        this.unidadeExecutora = unidadeExecutora;
    }

    public List<BemDto> getListaBem() {
        return listaBem;
    }

    public void setListaBem(List<BemDto> listaBem) {
        this.listaBem = listaBem;
    }

    public List<KitDto> getListaKit() {
        return listaKit;
    }

    public void setListaKit(List<KitDto> listaKit) {
        this.listaKit = listaKit;
    }

    public List<CriterioElegibilidadeDto> getListaElegibilidade() {
        return listaElegibilidade;
    }

    public void setListaElegibilidade(List<CriterioElegibilidadeDto> listaElegibilidade) {
        this.listaElegibilidade = listaElegibilidade;
    }

    public List<CriterioAvaliacaoDto> getListaAvaliacao() {
        return listaAvaliacao;
    }

    public void setListaAvaliacao(List<CriterioAvaliacaoDto> listaAvaliacao) {
        this.listaAvaliacao = listaAvaliacao;
    }

    public List<CriterioAcompanhamentoDto> getListaAcompanhamento() {
        return listaAcompanhamento;
    }

    public void setListaAcompanhamento(List<CriterioAcompanhamentoDto> listaAcompanhamento) {
        this.listaAcompanhamento = listaAcompanhamento;
    }

    public List<PotencialBeneficiarioDto> getListaBeneficiarios() {
        return listaBeneficiarios;
    }

    public void setListaBeneficiarios(List<PotencialBeneficiarioDto> listaBeneficiarios) {
        this.listaBeneficiarios = listaBeneficiarios;
    }

    public String getRegimeJuridico() {
        return regimeJuridico;
    }

    public void setRegimeJuridico(String regimeJuridico) {
        this.regimeJuridico = regimeJuridico;
    }

    public List<AcaoOrcamentariaDto> getListaAcoes() {
        return listaAcoes;
    }

    public void setListaAcoes(List<AcaoOrcamentariaDto> listaAcoes) {
        this.listaAcoes = listaAcoes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getNumeroSei() {
        return numeroSei;
    }

    public void setNumeroSei(String numeroSei) {
        this.numeroSei = numeroSei;
    }

    public List<HistoricoDto> getHistorico() {
        return historico;
    }

    public void setHistorico(List<HistoricoDto> historico) {
        this.historico = historico;
    }

    public String getPeriodoPropostas() {
        return periodoPropostas;
    }

    public void setPeriodoPropostas(String periodoPropostas) {
        this.periodoPropostas = periodoPropostas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValorMaximoProposta() {
        return valorMaximoProposta;
    }

    public void setValorMaximoProposta(String valorMaximoProposta) {
        this.valorMaximoProposta = valorMaximoProposta;
    }
}