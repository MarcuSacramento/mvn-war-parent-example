package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class PermissaoProgramaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean excluir = Boolean.FALSE;
    private Boolean alterar = Boolean.FALSE;
    private Boolean concluir = Boolean.FALSE;
    private Boolean publicar = Boolean.FALSE;
    private Boolean cancelar = Boolean.FALSE;
    private Boolean prorrogar = Boolean.FALSE;
    private Boolean escolherProrrogarSuspenderPrograma = false;
    private Boolean prorrogarAnalise = Boolean.FALSE;
    private Boolean reabrirPrazo = Boolean.FALSE;
    private Boolean suspenderPrazo = Boolean.FALSE;
    private Boolean suspenderPrograma = Boolean.FALSE;
    private Boolean inscrever = Boolean.FALSE;
    private Boolean visualizarListaPropostas = Boolean.FALSE;
    private Boolean publicarListaElegibilidade = Boolean.FALSE;
    private Boolean publicarListaAvaliacao = Boolean.FALSE;
    private Boolean vincularLocaisDeEntrega = Boolean.FALSE;
    private Boolean visualizarPanelAposAnalise = Boolean.FALSE;
    private Boolean vincularComissaoRecebimento = Boolean.FALSE;

    public Boolean getExcluir() {
        return excluir;
    }

    public Boolean getVincularComissaoRecebimento() {
        return vincularComissaoRecebimento;
    }

    public void setVincularComissaoRecebimento(Boolean vincularComissaoRecebimento) {
        this.vincularComissaoRecebimento = vincularComissaoRecebimento;
    }

    public void setExcluir(Boolean excluir) {
        this.excluir = excluir;
    }

    public Boolean getAlterar() {
        return alterar;
    }

    public void setAlterar(Boolean alterar) {
        this.alterar = alterar;
    }

    public Boolean getConcluir() {
        return concluir;
    }

    public void setConcluir(Boolean concluir) {
        this.concluir = concluir;
    }

    public Boolean getPublicar() {
        return publicar;
    }

    public void setPublicar(Boolean publicar) {
        this.publicar = publicar;
    }

    public Boolean getCancelar() {
        return cancelar;
    }

    public void setCancelar(Boolean cancelar) {
        this.cancelar = cancelar;
    }

    public Boolean getProrrogar() {
        return prorrogar;
    }

    public void setProrrogar(Boolean prorrogar) {
        this.prorrogar = prorrogar;
    }

    public Boolean getSuspenderPrazo() {
        return suspenderPrazo;
    }

    public void setSuspenderPrazo(Boolean suspenderPrazo) {
        this.suspenderPrazo = suspenderPrazo;
    }

    public Boolean getSuspenderPrograma() {
        return suspenderPrograma;
    }

    public void setSuspenderPrograma(Boolean suspenderPrograma) {
        this.suspenderPrograma = suspenderPrograma;
    }

    public Boolean getInscrever() {
        return inscrever;
    }

    public void setInscrever(Boolean inscrever) {
        this.inscrever = inscrever;
    }

    public Boolean getReabrirPrazo() {
        return reabrirPrazo;
    }

    public void setReabrirPrazo(Boolean reabrirPrazo) {
        this.reabrirPrazo = reabrirPrazo;
    }

    public Boolean getProrrogarAnalise() {
        return prorrogarAnalise;
    }

    public void setProrrogarAnalise(Boolean prorrogarAnalise) {
        this.prorrogarAnalise = prorrogarAnalise;
    }

    public Boolean getPublicarListaElegibilidade() {
        return publicarListaElegibilidade;
    }

    public void setPublicarListaElegibilidade(Boolean publicarListaElegibilidade) {
        this.publicarListaElegibilidade = publicarListaElegibilidade;
    }

    public Boolean getVisualizarListaPropostas() {
        return visualizarListaPropostas;
    }

    public void setVisualizarListaPropostas(Boolean visualizarListaPropostas) {
        this.visualizarListaPropostas = visualizarListaPropostas;
    }

    public Boolean getPublicarListaAvaliacao() {
        return publicarListaAvaliacao;
    }

    public void setPublicarListaAvaliacao(Boolean publicarListaAvaliacao) {
        this.publicarListaAvaliacao = publicarListaAvaliacao;
    }

    public Boolean getVincularLocaisDeEntrega() {
        return vincularLocaisDeEntrega;
    }

    public void setVincularLocaisDeEntrega(Boolean vincularLocaisDeEntrega) {
        this.vincularLocaisDeEntrega = vincularLocaisDeEntrega;
    }

    public Boolean getEscolherProrrogarSuspenderPrograma() {
        return escolherProrrogarSuspenderPrograma;
    }

    public void setEscolherProrrogarSuspenderPrograma(Boolean escolherProrrogarSuspenderPrograma) {
        this.escolherProrrogarSuspenderPrograma = escolherProrrogarSuspenderPrograma;
    }

    public Boolean getVisualizarPanelAposAnalise() {
        return visualizarPanelAposAnalise;
    }

    public void setVisualizarPanelAposAnalise(Boolean visualizarPanelAposAnalise) {
        this.visualizarPanelAposAnalise = visualizarPanelAposAnalise;
    }

}
