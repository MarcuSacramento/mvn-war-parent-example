package br.gov.mj.side.web.dto;

import java.io.Serializable;

import br.gov.mj.apoio.entidades.Municipio;

public class BeneficiariosDoProgramaDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long idBeneficiario;
    private String cnpjBeneficiario;
    private Municipio municipio;

}
