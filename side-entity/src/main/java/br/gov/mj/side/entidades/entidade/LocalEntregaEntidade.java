package br.gov.mj.side.entidades.entidade;

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

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumStatusLocalEntrega;

@Entity
@Table(name = "tb_lee_local_entrega_entidade", schema = "side")
public class LocalEntregaEntidade extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "lee_id_local_entrega_entidade")
    @SequenceGenerator(name = "tb_lee_local_entrega_entidade_generator", sequenceName = "side.seq_tb_lee_local_entrega_entidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_lee_local_entrega_entidade_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lee_fk_ent_id_entidade")
    @NotNull
    private Entidade entidade;

    @Column(name = "lee_no_nome_endereco")
    @NotNull
    private String nomeEndereco;

    @ManyToOne
    @JoinColumn(name = "lee_fk_mun_id_municipio")
    @NotNull
    private Municipio municipio;

    @ManyToOne
    @JoinColumn(name = "lee_fk_ted_id_tipo_endereco")
    @NotNull
    private TipoEndereco tipoEndereco;

    @Column(name = "lee_ds_descricao_endereco")
    @NotNull
    private String descricaoEndereco;

    @Column(name = "lee_nu_numero_endereco")
    @NotNull
    private String numeroEndereco;

    @Column(name = "lee_ds_descricao_complemento")
    private String complementoEndereco;

    @Column(name = "lee_ds_descricao_bairro")
    @NotNull
    private String bairro;

    @Column(name = "lee_nu_numero_cep")
    @NotNull
    private String numeroCep;

    @Column(name = "lee_nu_numero_telefone")
    @NotNull
    private String numeroTelefone;

    @Column(name = "lee_nu_numero_fone_fax")
    private String numeroFoneFax;

    @Column(name = "lee_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "lee_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "lee_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "lee_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusLocalEntrega", name = "enumClassName"))
    @Column(name = "lee_st_status_local_entrega_entidade")
    @NotNull
    private EnumStatusLocalEntrega statusLocalEntrega;

    @Transient
    private boolean possuiVinculo;

    public LocalEntregaEntidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnderecoCompleto() {
        return (getTipoEndereco().getDescricaoTipoEndereco()!=null?getTipoEndereco().getDescricaoTipoEndereco():"") + " " +
               (getDescricaoEndereco()!=null?getDescricaoEndereco():"") + " " +
        	   (getComplementoEndereco()!=null?getComplementoEndereco():"") + " - Nº: " +
        	   (getNumeroEndereco()!=null?getNumeroEndereco():"") + " - Bairro: " +
               (getBairro()!=null?getBairro():"") + " - Cidade: " +
        	   (getMunicipio().getNomeMunicipio()!=null?getMunicipio().getNomeMunicipio():"") + " - " +
               (getMunicipio().getUf().getSiglaUf()!=null?getMunicipio().getUf().getSiglaUf():"") + " - CEP:"+ 
        	   (getNumeroCep()!=null?getNumeroCep():"");
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public String getNomeEndereco() {
        return nomeEndereco;
    }

    public void setNomeEndereco(String nomeEndereco) {
        this.nomeEndereco = nomeEndereco;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
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

    public EnumStatusLocalEntrega getStatusLocalEntrega() {
        return statusLocalEntrega;
    }

    public void setStatusLocalEntrega(EnumStatusLocalEntrega statusLocalEntrega) {
        this.statusLocalEntrega = statusLocalEntrega;
    }

    public boolean isPossuiVinculo() {
        return possuiVinculo;
    }

    public void setPossuiVinculo(boolean possuiVinculo) {
        this.possuiVinculo = possuiVinculo;
    }
}