package br.gov.mj.side.web.dto;

import java.io.Serializable;

public class TermoRDDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nomeFornecedor;

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }
}
