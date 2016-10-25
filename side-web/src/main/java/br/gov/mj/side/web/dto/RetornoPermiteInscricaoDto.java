package br.gov.mj.side.web.dto;

public class RetornoPermiteInscricaoDto {

    private boolean permiteInscricao;
    private String retornoMensagem;

    public RetornoPermiteInscricaoDto(boolean permiteInscricao, String retornoMensagem) {
        super();
        this.permiteInscricao = permiteInscricao;
        this.retornoMensagem = retornoMensagem;
    }

    public boolean isPermiteInscricao() {
        return permiteInscricao;
    }

    public String getRetornoMensagem() {
        return retornoMensagem;
    }

}