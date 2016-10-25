package br.gov.mj.side.entidades.programa.patrimoniamento;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.programa.licitacao.contrato.ObjetoFornecimentoContrato;

@Entity
@Table(name = "tb_pof_patrimonio_objeto_fornecimento", schema = "side")
public class PatrimonioObjetoFornecimento extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pof_id_patrimonio_objeto_fornecimento")
    @SequenceGenerator(name = "seq_tb_pof_patrimonio_objeto_fornecimento_generator", sequenceName = "side.seq_tb_pof_patrimonio_objeto_fornecimento", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_pof_patrimonio_objeto_fornecimento_generator")
    @NotNull
    private Long id;
    
    @OneToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name = "pof_fk_arq_id_codigo_unico_arquivo_cadastrado")
    private ArquivoUnico arquivoUnico; 

    @ManyToOne
    @JoinColumn(name = "pof_fk_ofo_id_objeto_fornecimento_contrato")
    @NotNull
    private ObjetoFornecimentoContrato objetoFornecimentoContrato;

    @Column(name = "pof_no_numero_patrimonio")
    private String numeroPatrimonio;

    @Column(name = "pof_no_nome_item")
    private String nomeItem;
    
    @Column(name = "pof_no_nome_anexo")
    private String nomeAnexo;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "pof_dt_data_cadastro_anexo")
    @NotNull
    private LocalDateTime dataCadastro;
    
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "pof_dt_data_foto")
    private LocalDateTime dataFoto;
    
    @Column(name = "pof_ds_latitude_longitude_foto")
    private String latitudeLongitudeFoto;
    
    @Column(name = "pof_no_numero_hash_unico")
    private String numeroHashFotoUnica;
    
    @Column(name = "pof_vl_tamanho_anexo")
    private Long tamanho;

    @Column(name = "pof_ct_conteudo_anexo")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] conteudo;

    @Column(name = "pof_no_motivo_item_nao_patrimoniavel")
    private String motivoItemNaoPatrimoniavel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObjetoFornecimentoContrato getObjetoFornecimentoContrato() {
        return objetoFornecimentoContrato;
    }

    public void setObjetoFornecimentoContrato(ObjetoFornecimentoContrato objetoFornecimentoContrato) {
        this.objetoFornecimentoContrato = objetoFornecimentoContrato;
    }

    public String getNumeroPatrimonio() {
        return numeroPatrimonio;
    }

    public void setNumeroPatrimonio(String numeroPatrimonio) {
        this.numeroPatrimonio = numeroPatrimonio;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataFoto() {
        return dataFoto;
    }

    public void setDataFoto(LocalDateTime dataFoto) {
        this.dataFoto = dataFoto;
    }

    public String getLatitudeLongitudeFoto() {
        return latitudeLongitudeFoto;
    }

    public void setLatitudeLongitudeFoto(String latitudeLongitudeFoto) {
        this.latitudeLongitudeFoto = latitudeLongitudeFoto;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public String getNumeroHashFotoUnica() {
        return numeroHashFotoUnica;
    }

    public void setNumeroHashFotoUnica(String numeroHashFotoUnica) {
        this.numeroHashFotoUnica = numeroHashFotoUnica;
    }

    public ArquivoUnico getArquivoUnico() {
        return arquivoUnico;
    }

    public void setArquivoUnico(ArquivoUnico arquivoUnico) {
        this.arquivoUnico = arquivoUnico;
    }

    public String getMotivoItemNaoPatrimoniavel() {
        return motivoItemNaoPatrimoniavel;
    }

    public void setMotivoItemNaoPatrimoniavel(String motivoItemNaoPatrimoniavel) {
        this.motivoItemNaoPatrimoniavel = motivoItemNaoPatrimoniavel;
    }
}
