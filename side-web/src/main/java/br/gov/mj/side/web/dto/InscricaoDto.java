package br.gov.mj.side.web.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaBem;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.inscricao.InscricaoProgramaKit;

public class InscricaoDto {

    private String nomePrograma;
    private String codigoPublicacaoPrograma;
    private String periodoRecebimentoPropostasPrograma;
    private BigDecimal valorTotalPrograma;
    private String orgaoExecutor;
    private String cnpjEntidade;
    private String nomeEntidade;
    private String descricaoEndereco;
    private String telefoneEntidade;
    private String emailEntidade;
    private String cpfRepresentante;
    private String nomeRepresentante;
    private String cargoRepresentante;
    private String telefoneRepresentante;
    private String emailRepresentante;

    private BigDecimal valorTotalProposta;
    private BigDecimal valorMaximoPorProposta;

    private List<InscricaoProgramaBem> listaBens = new ArrayList<InscricaoProgramaBem>();
    private List<InscricaoProgramaKit> listaKits = new ArrayList<InscricaoProgramaKit>();
    private List<InscricaoProgramaCriterioElegibilidade> listaCriteriosElegibilidade = new ArrayList<InscricaoProgramaCriterioElegibilidade>();
    private List<InscricaoProgramaCriterioAvaliacao> listaCriteriosAvaliacao = new ArrayList<InscricaoProgramaCriterioAvaliacao>();

    public String getNomePrograma() {
        return nomePrograma;
    }

    public void setNomePrograma(String nomePrograma) {
        this.nomePrograma = nomePrograma;
    }

    public String getCodigoPublicacaoPrograma() {
        return codigoPublicacaoPrograma;
    }

    public void setCodigoPublicacaoPrograma(String codigoPublicacaoPrograma) {
        this.codigoPublicacaoPrograma = codigoPublicacaoPrograma;
    }

    public String getPeriodoRecebimentoPropostasPrograma() {
        return periodoRecebimentoPropostasPrograma;
    }

    public void setPeriodoRecebimentoPropostasPrograma(String periodoRecebimentoPropostasPrograma) {
        this.periodoRecebimentoPropostasPrograma = periodoRecebimentoPropostasPrograma;
    }

    public BigDecimal getValorTotalPrograma() {
        return valorTotalPrograma;
    }

    public void setValorTotalPrograma(BigDecimal bigDecimal) {
        this.valorTotalPrograma = bigDecimal;
    }

    public String getOrgaoExecutor() {
        return orgaoExecutor;
    }

    public void setOrgaoExecutor(String orgaoExecutor) {
        this.orgaoExecutor = orgaoExecutor;
    }

    public String getCnpjEntidade() {
        return cnpjEntidade;
    }

    public void setCnpjEntidade(String cnpjEntidade) {
        this.cnpjEntidade = cnpjEntidade;
    }

    public String getNomeEntidade() {
        return nomeEntidade;
    }

    public void setNomeEntidade(String nomeEntidade) {
        this.nomeEntidade = nomeEntidade;
    }

    public String getDescricaoEndereco() {
        return descricaoEndereco;
    }

    public void setDescricaoEndereco(String descricaoEndereco) {
        this.descricaoEndereco = descricaoEndereco;
    }

    public String getTelefoneEntidade() {
        return telefoneEntidade;
    }

    public void setTelefoneEntidade(String telefoneEntidade) {
        this.telefoneEntidade = telefoneEntidade;
    }

    public String getEmailEntidade() {
        return emailEntidade;
    }

    public void setEmailEntidade(String emailEntidade) {
        this.emailEntidade = emailEntidade;
    }

    public String getCpfRepresentante() {
        return cpfRepresentante;
    }

    public void setCpfRepresentante(String cpfRepresentante) {
        this.cpfRepresentante = cpfRepresentante;
    }

    public String getNomeRepresentante() {
        return nomeRepresentante;
    }

    public void setNomeRepresentante(String nomeRepresentante) {
        this.nomeRepresentante = nomeRepresentante;
    }

    public String getCargoRepresentante() {
        return cargoRepresentante;
    }

    public void setCargoRepresentante(String cargoRepresentante) {
        this.cargoRepresentante = cargoRepresentante;
    }

    public String getTelefoneRepresentante() {
        return telefoneRepresentante;
    }

    public void setTelefoneRepresentante(String telefoneRepresentante) {
        this.telefoneRepresentante = telefoneRepresentante;
    }

    public String getEmailRepresentante() {
        return emailRepresentante;
    }

    public void setEmailRepresentante(String emailRepresentante) {
        this.emailRepresentante = emailRepresentante;
    }

    public List<InscricaoProgramaBem> getListaBens() {
        return listaBens;
    }

    public void setListaBens(List<InscricaoProgramaBem> listaBens) {
        this.listaBens = listaBens;
    }

    public List<InscricaoProgramaKit> getListaKits() {
        return listaKits;
    }

    public void setListaKits(List<InscricaoProgramaKit> listaKits) {
        this.listaKits = listaKits;
    }

    public List<InscricaoProgramaCriterioElegibilidade> getListaCriteriosElegibilidade() {
        return listaCriteriosElegibilidade;
    }

    public void setListaCriteriosElegibilidade(List<InscricaoProgramaCriterioElegibilidade> listaCriteriosElegibilidade) {
        this.listaCriteriosElegibilidade = listaCriteriosElegibilidade;
    }

    public List<InscricaoProgramaCriterioAvaliacao> getListaCriteriosAvaliacao() {
        return listaCriteriosAvaliacao;
    }

    public void setListaCriteriosAvaliacao(List<InscricaoProgramaCriterioAvaliacao> listaCriteriosAvaliacao) {
        this.listaCriteriosAvaliacao = listaCriteriosAvaliacao;
    }

    public BigDecimal getValorTotalProposta() {
        return valorTotalProposta;
    }

    public void setValorTotalProposta(BigDecimal valorTotalProposta) {
        this.valorTotalProposta = valorTotalProposta;
    }

    public BigDecimal getValorMaximoPorProposta() {
        return valorMaximoPorProposta;
    }

    public void setValorMaximoPorProposta(BigDecimal valorMaximoPorProposta) {
        this.valorMaximoPorProposta = valorMaximoPorProposta;
    }
}
