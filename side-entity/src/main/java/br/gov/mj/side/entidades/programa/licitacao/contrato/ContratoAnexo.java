package br.gov.mj.side.entidades.programa.licitacao.contrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumTipoArquivoContrato;

@Entity
@Table(name = "tb_can_anexo_contrato", schema = "side")
public class ContratoAnexo extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "can_id_anexo_contrato")
    @SequenceGenerator(name = "tb_can_anexo_contrato_generator", sequenceName = "side.seq_tb_can_anexo_contrato", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_can_anexo_contrato_generator")
    @NotNull
    private Long id;

    @Column(name = "can_no_nome_anexo")
    @NotNull
    private String nomeAnexo;

    @ManyToOne
    @JoinColumn(name = "can_fk_contrato_id_contrato")
    @NotNull
    private Contrato contrato;

    @Column(name = "can_ct_conteudo")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] conteudo;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoArquivoContrato", name = "enumClassName"))
    @Column(name = "can_tp_tipo_arquivo")
    @NotNull
    private EnumTipoArquivoContrato tipoArquivoContrato;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "can_dt_data_documento")
    @NotNull
    private LocalDate dataDocumento;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "can_dt_data_cadastro_documento")
    @NotNull
    private LocalDateTime dataCadastro;

    @Column(name = "can_vl_tamanho_anexo")
    @NotNull
    private Long tamanho;

    public ContratoAnexo() {
    }

    @Override
    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public EnumTipoArquivoContrato getTipoArquivoContrato() {
        return tipoArquivoContrato;
    }

    public void setTipoArquivoContrato(EnumTipoArquivoContrato tipoArquivoContrato) {
        this.tipoArquivoContrato = tipoArquivoContrato;
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

    @Transient
    public BigDecimal getTamanhoArquivoEmMB() {
        Long tamanhoArquivo = new Long("0");
        if (this.tamanho != null) {
            tamanhoArquivo = this.tamanho;
        } else if (this.conteudo != null) {
            tamanhoArquivo = new Long(this.conteudo.length);
        }
        BigDecimal valor = new BigDecimal(String.valueOf(tamanhoArquivo));
        BigDecimal mega = new BigDecimal("1024");
        valor = valor.divide(mega).divide(mega);
        return valor.setScale(2, BigDecimal.ROUND_UP);
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

}
