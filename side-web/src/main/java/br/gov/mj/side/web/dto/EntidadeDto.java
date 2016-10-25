package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.entidade.EntidadeAnexo;
import br.gov.mj.side.entidades.entidade.Pessoa;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;

public class EntidadeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Usuario usuario;
    private EnumStatusEntidade statusEntidade;
    private EnumOrigemCadastro origemCadastro;
    private String numeroCnpj;
    private TipoEntidade tipoEntidade;
    private String nomeEntidade;
    private String nomeContato;
    private EnumPersonalidadeJuridica personalidadeJuridica;
    private Municipio municipio;
    private TipoEndereco tipoEndereco;
    private String descricaoEndereco;
    private String numeroEndereco;
    private String complementoEndereco;
    private String numeroProcessoSEI;
    private String bairro;
    private String numeroCep;
    private String numeroTelefone;
    private String numeroFoneFax;
    private String email;
    private String usuarioCadastro;
    private String usuarioAlteracao;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAlteracao;
    private Pessoa entidadeTitular = new Pessoa();
    private Pessoa entidadeRepresentante = new Pessoa();
    private List<EntidadeAnexo> listaAnexos = new ArrayList<EntidadeAnexo>();
    private Entidade entidade;
    private boolean botaoAlterarSituacoesClicado = false;

    public EnumStatusEntidade getStatusEntidade() {
        return statusEntidade;
    }

    public void setStatusEntidade(EnumStatusEntidade statusEntidade) {
        this.statusEntidade = statusEntidade;
    }

    public String getNumeroCnpj() {
        return numeroCnpj;
    }

    public void setNumeroCnpj(String numeroCnpj) {
        this.numeroCnpj = numeroCnpj;
    }

    public TipoEntidade getTipoEntidade() {
        return tipoEntidade;
    }

    public void setTipoEntidade(TipoEntidade tipoEntidade) {
        this.tipoEntidade = tipoEntidade;
    }

    public String getNomeEntidade() {
        return nomeEntidade;
    }

    public void setNomeEntidade(String nomeEntidade) {
        this.nomeEntidade = nomeEntidade;
    }

    public EnumPersonalidadeJuridica getPersonalidadeJuridica() {
        return personalidadeJuridica;
    }

    public void setPersonalidadeJuridica(EnumPersonalidadeJuridica personalidadeJuridica) {
        this.personalidadeJuridica = personalidadeJuridica;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public String getDescricaoEndereco() {
        return descricaoEndereco;
    }

    public void setDescricaoEndereco(String descricaoEndereco) {
        this.descricaoEndereco = descricaoEndereco;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getComplementoEndereco() {
        return complementoEndereco;
    }

    public void setComplementoEndereco(String complementoEndereco) {
        this.complementoEndereco = complementoEndereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumeroCep() {
        return numeroCep;
    }

    public void setNumeroCep(String numeroCep) {
        this.numeroCep = numeroCep;
    }

    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }

    public String getNumeroFoneFax() {
        return numeroFoneFax;
    }

    public void setNumeroFoneFax(String numeroFoneFax) {
        this.numeroFoneFax = numeroFoneFax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public List<EntidadeAnexo> getListaAnexos() {
        return listaAnexos;
    }

    public void setListaAnexos(List<EntidadeAnexo> listaAnexos) {
        this.listaAnexos = listaAnexos;
    }

    public Pessoa getEntidadeTitular() {
        return entidadeTitular;
    }

    public void setEntidadeTitular(Pessoa entidadeTitular) {
        this.entidadeTitular = entidadeTitular;
    }

    public Pessoa getEntidadeRepresentante() {
        return entidadeRepresentante;
    }

    public void setEntidadeRepresentante(Pessoa entidadeRepresentante) {
        this.entidadeRepresentante = entidadeRepresentante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroProcessoSEI() {
        return numeroProcessoSEI;
    }

    public void setNumeroProcessoSEI(String numeroProcessoSEI) {
        this.numeroProcessoSEI = numeroProcessoSEI;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNomeContato() {
        return nomeContato;
    }

    public void setNomeContato(String nomeContato) {
        this.nomeContato = nomeContato;
    }

    public EnumOrigemCadastro getOrigemCadastro() {
        return origemCadastro;
    }

    public void setOrigemCadastro(EnumOrigemCadastro origemCadastro) {
        this.origemCadastro = origemCadastro;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public boolean isBotaoAlterarSituacoesClicado() {
        return botaoAlterarSituacoesClicado;
    }

    public void setBotaoAlterarSituacoesClicado(boolean botaoAlterarSituacoesClicado) {
        this.botaoAlterarSituacoesClicado = botaoAlterarSituacoesClicado;
    }
}
