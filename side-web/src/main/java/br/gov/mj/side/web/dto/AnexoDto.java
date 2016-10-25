package br.gov.mj.side.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumTipoArquivo;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoContrato;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoEntidade;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoTermoEntrega;
import br.gov.mj.side.entidades.enums.EnumTipoLista;

public class AnexoDto extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;

    private byte[] conteudo;

    private String nomeAnexo;

    private Long tamanho;

    private EnumTipoArquivo tipoArquivo;

    private EnumTipoArquivoEntidade tipoArquivoEntidade;

    private EnumTipoArquivoContrato tipoArquivoContrato;

    private LocalDate dataDocumento;

    private LocalDateTime dataCadastro;

    private String descricaoAnexo;

    private EnumTipoLista tipoLista;

    private EnumTipoArquivoTermoEntrega tipoArquivoTermoEntrega;

    private String usuarioCadastro;

    public Long getId() {
        return id;
    }

    public AnexoDto() {
        super();
    }

    public AnexoDto(Long id, String nomeAnexo, Long tamanho, byte[] conteudo) {
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.nomeAnexo = nomeAnexo;
        this.tamanho = tamanho;
    }

    public AnexoDto(Long id, String nomeAnexo, EnumTipoArquivo tipoArquivo, LocalDate dataDocumento, LocalDateTime dataCadastro, Long tamanho, byte[] conteudo) {
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.nomeAnexo = nomeAnexo;
        this.tamanho = tamanho;
        this.tipoArquivo = tipoArquivo;
        this.dataDocumento = dataDocumento;
        this.dataCadastro = dataCadastro;
    }

    public AnexoDto(Long id, String nomeAnexo, EnumTipoArquivoEntidade tipoArquivoEntidade, LocalDate dataDocumento, LocalDateTime dataCadastro, Long tamanho, byte[] conteudo) {
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.nomeAnexo = nomeAnexo;
        this.tamanho = tamanho;
        this.tipoArquivoEntidade = tipoArquivoEntidade;
        this.dataDocumento = dataDocumento;
        this.dataCadastro = dataCadastro;
    }

    public AnexoDto(Long id, String nomeAnexo, LocalDate dataDocumento, LocalDateTime dataCadastro, Long tamanho, byte[] conteudo) {
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.nomeAnexo = nomeAnexo;
        this.tamanho = tamanho;
        this.dataDocumento = dataDocumento;
        this.dataCadastro = dataCadastro;
    }

    public AnexoDto(Long id, String nomeAnexo, LocalDate dataDocumento, LocalDateTime dataCadastro, Long tamanho, byte[] conteudo, String descricaoAnexo) {
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.nomeAnexo = nomeAnexo;
        this.tamanho = tamanho;
        this.dataDocumento = dataDocumento;
        this.dataCadastro = dataCadastro;
        this.descricaoAnexo = descricaoAnexo;
    }

    public AnexoDto(Long id, String nomeAnexo, EnumTipoArquivoContrato tipoArquivoContrato, LocalDate dataDocumento, LocalDateTime dataCadastro, Long tamanho, byte[] conteudo) {
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.nomeAnexo = nomeAnexo;
        this.tamanho = tamanho;
        this.tipoArquivoContrato = tipoArquivoContrato;
        this.dataDocumento = dataDocumento;
        this.dataCadastro = dataCadastro;
    }

    // Usado para o Termo de Recebimento Definitivo
    public AnexoDto(Long id, String nomeAnexo, LocalDateTime dataCadastro) {
        super();
        this.id = id;
        this.nomeAnexo = nomeAnexo;
        this.dataCadastro = dataCadastro;
    }

    // Usado para o Termo de Recebimento Definitivo
    public AnexoDto(Long id, String nomeAnexo, LocalDateTime dataCadastro, byte[] conteudo) {
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.nomeAnexo = nomeAnexo;
        this.dataCadastro = dataCadastro;
    }

    public AnexoDto(Long id, String nomeAnexo, EnumTipoLista tipoLista, LocalDateTime dataCadastro, Long tamanho, String usuarioCadastro, byte[] conteudo) {
        super();
        this.id = id;
        this.conteudo = conteudo;
        this.nomeAnexo = nomeAnexo;
        this.tamanho = tamanho;
        this.tipoLista = tipoLista;
        this.dataCadastro = dataCadastro;
        this.usuarioCadastro = usuarioCadastro;
    }

    // Construtor usado para os AnexoNotaRemessa
    public AnexoDto(Long id, String nomeAnexo, EnumTipoArquivoTermoEntrega tipoTermoEntrega, byte[] conteudo) {
        this.id = id;
        this.nomeAnexo = nomeAnexo;
        this.tipoArquivoTermoEntrega = tipoTermoEntrega;
        this.conteudo = conteudo;
    }

    // Construtor usado para os AnexoNotaRemessa
    public AnexoDto(Long id, String nomeAnexo, EnumTipoArquivoTermoEntrega tipoTermoEntrega) {
        this.id = id;
        this.nomeAnexo = nomeAnexo;
        this.tipoArquivoTermoEntrega = tipoTermoEntrega;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public EnumTipoArquivo getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(EnumTipoArquivo tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public LocalDate getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(LocalDate dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public EnumTipoArquivoEntidade getTipoArquivoEntidade() {
        return tipoArquivoEntidade;
    }

    public void setTipoArquivoEntidade(EnumTipoArquivoEntidade tipoArquivoEntidade) {
        this.tipoArquivoEntidade = tipoArquivoEntidade;
    }

    public String getDescricaoAnexo() {
        return descricaoAnexo;
    }

    public void setDescricaoAnexo(String descricaoAnexo) {
        this.descricaoAnexo = descricaoAnexo;
    }

    public EnumTipoArquivoContrato getTipoArquivoContrato() {
        return tipoArquivoContrato;
    }

    public void setTipoArquivoContrato(EnumTipoArquivoContrato tipoArquivoContrato) {
        this.tipoArquivoContrato = tipoArquivoContrato;
    }

    public EnumTipoLista getTipoLista() {
        return tipoLista;
    }

    public void setTipoLista(EnumTipoLista tipoLista) {
        this.tipoLista = tipoLista;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public EnumTipoArquivoTermoEntrega getTipoArquivoTermoEntrega() {
        return tipoArquivoTermoEntrega;
    }

    public void setTipoArquivoTermoEntrega(EnumTipoArquivoTermoEntrega tipoArquivoTermoEntrega) {
        this.tipoArquivoTermoEntrega = tipoArquivoTermoEntrega;
    }

}
