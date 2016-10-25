package br.gov.mj.side.web.dto;

public class PotencialBeneficiarioDto {

    private String uf;
    private String municipios;

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getMunicipios() {
        return municipios;
    }

    public void setMunicipios(String municipios) {
        this.municipios = municipios;
    }
}
