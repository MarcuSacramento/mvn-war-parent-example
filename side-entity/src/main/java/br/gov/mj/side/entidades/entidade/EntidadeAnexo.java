package br.gov.mj.side.entidades.entidade;

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
import br.gov.mj.side.entidades.enums.EnumTipoArquivoEntidade;

@Entity
@Table(name = "tb_ean_entidade_anexo", schema = "side")
public class EntidadeAnexo extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ean_id_entidade_anexo")
    @SequenceGenerator(name = "tb_ean_entidade_anexo_generator", sequenceName = "side.seq_tb_ean_entidade_anexo", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ean_entidade_anexo_generator")
    @NotNull
    private Long id;

    @Column(name = "ean_no_nome_anexo")
    @NotNull
    private String nomeAnexo;

    @ManyToOne
    @JoinColumn(name = "ean_fk_ent_id_entidade")
    @NotNull
    private Entidade entidade;

    @Column(name = "ean_ct_conteudo_anexo")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] conteudo;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoArquivoEntidade", name = "enumClassName"))
    @Column(name = "ean_tp_tipo_arquivo")
    @NotNull
    private EnumTipoArquivoEntidade tipoArquivo;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "ean_dt_data_documento")
    @NotNull
    private LocalDate dataDocumento;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ean_dt_data_cadastro_documento")
    @NotNull
    private LocalDateTime dataCadastro;

    @Column(name = "ean_vl_tamanho_anexo")
    @NotNull
    private Long tamanho;

    public EntidadeAnexo() {
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

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
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

    public EnumTipoArquivoEntidade getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(EnumTipoArquivoEntidade tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

}