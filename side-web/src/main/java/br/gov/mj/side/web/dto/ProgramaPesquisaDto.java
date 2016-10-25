package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.PartidoPolitico;
import br.gov.mj.apoio.entidades.Uf;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.BeneficiarioEmendaParlamentar;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.entidades.enums.EnumTipoPrograma;
import br.gov.mj.side.entidades.programa.Programa;

public class ProgramaPesquisaDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private Programa programa;
    private List<Uf> listaUf = new ArrayList<Uf>();
    private List<Municipio> listaMunicipio = new ArrayList<Municipio>();
    private EmendaParlamentar emendaParlamentar;
    private AcaoOrcamentaria acaoOrcamentaria;
    private String codigoIdentificadorProgramaPublicado;
    private Elemento elemento;
    private Kit kit;
    private Bem bem;
    private EnumTipoPrograma tipoPrograma;

    // Utilizado para a pesquisa publica
    private PartidoPolitico partidoParlamentar;
    private Uf ufParlamentar;
    private String nomeParlamentar;
    private String cargoParlamentar;
    private BeneficiarioEmendaParlamentar cnpjBeneficiario;
    private Boolean pesquisaPublica = false;

    public List<Uf> getListaUf() {
        return listaUf;
    }

    public void setListaUf(List<Uf> listaUf) {
        this.listaUf = listaUf;
    }

    public EmendaParlamentar getEmendaParlamentar() {
        return emendaParlamentar;
    }

    public void setEmendaParlamentar(EmendaParlamentar emendaParlamentar) {
        this.emendaParlamentar = emendaParlamentar;
    }

    public AcaoOrcamentaria getAcaoOrcamentaria() {
        return acaoOrcamentaria;
    }

    public void setAcaoOrcamentaria(AcaoOrcamentaria acaoOrcamentaria) {
        this.acaoOrcamentaria = acaoOrcamentaria;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public List<Municipio> getListaMunicipio() {
        return listaMunicipio;
    }

    public void setListaMunicipio(List<Municipio> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public EnumTipoPrograma getTipoPrograma() {
        return tipoPrograma;
    }

    public void setTipoPrograma(EnumTipoPrograma tipoPrograma) {
        this.tipoPrograma = tipoPrograma;
    }

    public BeneficiarioEmendaParlamentar getCnpjBeneficiario() {
        return cnpjBeneficiario;
    }

    public void setCnpjBeneficiario(BeneficiarioEmendaParlamentar cnpjBeneficiario) {
        this.cnpjBeneficiario = cnpjBeneficiario;
    }

    public Boolean getPesquisaPublica() {
        return pesquisaPublica;
    }

    public void setPesquisaPublica(Boolean pesquisaPublica) {
        this.pesquisaPublica = pesquisaPublica;
    }

    public Uf getUfParlamentar() {
        return ufParlamentar;
    }

    public void setUfParlamentar(Uf ufParlamentar) {
        this.ufParlamentar = ufParlamentar;
    }

    public String getNomeParlamentar() {
        return nomeParlamentar;
    }

    public void setNomeParlamentar(String nomeParlamentar) {
        this.nomeParlamentar = nomeParlamentar;
    }

    public String getCargoParlamentar() {
        return cargoParlamentar;
    }

    public void setCargoParlamentar(String cargoParlamentar) {
        this.cargoParlamentar = cargoParlamentar;
    }

    public PartidoPolitico getPartidoParlamentar() {
        return partidoParlamentar;
    }

    public void setPartidoParlamentar(PartidoPolitico partidoParlamentar) {
        this.partidoParlamentar = partidoParlamentar;
    }

    public String getCodigoIdentificadorProgramaPublicado() {
        return codigoIdentificadorProgramaPublicado;
    }

    public void setCodigoIdentificadorProgramaPublicado(String codigoIdentificadorProgramaPublicado) {
        this.codigoIdentificadorProgramaPublicado = codigoIdentificadorProgramaPublicado;
    }
}
