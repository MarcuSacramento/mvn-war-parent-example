package br.gov.mj.side.entidades.programa;

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
import br.gov.mj.side.entidades.enums.EnumTipoArquivo;

@Entity
@Table(name = "tb_pan_programa_anexo", schema = "side")
public class ProgramaAnexo extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pan_id_programa_anexo")
    @SequenceGenerator(name = "TB_PAN_PROGRAMA_ANEXO_GENERATOR", sequenceName = "SIDE.SEQ_TB_PAN_PROGRAMA_ANEXO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_PAN_PROGRAMA_ANEXO_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "pan_no_nome_anexo")
    @NotNull
    private String nomeAnexo;

    @ManyToOne
    @JoinColumn(name = "pan_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @Column(name = "pan_ct_conteudo_anexo")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] conteudo;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoArquivo", name = "enumClassName"))
    @Column(name = "pan_tp_tipo_arquivo")
    @NotNull
    private EnumTipoArquivo tipoArquivo;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "pan_dt_data_documento")
    @NotNull
    private LocalDate dataDocumento;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "pan_dt_data_cadastro_documento")
    @NotNull
    private LocalDateTime dataCadastro;

    @Column(name = "pan_vl_tamanho_anexo")
    @NotNull
    private Long tamanho;

    public ProgramaAnexo() {
    }

    @Override
    public Long getId() {
        return id;
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

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public EnumTipoArquivo getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(EnumTipoArquivo tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
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

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

}