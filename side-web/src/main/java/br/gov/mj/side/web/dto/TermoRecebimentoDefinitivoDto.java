package br.gov.mj.side.web.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.entidade.Entidade;
import br.gov.mj.side.entidades.enums.EnumSituacaoGeracaoTermos;
import br.gov.mj.side.entidades.enums.EnumStatusGeracaoTermoDoacao;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.NotaRemessaOrdemFornecimentoContrato;
import br.gov.mj.side.entidades.programa.licitacao.contrato.notaRemessa.TermoRecebimentoDefinitivo;

public class TermoRecebimentoDefinitivoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private TermoRecebimentoDefinitivo termoRecebimentoDefinitivo;
    private NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato;

    private Long idTermoDoacao;
    private Long idTermoRecebimentoDefinitivo;
    private String usuarioLogado;
    private List<ObjetoFornecimentoContrato> listaDeObjetosFornecimentoContrato = new ArrayList<ObjetoFornecimentoContrato>();

    private Long idBeneficiario = 0L;
    private String nomeBeneciario = "";

    private Entidade entidade;
    private String numeroTermoDoacao;
    private String numeroCnpj;
    private Municipio estado;
    private Bem item;
    private LocalDateTime dataGeracao;
    private Programa programa;
    private EnumStatusGeracaoTermoDoacao statusGeracaoTermoDoacao;
    private List<ItensNotaRemessaDto> itensDoTermo = new ArrayList<ItensNotaRemessaDto>();
    private boolean buscarSomenteOsOfosComTermosJaGerados = false;
    private String numeroProcessoSei;
    
    //Variável que fica no ObjetoFornecimento contrato e armazena a situação da geração dos termos
    private EnumSituacaoGeracaoTermos situacaoGeracaoTermos;

    private Boolean trdSelecionado;

    /* Inicio Variaveis do relatorio de termo de recebimento */
    private String nomePrograma;
    private String nomeUnidadeExecutora;
    private String nomeBeneficiario;
    private String nomeRepresentante;
    private String telefoneRepresentante;
    private String emailRepresentante;
    private String enderecoBeneficiario;
    private List<TermoDefinitivoItensDto> listaItens = new ArrayList<TermoDefinitivoItensDto>();
    private List<TermoDefinitivoItensDto> listaMembros = new ArrayList<TermoDefinitivoItensDto>();
    /* Fim Variaveis do relatorio de termo de recebimento */

    public TermoRecebimentoDefinitivo getTermoRecebimentoDefinitivo() {
        return termoRecebimentoDefinitivo;
    }

    public void setTermoRecebimentoDefinitivo(TermoRecebimentoDefinitivo termoRecebimentoDefinitivo) {
        this.termoRecebimentoDefinitivo = termoRecebimentoDefinitivo;
    }

    public List<ItensNotaRemessaDto> getItensDoTermo() {
        return itensDoTermo;
    }

    public void setItensDoTermo(List<ItensNotaRemessaDto> itensDoTermo) {
        this.itensDoTermo = itensDoTermo;
    }

    public Long getIdTermoDoacao() {
        return idTermoDoacao;
    }

    public void setIdTermoDoacao(Long idTermoDoacao) {
        this.idTermoDoacao = idTermoDoacao;
    }

    public String getNumeroTermoDoacao() {
        return numeroTermoDoacao;
    }

    public void setNumeroTermoDoacao(String numeroTermoDoacao) {
        this.numeroTermoDoacao = numeroTermoDoacao;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public String getNomeBeneciario() {
        return nomeBeneciario;
    }

    public void setNomeBeneciario(String nomeBeneciario) {
        this.nomeBeneciario = nomeBeneciario;
    }

    public NotaRemessaOrdemFornecimentoContrato getNotaRemessaOrdemFornecimentoContrato() {
        return notaRemessaOrdemFornecimentoContrato;
    }

    public void setNotaRemessaOrdemFornecimentoContrato(NotaRemessaOrdemFornecimentoContrato notaRemessaOrdemFornecimentoContrato) {
        this.notaRemessaOrdemFornecimentoContrato = notaRemessaOrdemFornecimentoContrato;
    }

    public Long getIdBeneficiario() {
        return idBeneficiario;
    }

    public void setIdBeneficiario(Long idBeneficiario) {
        this.idBeneficiario = idBeneficiario;
    }

    public Boolean getTrdSelecionado() {
        return trdSelecionado;
    }

    public void setTrdSelecionado(Boolean trdSelecionado) {
        this.trdSelecionado = trdSelecionado;
    }

    public String getNumeroCnpj() {
        return numeroCnpj;
    }

    public void setNumeroCnpj(String numeroCnpj) {
        this.numeroCnpj = numeroCnpj;
    }

    public Municipio getEstado() {
        return estado;
    }

    public void setEstado(Municipio estado) {
        this.estado = estado;
    }

    public Bem getItem() {
        return item;
    }

    public void setItem(Bem item) {
        this.item = item;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public String getNomePrograma() {
        return nomePrograma;
    }

    public void setNomePrograma(String nomePrograma) {
        this.nomePrograma = nomePrograma;
    }

    public String getNomeUnidadeExecutora() {
        return nomeUnidadeExecutora;
    }

    public void setNomeUnidadeExecutora(String nomeUnidadeExecutora) {
        this.nomeUnidadeExecutora = nomeUnidadeExecutora;
    }

    public String getNomeBeneficiario() {
        return nomeBeneficiario;
    }

    public void setNomeBeneficiario(String nomeBeneficiario) {
        this.nomeBeneficiario = nomeBeneficiario;
    }

    public String getNomeRepresentante() {
        return nomeRepresentante;
    }

    public void setNomeRepresentante(String nomeRepresentante) {
        this.nomeRepresentante = nomeRepresentante;
    }

    public String getTelefoneRepresentante() {
        return telefoneRepresentante;
    }

    public void setTelefoneRepresentante(String telefoneRepresentante) {
        this.telefoneRepresentante = telefoneRepresentante;
    }

    public String getEmailRepresentante() {
        return emailRepresentante;
    }

    public void setEmailRepresentante(String emailRepresentante) {
        this.emailRepresentante = emailRepresentante;
    }

    public String getEnderecoBeneficiario() {
        return enderecoBeneficiario;
    }

    public void setEnderecoBeneficiario(String enderecoBeneficiario) {
        this.enderecoBeneficiario = enderecoBeneficiario;
    }

    public List<TermoDefinitivoItensDto> getListaItens() {
        return listaItens;
    }

    public void setListaItens(List<TermoDefinitivoItensDto> listaItens) {
        this.listaItens = listaItens;
    }

    public List<TermoDefinitivoItensDto> getListaMembros() {
        return listaMembros;
    }

    public void setListaMembros(List<TermoDefinitivoItensDto> listaMembros) {
        this.listaMembros = listaMembros;
    }

    public EnumStatusGeracaoTermoDoacao getStatusGeracaoTermoDoacao() {
        return statusGeracaoTermoDoacao;
    }

    public void setStatusGeracaoTermoDoacao(EnumStatusGeracaoTermoDoacao statusGeracaoTermoDoacao) {
        this.statusGeracaoTermoDoacao = statusGeracaoTermoDoacao;
    }

    public EnumSituacaoGeracaoTermos getSituacaoGeracaoTermos() {
        return situacaoGeracaoTermos;
    }

    public void setSituacaoGeracaoTermos(EnumSituacaoGeracaoTermos situacaoGeracaoTermos) {
        this.situacaoGeracaoTermos = situacaoGeracaoTermos;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public boolean isBuscarSomenteOsOfosComTermosJaGerados() {
        return buscarSomenteOsOfosComTermosJaGerados;
    }

    public void setBuscarSomenteOsOfosComTermosJaGerados(boolean buscarSomenteOsOfosComTermosJaGerados) {
        this.buscarSomenteOsOfosComTermosJaGerados = buscarSomenteOsOfosComTermosJaGerados;
    }

    public Long getIdTermoRecebimentoDefinitivo() {
        return idTermoRecebimentoDefinitivo;
    }

    public void setIdTermoRecebimentoDefinitivo(Long idTermoRecebimentoDefinitivo) {
        this.idTermoRecebimentoDefinitivo = idTermoRecebimentoDefinitivo;
    }

    public String getNumeroProcessoSei() {
        return numeroProcessoSei;
    }

    public void setNumeroProcessoSei(String numeroProcessoSei) {
        this.numeroProcessoSei = numeroProcessoSei;
    }

    public String getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(String usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public List<ObjetoFornecimentoContrato> getListaDeObjetosFornecimentoContrato() {
        return listaDeObjetosFornecimentoContrato;
    }

    public void setListaDeObjetosFornecimentoContrato(List<ObjetoFornecimentoContrato> listaDeObjetosFornecimentoContrato) {
        this.listaDeObjetosFornecimentoContrato = listaDeObjetosFornecimentoContrato;
    }
}
