package br.gov.mj.side.entidades.entidade;

import java.time.LocalDate;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.seg.entidades.Usuario;
import br.gov.mj.seg.entidades.UsuarioPerfil;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;
import br.gov.mj.side.entidades.enums.EnumPerfilUsuario;
import br.gov.mj.side.entidades.enums.EnumStatusPessoa;
import br.gov.mj.side.entidades.enums.EnumTipoPessoa;

@Entity
@Table(name = "tb_pso_pessoa", schema = "side")
public class Pessoa extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pso_id_pessoa")
    @SequenceGenerator(name = "tb_pso_pessoa_generator", sequenceName = "side.seq_tb_pso_pessoa", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_pso_pessoa_generator")
    @NotNull
    private Long id;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumStatusPessoa", name = "enumClassName"))
    @Column(name = "pso_st_status_pessoa")
    @NotNull
    private EnumStatusPessoa statusPessoa;

    @Column(name = "pso_no_nome_pessoa")
    @NotNull
    private String nomePessoa;

    @Column(name = "pso_nu_numero_cpf")
    @NotNull
    private String numeroCpf;

    @Column(name = "pso_ds_descricao_cargo")
    @NotNull
    private String descricaoCargo;

    @Column(name = "pso_nu_numero_telefone")
    @NotNull
    private String numeroTelefone;

    @Column(name = "pso_ds_email")
    @NotNull
    private String email;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "pso_dt_data_inicio_exercicio")
    @NotNull
    private LocalDate dataInicioExercicio;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "pso_dt_data_fim_exercicio")
    private LocalDate dataFimExercicio;

    @Column(name = "pso_no_usuario_cadastro")
    @NotNull
    private String usuarioCadastro;

    @Column(name = "pso_no_usuario_alteracao")
    private String usuarioAlteracao;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "pso_dt_data_cadastro")
    @NotNull
    private LocalDateTime dataCadastro;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "pso_dt_data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "pso_ds_descricao_endereco_correspondencia")
    private String enderecoCorrespondencia;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoPessoa", name = "enumClassName"))
    @Column(name = "pso_tp_tipo_pessoa")
    @NotNull
    private EnumTipoPessoa tipoPessoa;

    @Column(name = "pso_bo_possui_funcao_representante")
    @NotNull
    private Boolean possuiFuncaoDeRepresentante;

    @OneToOne
    @JoinColumn(name = "pso_fk_usu_id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "pessoa", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<PessoaEntidade> entidades = new ArrayList<PessoaEntidade>();

    public Pessoa() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumStatusPessoa getStatusPessoa() {
        return statusPessoa;
    }

    public void setStatusPessoa(EnumStatusPessoa statusPessoa) {
        this.statusPessoa = statusPessoa;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getNumeroCpf() {
        return numeroCpf;
    }

    public void setNumeroCpf(String numeroCpf) {
        this.numeroCpf = numeroCpf;
    }

    public String getDescricaoCargo() {
        return descricaoCargo;
    }

    public void setDescricaoCargo(String descricaoCargo) {
        this.descricaoCargo = descricaoCargo;
    }

    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataInicioExercicio() {
        return dataInicioExercicio;
    }

    public void setDataInicioExercicio(LocalDate dataInicioExercicio) {
        this.dataInicioExercicio = dataInicioExercicio;
    }

    public LocalDate getDataFimExercicio() {
        return dataFimExercicio;
    }

    public void setDataFimExercicio(LocalDate dataFimExercicio) {
        this.dataFimExercicio = dataFimExercicio;
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

    public String getEnderecoCorrespondencia() {
        return enderecoCorrespondencia;
    }

    public void setEnderecoCorrespondencia(String enderecoCorrespondencia) {
        this.enderecoCorrespondencia = enderecoCorrespondencia;
    }

    public EnumTipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(EnumTipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public Boolean getPossuiFuncaoDeRepresentante() {
        return possuiFuncaoDeRepresentante;
    }

    public void setPossuiFuncaoDeRepresentante(Boolean possuiFuncaoDeRepresentante) {
        this.possuiFuncaoDeRepresentante = possuiFuncaoDeRepresentante;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<PessoaEntidade> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<PessoaEntidade> entidades) {
        this.entidades = entidades;
    }

    @Transient
    public boolean isMembroComissao() {
        return (this.tipoPessoa.equals(EnumTipoPessoa.MEMBRO_COMISSAO_RECEBIMENTO) || possuiPerfilDeMembro());
    }

    @Transient
    public boolean possuiPerfilDeMembro() {
        if (this.usuario != null) {
            for (UsuarioPerfil usuarioPerfil : this.usuario.getPerfis()) {
                if(usuarioPerfil.getPerfil().getNomePerfil().equals(EnumPerfilUsuario.MEMBRO_COMISSAO.getDescricao())){
                    return true;
                };
            }
        }
        return false;
    }

    @Transient
    public boolean isRepresentante() {
        return (this.tipoPessoa.equals(EnumTipoPessoa.REPRESENTANTE_ENTIDADE) || isTitularComFuncaoDeRepesentante());
    }

    @Transient
    public boolean isRepresentanteFornecedor() {
        return (this.tipoPessoa.equals(EnumTipoPessoa.REPRESENTANTE_LEGAL));
    }

    @Transient
    public boolean isPreposto() {
        return (this.tipoPessoa.equals(EnumTipoPessoa.PREPOSTO_FORNECEDOR));
    }

    @Transient
    public boolean isTitular() {
        return (this.tipoPessoa.equals(EnumTipoPessoa.TITULAR));
    }

    @Transient
    public boolean isTitularComFuncaoDeRepesentante() {
        return (this.tipoPessoa.equals(EnumTipoPessoa.TITULAR) && this.possuiFuncaoDeRepresentante);
    }

    @Transient
    public boolean isPessoaComFuncaoDeUsuarioInterno() {
        if (usuario == null) {
            return false;
        } else {
            return (usuario.getTipoUsuario() == EnumTipoUsuario.INTERNO);
        }
    }

}