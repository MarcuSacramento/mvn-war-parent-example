package br.gov.mj.side.web.dto;

public class HistoricoDto {

    private String dataCadastro;
    private String dataPublicacaoDOU;
    private String dataInicialProposta;
    private String dataFinalProposta;
    private String dataInicialAnalise;
    private String dataFinalAnalise;
    private String tipoPrograma;
    private String statusPrograma;
    private String motivo;
    private String usuarioCadastro;

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDataPublicacaoDOU() {
        return dataPublicacaoDOU;
    }

    public void setDataPublicacaoDOU(String dataPublicacaoDOU) {
        this.dataPublicacaoDOU = dataPublicacaoDOU;
    }

    public String getDataInicialProposta() {
        return dataInicialProposta;
    }

    public void setDataInicialProposta(String dataInicialProposta) {
        this.dataInicialProposta = dataInicialProposta;
    }

    public String getDataFinalProposta() {
        return dataFinalProposta;
    }

    public void setDataFinalProposta(String dataFinalProposta) {
        this.dataFinalProposta = dataFinalProposta;
    }

    public String getTipoPrograma() {
        return tipoPrograma;
    }

    public void setTipoPrograma(String tipoPrograma) {
        this.tipoPrograma = tipoPrograma;
    }

    public String getStatusPrograma() {
        return statusPrograma;
    }

    public void setStatusPrograma(String statusPrograma) {
        this.statusPrograma = statusPrograma;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public String getDataInicialAnalise() {
        return dataInicialAnalise;
    }

    public void setDataInicialAnalise(String dataInicialAnalise) {
        this.dataInicialAnalise = dataInicialAnalise;
    }

    public String getDataFinalAnalise() {
        return dataFinalAnalise;
    }

    public void setDataFinalAnalise(String dataFinalAnalise) {
        this.dataFinalAnalise = dataFinalAnalise;
    }
}
