package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.List;

public class MinutaOrdemFornecedorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nomeFornecedor;
    private String numeroContrato;
    private String periodoVigenciaContrato;
    private String nomeProgramaContratante;
    private String nomePrepostoContrato;
    private String telefonePreposto;
    private String emailPreposto;
    private List<EntregaPrevistaDto> listaEntregaPrevistaDto;
    private String valorEstimadoBem;

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
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

    public String getNomePrepostoContrato() {
        return nomePrepostoContrato;
    }

    public void setNomePrepostoContrato(String nomePrepostoContrato) {
        this.nomePrepostoContrato = nomePrepostoContrato;
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

    public List<EntregaPrevistaDto> getListaEntregaPrevistaDto() {
        return listaEntregaPrevistaDto;
    }

    public void setListaEntregaPrevistaDto(List<EntregaPrevistaDto> listaEntregaPrevistaDto) {
        this.listaEntregaPrevistaDto = listaEntregaPrevistaDto;
    }

    public String getValorEstimadoBem() {
        return valorEstimadoBem;
    }

    public void setValorEstimadoBem(String valorEstimadoBem) {
        this.valorEstimadoBem = valorEstimadoBem;
    }

    public String getNomeProgramaContratante() {
        return nomeProgramaContratante;
    }

    public void setNomeProgramaContratante(String nomeProgramaContratante) {
        this.nomeProgramaContratante = nomeProgramaContratante;
    }
}
