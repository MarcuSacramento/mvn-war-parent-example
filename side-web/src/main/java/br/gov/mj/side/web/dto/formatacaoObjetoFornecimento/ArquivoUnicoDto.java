package br.gov.mj.side.web.dto.formatacaoObjetoFornecimento;

import java.io.Serializable;

import br.gov.mj.side.entidades.enums.EnumOrigemArquivo;

public class ArquivoUnicoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String codigoUnico;
    private EnumOrigemArquivo origemArquivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public EnumOrigemArquivo getOrigemArquivo() {
        return origemArquivo;
    }

    public void setOrigemArquivo(EnumOrigemArquivo origemArquivo) {
        this.origemArquivo = origemArquivo;
    }
}
