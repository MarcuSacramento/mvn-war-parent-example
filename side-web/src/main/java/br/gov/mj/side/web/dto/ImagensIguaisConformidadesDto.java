package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class ImagensIguaisConformidadesDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long idResposta;
    private String codigoImagemUnica;

    public String getCodigoImagemUnica() {
        return codigoImagemUnica;
    }

    public void setCodigoImagemUnica(String codigoImagemUnica) {
        this.codigoImagemUnica = codigoImagemUnica;
    }

    public Long getIdResposta() {
        return idResposta;
    }

    public void setIdResposta(Long idResposta) {
        this.idResposta = idResposta;
    }
}
