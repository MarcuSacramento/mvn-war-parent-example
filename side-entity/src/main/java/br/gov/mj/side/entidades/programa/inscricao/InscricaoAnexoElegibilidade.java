package br.gov.mj.side.entidades.programa.inscricao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_aie_anexo_inscricao_elegibilidade", schema = "side")
public class InscricaoAnexoElegibilidade extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "aie_id_anexo_inscricao_elegibilidade")
    @SequenceGenerator(name = "tb_aie_anexo_inscricao_elegibilidade_generator", sequenceName = "side.seq_tb_aie_anexo_inscricao_elegibilidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_aie_anexo_inscricao_elegibilidade_generator")
    @NotNull
    private Long id;

    @Column(name = "aie_no_nome_anexo")
    @NotNull
    private String nomeAnexo;

    @ManyToOne
    @JoinColumn(name = "aie_fk_ipe_id_inscricao_programa_criterio_elegibilidade")
    @NotNull
    private InscricaoProgramaCriterioElegibilidade inscricaoProgramaCriterioElegibilidade;

    @Column(name = "aie_ct_conteudo_anexo")
    @Type(type = "org.hibernate.type.BinaryType")
    @NotNull
    private byte[] conteudo;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "aie_dt_data_cadastro_documento")
    @NotNull
    private LocalDateTime dataCadastro;

    @Column(name = "aie_vl_tamanho_anexo")
    @NotNull
    private Long tamanho;

    public InscricaoAnexoElegibilidade() {
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

    public InscricaoProgramaCriterioElegibilidade getInscricaoProgramaCriterioElegibilidade() {
        return inscricaoProgramaCriterioElegibilidade;
    }

    public void setInscricaoProgramaCriterioElegibilidade(InscricaoProgramaCriterioElegibilidade inscricaoProgramaCriterioElegibilidade) {
        this.inscricaoProgramaCriterioElegibilidade = inscricaoProgramaCriterioElegibilidade;
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

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
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

}