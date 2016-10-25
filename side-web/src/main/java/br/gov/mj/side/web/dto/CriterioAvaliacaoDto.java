package br.gov.mj.side.web.dto;

public class CriterioAvaliacaoDto {

    private String nomeCriterioAvaliacao;
    private String descricaoCriterioAvaliacao;
    private String formaVerificacao;
    private String tipoResposta;
    private String pesoResposta;
    private String possuiObrigatoriedadeDeAnexo;
    private String utilizadoParaCriterioDesempate;
    private String criteriosAvaliacaoOpcaoResposta;

    public String getNomeCriterioAvaliacao() {
        return nomeCriterioAvaliacao;
    }

    public void setNomeCriterioAvaliacao(String nomeCriterioAvaliacao) {
        this.nomeCriterioAvaliacao = nomeCriterioAvaliacao;
    }

    public String getDescricaoCriterioAvaliacao() {
        return descricaoCriterioAvaliacao;
    }

    public void setDescricaoCriterioAvaliacao(String descricaoCriterioAvaliacao) {
        this.descricaoCriterioAvaliacao = descricaoCriterioAvaliacao;
    }

    public String getFormaVerificacao() {
        return formaVerificacao;
    }

    public void setFormaVerificacao(String formaVerificacao) {
        this.formaVerificacao = formaVerificacao;
    }

    public String getTipoResposta() {
        return tipoResposta;
    }

    public void setTipoResposta(String tipoResposta) {
        this.tipoResposta = tipoResposta;
    }

    public String getPesoResposta() {
        return pesoResposta;
    }

    public void setPesoResposta(String pesoResposta) {
        this.pesoResposta = pesoResposta;
    }

    public String getPossuiObrigatoriedadeDeAnexo() {
        return possuiObrigatoriedadeDeAnexo;
    }

    public void setPossuiObrigatoriedadeDeAnexo(String possuiObrigatoriedadeDeAnexo) {
        this.possuiObrigatoriedadeDeAnexo = possuiObrigatoriedadeDeAnexo;
    }

    public String getUtilizadoParaCriterioDesempate() {
        return utilizadoParaCriterioDesempate;
    }

    public void setUtilizadoParaCriterioDesempate(String utilizadoParaCriterioDesempate) {
        this.utilizadoParaCriterioDesempate = utilizadoParaCriterioDesempate;
    }

    public String getCriteriosAvaliacaoOpcaoResposta() {
        return criteriosAvaliacaoOpcaoResposta;
    }

    public void setCriteriosAvaliacaoOpcaoResposta(String criteriosAvaliacaoOpcaoResposta) {
        this.criteriosAvaliacaoOpcaoResposta = criteriosAvaliacaoOpcaoResposta;
    }

}
