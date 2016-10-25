package br.gov.mj.seg.entidades;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.enums.EnumPerfilUsuario;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;

@Entity
@Table(name = "tb_usu_usuario", schema = "seg")
public class Usuario extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "usu_id_usuario")
    @SequenceGenerator(name = "tb_usu_usuario_generator", sequenceName = "seg.seq_tb_usu_usuario", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_usu_usuario_generator")
    @NotNull
    private Long id;

    @Column(name = "usu_nu_numero_cpf_usuario")
    private String numeroCpf;

    @Column(name = "usu_lg_login_usuario")
    @NotNull
    private String login;

    @Column(name = "usu_sn_senha_usuario")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusPessoa", name = "enumClassName"))
    @Column(name = "usu_st_situacao_usuario")
    @NotNull
    private EnumStatusPessoa situacaoUsuario;

    @Column(name = "usu_bo_primeiro_acesso")
    @NotNull
    private Boolean possuiPrimeiroAcesso;

    @Column(name = "usu_ds_descricao_email")
    @NotNull
    private String email;

    @Column(name = "usu_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "usu_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "usu_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "usu_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "usu_ds_hash_envio_troca_senha")
    private String hashEnvioTrocaSenha;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "usu_dt_data_expiracao_senha")
    private LocalDate dataExpiracaoSenha;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "usu_dt_data_limite_troca_senha")
    private LocalDate dataLimiteTrocaSenha;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.seg.entidades.enums.EnumTipoUsuario", name = "enumClassName"))
    @Column(name = "usu_tp_tipo_usuario")
    @NotNull
    private EnumTipoUsuario tipoUsuario;

    @Column(name = "usu_no_nome_completo_usuario")
    private String nomeCompleto;

    @Column(name = "usu_no_primeiro_nome_usuario")
    private String primeiroNome;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "usuario", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private Set<UsuarioPerfil> perfis = new HashSet<UsuarioPerfil>();

    @Column(name = "usu_ds_hash_envio_altera_entidade")
    private String hashEnvioAlteraEntidade;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "usu_dt_data_expiracao_altera_entidade")
    private LocalDate dataExpiracaoAlteraEntidade;

    public Usuario() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCpf() {
        return numeroCpf;
    }

    public void setNumeroCpf(String numeroCpf) {
        this.numeroCpf = numeroCpf;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getPossuiPrimeiroAcesso() {
        return possuiPrimeiroAcesso;
    }

    public void setPossuiPrimeiroAcesso(Boolean possuiPrimeiroAcesso) {
        this.possuiPrimeiroAcesso = possuiPrimeiroAcesso;
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

    public String getHashEnvioTrocaSenha() {
        return hashEnvioTrocaSenha;
    }

    public void setHashEnvioTrocaSenha(String hashEnvioTrocaSenha) {
        this.hashEnvioTrocaSenha = hashEnvioTrocaSenha;
    }

    public LocalDate getDataExpiracaoSenha() {
        return dataExpiracaoSenha;
    }

    public void setDataExpiracaoSenha(LocalDate dataExpiracaoSenha) {
        this.dataExpiracaoSenha = dataExpiracaoSenha;
    }

    public LocalDate getDataLimiteTrocaSenha() {
        return dataLimiteTrocaSenha;
    }

    public void setDataLimiteTrocaSenha(LocalDate dataLimiteTrocaSenha) {
        this.dataLimiteTrocaSenha = dataLimiteTrocaSenha;
    }

    public EnumTipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(EnumTipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public EnumStatusPessoa getSituacaoUsuario() {
        return situacaoUsuario;
    }

    public void setSituacaoUsuario(EnumStatusPessoa situacaoUsuario) {
        this.situacaoUsuario = situacaoUsuario;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getPrimeiroNome() {
        return primeiroNome;
    }

    public void setPrimeiroNome(String primeiroNome) {
        this.primeiroNome = primeiroNome;
    }

    @Transient
    public boolean possuiPerfilRepresentante() {
        for (UsuarioPerfil usuarioPerfil : perfis) {
            if (usuarioPerfil.getPerfil().getNomePerfil().equals(EnumPerfilUsuario.REPRESENTANTE.getDescricao())) {
                return true;
            }
        }
        return false;
    }

    public String getHashEnvioAlteraEntidade() {
        return hashEnvioAlteraEntidade;
    }

    public void setHashEnvioAlteraEntidade(String hashEnvioAlteraEntidade) {
        this.hashEnvioAlteraEntidade = hashEnvioAlteraEntidade;
    }

    public LocalDate getDataExpiracaoAlteraEntidade() {
        return dataExpiracaoAlteraEntidade;
    }

    public void setDataExpiracaoAlteraEntidade(LocalDate dataExpiracaoAlteraEntidade) {
        this.dataExpiracaoAlteraEntidade = dataExpiracaoAlteraEntidade;
    }

    public Set<UsuarioPerfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(Set<UsuarioPerfil> perfis) {
        this.perfis = perfis;
    }

}