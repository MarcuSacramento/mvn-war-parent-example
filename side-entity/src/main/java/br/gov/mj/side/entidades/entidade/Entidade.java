package br.gov.mj.side.entidades.entidade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.apoio.entidades.Municipio;
import br.gov.mj.apoio.entidades.TipoEndereco;
import br.gov.mj.apoio.entidades.TipoEntidade;
import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumOrigemCadastro;
import br.gov.mj.side.entidades.enums.EnumPerfilEntidade;
import br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica;
import br.gov.mj.side.entidades.enums.EnumStatusEntidade;
import br.gov.mj.side.entidades.enums.EnumValidacaoCadastro;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;

@Entity
@Table(name = "tb_ent_entidade", schema = "side")
public class Entidade extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ent_id_entidade")
    @SequenceGenerator(name = "tb_ent_entidade_generator", sequenceName = "side.seq_tb_ent_entidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ent_entidade_generator")
    @NotNull
    private Long id;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusEntidade", name = "enumClassName"))
    @Column(name = "ent_st_status_entidade")
    @NotNull
    private EnumStatusEntidade statusEntidade;

    @Column(name = "ent_nu_numero_cnpj")
    @NotNull
    private String numeroCnpj;

    @ManyToOne
    @JoinColumn(name = "ent_fk_ten_id_tipo_entidade")
    private TipoEntidade tipoEntidade;

    @Column(name = "ent_no_nome_entidade")
    @NotNull
    private String nomeEntidade;

    @Column(name = "ent_no_nome_contato")
    private String nomeContato;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumPersonalidadeJuridica", name = "enumClassName"))
    @Column(name = "ent_tp_tipo_natureza_juridica")
    @NotNull
    private EnumPersonalidadeJuridica personalidadeJuridica;

    @ManyToOne
    @JoinColumn(name = "ent_fk_mun_id_municipio")
    @NotNull
    private Municipio municipio;

    @ManyToOne
    @JoinColumn(name = "ent_fk_ted_id_tipo_endereco")
    @NotNull
    private TipoEndereco tipoEndereco;

    @Column(name = "ent_ds_descricao_endereco")
    @NotNull
    private String descricaoEndereco;

    @Column(name = "ent_nu_numero_endereco")
    @NotNull
    private String numeroEndereco;

    @Column(name = "ent_ds_descricao_complemento")
    private String complementoEndereco;

    @Column(name = "ent_ds_descricao_bairro")
    @NotNull
    private String bairro;

    @Column(name = "ent_nu_numero_cep")
    @NotNull
    private String numeroCep;

    @Column(name = "ent_nu_numero_telefone")
    private String numeroTelefone;

    @Column(name = "ent_nu_numero_fone_fax")
    private String numeroFoneFax;

    @Column(name = "ent_ds_email")
    @NotNull
    private String email;

    @Column(name = "ent_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "ent_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ent_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "ent_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "ent_nu_numero_processo_sei")
    private String numeroProcessoSEI;

    @OneToMany(mappedBy = "entidade", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<EntidadeAnexo> anexos = new ArrayList<EntidadeAnexo>();

    @OneToMany(mappedBy = "entidade", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<PessoaEntidade> pessoas = new ArrayList<PessoaEntidade>();

    @OneToMany(mappedBy = "entidade", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<LocalEntregaEntidade> locaisEntregaEntidade = new ArrayList<LocalEntregaEntidade>();

    @Column(name = "ent_ob_observacoes")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "ent_tp_perfil_entidade")
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumPerfilEntidade", name = "enumClassName"))
    @NotNull
    private EnumPerfilEntidade perfilEntidade;

    @ManyToOne
    @JoinColumn(name = "ent_fk_prg_id_programa_preferencial")
    private Programa programaPreferencial;

    @Enumerated(EnumType.STRING)
    @Column(name = "ent_tp_origem_cadastro")
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumOrigemCadastro", name = "enumClassName"))
    @NotNull
    private EnumOrigemCadastro origemCadastro;

    @Enumerated(EnumType.STRING)
    @Column(name = "ent_tp_validacao_cadastro")
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumValidacaoCadastro", name = "enumClassName"))
    @NotNull
    private EnumValidacaoCadastro validacaoCadastro;

    @Column(name = "ent_ds_motivo_validacao")
    private String motivoValidacao;

    @OneToMany(mappedBy = "fornecedor", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<Contrato> contratosEntidade = new ArrayList<Contrato>();

    @Transient
    public String getCnpjENome() {

        if (numeroCnpj != null && nomeEntidade != null) {
            return imprimeCNPJ(numeroCnpj) + " - " + nomeEntidade;
        } else {
            return "";
        }
    }

    public Entidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<EntidadeAnexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<EntidadeAnexo> anexos) {
        this.anexos = anexos;
    }

    public List<PessoaEntidade> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<PessoaEntidade> pessoas) {
        this.pessoas = pessoas;
    }

    public String getNumeroProcessoSEI() {
        return numeroProcessoSEI;
    }

    public void setNumeroProcessoSEI(String numeroProcessoSEI) {
        this.numeroProcessoSEI = numeroProcessoSEI;
    }

    public String getEnderecoCompleto() {
        String endereco = getTipoEndereco().getDescricaoTipoEndereco() == null ? " " : getTipoEndereco().getDescricaoTipoEndereco() + " ";
        endereco += getDescricaoEndereco() == null ? " " : getDescricaoEndereco() + " ";
        endereco += getComplementoEndereco() == null ? " " : getComplementoEndereco() + " ";
        endereco += " - Nº: ";
        endereco += getNumeroEndereco() == null ? " " : getNumeroEndereco() + " ";
        endereco += " - Bairro: ";
        endereco += getBairro() == null ? " " : getBairro() + " ";
        endereco += " - Cidade: ";
        endereco += getMunicipio().getNomeMunicipio() == null ? " " : getMunicipio().getNomeMunicipio() + " ";
        endereco += " - ";
        endereco += getMunicipio().getUf().getSiglaUf() == null ? " " : getMunicipio().getUf().getSiglaUf() + " ";
        endereco += " - CEP:";
        endereco += getNumeroCep();

        return endereco;
    }

    public List<LocalEntregaEntidade> getLocaisEntregaEntidade() {
        return locaisEntregaEntidade;
    }

    public void setLocaisEntregaEntidade(List<LocalEntregaEntidade> locaisEntregaEntidade) {
        this.locaisEntregaEntidade = locaisEntregaEntidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public EnumPerfilEntidade getPerfilEntidade() {
        return perfilEntidade;
    }

    public void setPerfilEntidade(EnumPerfilEntidade perfilEntidade) {
        this.perfilEntidade = perfilEntidade;
    }

    public List<Contrato> getContratosEntidade() {
        return contratosEntidade;
    }

    public void setContratosEntidade(List<Contrato> contratosEntidade) {
        this.contratosEntidade = contratosEntidade;
    }

    public String getNomeContato() {
        return nomeContato;
    }

    public void setNomeContato(String nomeContato) {
        this.nomeContato = nomeContato;
    }

    @Transient
    public String imprimeCNPJ(String cnpj) {
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "." + cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
    }

    public Programa getProgramaPreferencial() {
        return programaPreferencial;
    }

    public void setProgramaPreferencial(Programa programaPreferencial) {
        this.programaPreferencial = programaPreferencial;
    }

    public EnumOrigemCadastro getOrigemCadastro() {
        return origemCadastro;
    }

    public void setOrigemCadastro(EnumOrigemCadastro origemCadastro) {
        this.origemCadastro = origemCadastro;
    }

    public EnumValidacaoCadastro getValidacaoCadastro() {
        return validacaoCadastro;
    }

    public void setValidacaoCadastro(EnumValidacaoCadastro validacaoCadastro) {
        this.validacaoCadastro = validacaoCadastro;
    }

    public String getMotivoValidacao() {
        return motivoValidacao;
    }

    public void setMotivoValidacao(String motivoValidacao) {
        this.motivoValidacao = motivoValidacao;
    }

}