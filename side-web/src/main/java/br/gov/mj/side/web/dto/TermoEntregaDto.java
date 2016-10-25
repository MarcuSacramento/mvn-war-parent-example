package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.LocalEntregaEntidade;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.programa.inscricao.ComissaoRecebimento;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.ItensNotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.web.util.CPFUtils;
import br.gov.mj.side.web.util.DataUtil;
import br.gov.mj.side.web.util.MascaraUtils;
import bsh.util.Util;

public class TermoEntregaDto implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1823301928117096986L;

    private String nomeFornecedor;
    private String cnpjFornecedor;
    private String numeroContrato;
    private String periodoVigenciaContrato;
    private String nomePreposto;
    private String telefonePreposto;
    private String emailPreposto;
    private String numeroOF;
    private String numeroNF;

    private String nomeBeneficiario;
    private String nomeRepresentante;
    private String telefoneRepresentante;
    private String emailRepresentante;
    private String enderecoBeneficiario;

    private List<TermoEntregaitensDto> listaItens = new ArrayList<TermoEntregaitensDto>();
    private List<TermoEntregaitensDto> listaMembros = new ArrayList<TermoEntregaitensDto>();

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    public String getCnpjFornecedor() {
        return cnpjFornecedor;
    }

    public void setCnpjFornecedor(String cnpjFornecedor) {
        this.cnpjFornecedor = cnpjFornecedor;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getPeriodoVigenciaContrato() {
        return periodoVigenciaContrato;
    }

    public void setPeriodoVigenciaContrato(String periodoVigenciaContrato) {
        this.periodoVigenciaContrato = periodoVigenciaContrato;
    }

    public String getNomePreposto() {
        return nomePreposto;
    }

    public void setNomePreposto(String nomePreposto) {
        this.nomePreposto = nomePreposto;
    }

    public String getTelefonePreposto() {
        return telefonePreposto;
    }

    public void setTelefonePreposto(String telefonePreposto) {
        this.telefonePreposto = telefonePreposto;
    }

    public String getEmailPreposto() {
        return emailPreposto;
    }

    public void setEmailPreposto(String emailPreposto) {
        this.emailPreposto = emailPreposto;
    }

    public String getNumeroOF() {
        return numeroOF;
    }

    public void setNumeroOF(String numeroOF) {
        this.numeroOF = numeroOF;
    }

    public String getNumeroNF() {
        return numeroNF;
    }

    public void setNumeroNF(String numeroNF) {
        this.numeroNF = numeroNF;
    }

    public String getNomeBeneficiario() {
        return nomeBeneficiario;
    }

    public void setNomeBeneficiario(String nomeBeneficiario) {
        this.nomeBeneficiario = nomeBeneficiario;
    }

    public String getNomeRepresentante() {
        return nomeRepresentante;
    }

    public void setNomeRepresentante(String nomeRepresentante) {
        this.nomeRepresentante = nomeRepresentante;
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

    public String getEnderecoBeneficiario() {
        return enderecoBeneficiario;
    }

    public void setEnderecoBeneficiario(String enderecoBeneficiario) {
        this.enderecoBeneficiario = enderecoBeneficiario;
    }

    public List<TermoEntregaitensDto> getListaItens() {
        return listaItens;
    }

    public void setListaItens(List<TermoEntregaitensDto> listaItens) {
        this.listaItens = listaItens;
    }

    public List<TermoEntregaitensDto> getListaMembros() {
        return listaMembros;
    }

    public void setListaMembros(List<TermoEntregaitensDto> listaMembros) {
        this.listaMembros = listaMembros;
    }
}
