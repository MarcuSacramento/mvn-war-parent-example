package br.gov.mj.side.entidades.programa.licitacao.contrato;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_foc_formatacao_contrato", schema = "side")
public class FormatacaoContrato extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "foc_id_formatacao_contrato")
	@SequenceGenerator(name = "seq_tb_foc_formatacao_contrato_generator", sequenceName = "side.seq_tb_foc_formatacao_contrato", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_foc_formatacao_contrato_generator")
	@NotNull
	private Long id;

	@ManyToOne
	@JoinColumn(name = "foc_fk_con_id_contrato")
	@NotNull
	private Contrato contrato;

	@Column(name = "foc_no_usuario_cadastro")
	@NotNull
	private String usuarioCadastro;

	@Column(name = "foc_no_usuario_alteracao")
	private String usuarioAlteracao;

	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	@Column(name = "foc_dt_data_cadastro")
	@NotNull
	private LocalDateTime dataCadastro;

	@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
	@Column(name = "foc_dt_data_alteracao")
	private LocalDateTime dataAlteracao;

	@OneToMany(mappedBy = "formatacao", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<FormatacaoItensContrato> listaItensFormatacao = new ArrayList<FormatacaoItensContrato>();

	@OneToMany(mappedBy = "formatacao", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<ItensFormatacao> itens = new ArrayList<ItensFormatacao>();

	public List<ItensFormatacao> getItens() {
		return itens;
	}

	public void setItens(List<ItensFormatacao> itens) {
		this.itens = itens;
	}

	@Override
	public Long getId() {
		return id;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public List<FormatacaoItensContrato> getListaItensFormatacao() {
		return listaItensFormatacao;
	}

	public void setListaItensFormatacao(List<FormatacaoItensContrato> listaItensFormatacao) {
		this.listaItensFormatacao = listaItensFormatacao;
	}

	public void setId(Long id) {
		this.id = id;
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

}
