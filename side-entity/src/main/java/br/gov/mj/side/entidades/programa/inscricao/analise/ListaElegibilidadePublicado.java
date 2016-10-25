package br.gov.mj.side.entidades.programa.inscricao.analise;

import java.math.BigDecimal;
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
import br.gov.mj.side.entidades.enums.EnumTipoLista;
import br.gov.mj.side.entidades.programa.Programa;

@Entity
@Table(name = "tb_lep_lista_elegibilidade_publicado", schema = "side")
public class ListaElegibilidadePublicado extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "lep_id_lista_elegibilidade")
    @SequenceGenerator(name = "tb_lep_lista_elegibilidade_publicado_generator", sequenceName = "side.seq_tb_lep_lista_elegibilidade_publicado", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_lep_lista_elegibilidade_publicado_generator")
    @NotNull
    private Long id;

    @Column(name = "lep_no_nome_arquivo")
    @NotNull
    private String nomeArquivo;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoLista", name = "enumClassName"))
    @Column(name = "lep_tp_tipo_lista")
    @NotNull
    private EnumTipoLista tipoLista;

    @ManyToOne
    @JoinColumn(name = "lep_fk_prg_id_programa")
    @NotNull
    private Programa programa;

    @Column(name = "lep_ct_conteudo_anexo")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] conteudo;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "lep_dt_data_cadastro_documento")
    @NotNull
    private LocalDateTime dataCadastro;

    @Column(name = "lep_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "lep_vl_tamanho_anexo")
    @NotNull
    private Long tamanho;

    public ListaElegibilidadePublicado() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public EnumTipoLista getTipoLista() {
        return tipoLista;
    }

    public void setTipoLista(EnumTipoLista tipoLista) {
        this.tipoLista = tipoLista;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

}