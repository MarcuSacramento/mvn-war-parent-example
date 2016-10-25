package br.gov.mj.side.web.dto;

public class CriterioAcompanhamentoDto {

    private String criterioAcompanhamento;
    private String descricaoAcompanhamento;
    private String formaAcompanhamento;

    public String getCriterioAcompanhamento() {
        return criterioAcompanhamento;
    }

    public void setCriterioAcompanhamento(String criterioAcompanhamento) {
        this.criterioAcompanhamento = criterioAcompanhamento;
    }

    public String getDescricaoAcompanhamento() {
        return descricaoAcompanhamento;
    }

    public void setDescricaoAcompanhamento(String descricaoAcompanhamento) {
        this.descricaoAcompanhamento = descricaoAcompanhamento;
    }

    public String getFormaAcompanhamento() {
        return formaAcompanhamento;
    }

    public void setFormaAcompanhamento(String formaAcompanhamento) {
        this.formaAcompanhamento = formaAcompanhamento;
    }
}
