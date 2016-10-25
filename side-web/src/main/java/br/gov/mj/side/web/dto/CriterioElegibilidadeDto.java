package br.gov.mj.side.web.dto;

public class CriterioElegibilidadeDto {

    private String criterioElegibilidade;
    private String descricaoElegibilidade;
    private String verificacaoElebilidade;
    private String anexoElegibilidade;

    public String getCriterioElegibilidade() {
        return criterioElegibilidade;
    }

    public void setCriterioElegibilidade(String criterioElegibilidade) {
        this.criterioElegibilidade = criterioElegibilidade;
    }

    public String getDescricaoElegibilidade() {
        return descricaoElegibilidade;
    }

    public void setDescricaoElegibilidade(String descricaoElegibilidade) {
        this.descricaoElegibilidade = descricaoElegibilidade;
    }

    public String getVerificacaoElebilidade() {
        return verificacaoElebilidade;
    }

    public void setVerificacaoElebilidade(String verificacaoElebilidade) {
        this.verificacaoElebilidade = verificacaoElebilidade;
    }

    public String getAnexoElegibilidade() {
        return anexoElegibilidade;
    }

    public void setAnexoElegibilidade(String anexoElegibilidade) {
        this.anexoElegibilidade = anexoElegibilidade;
    }
}
