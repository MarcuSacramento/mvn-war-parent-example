package br.gov.mj.side.entidades.programa.licitacao;

import java.util.ArrayList;
import java.util.Comparator;
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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.side.entidades.enums.EnumTipoAgrupamentoLicitacao;
import br.gov.mj.side.entidades.programa.licitacao.contrato.Contrato;

@Entity
@Table(name = "tb_agl_agrupamento_licitacao", schema = "side")
public class AgrupamentoLicitacao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "agl_id_agrupamento_licitacao")
    @SequenceGenerator(name = "tb_agl_agrupamento_licitacao_generator", sequenceName = "side.seq_tb_agl_agrupamento_licitacao", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_agl_agrupamento_licitacao_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agl_fk_lip_id_licitacao_programa")
    @NotNull
    private LicitacaoPrograma licitacaoPrograma;

    @Column(name = "agl_no_nome_agrupamento")
    @NotNull
    private String nomeAgrupamento;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.side.entidades.enums.EnumTipoAgrupamentoLicitacao", name = "enumClassName"))
    @Column(name = "agl_tp_tipo_agrupamento_licitacao")
    @NotNull
    private EnumTipoAgrupamentoLicitacao tipoAgrupamentoLicitacao;

    @OneToMany(mappedBy = "agrupamentoLicitacao", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<SelecaoItem> listaSelecaoItem = new ArrayList<SelecaoItem>();
    
    @ManyToOne
    @JoinColumn(name = "agl_fk_con_id_contrato")
    private Contrato contrato;

    public AgrupamentoLicitacao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LicitacaoPrograma getLicitacaoPrograma() {
        return licitacaoPrograma;
    }

    public void setLicitacaoPrograma(LicitacaoPrograma licitacaoPrograma) {
        this.licitacaoPrograma = licitacaoPrograma;
    }

    public String getNomeAgrupamento() {
        return nomeAgrupamento;
    }

    public void setNomeAgrupamento(String nomeAgrupamento) {
        this.nomeAgrupamento = nomeAgrupamento;
    }

    public EnumTipoAgrupamentoLicitacao getTipoAgrupamentoLicitacao() {
        return tipoAgrupamentoLicitacao;
    }

    public void setTipoAgrupamentoLicitacao(EnumTipoAgrupamentoLicitacao tipoAgrupamentoLicitacao) {
        this.tipoAgrupamentoLicitacao = tipoAgrupamentoLicitacao;
    }

    public List<SelecaoItem> getListaSelecaoItem() {
        return listaSelecaoItem;
    }

    public void setListaSelecaoItem(List<SelecaoItem> listaSelecaoItem) {
        this.listaSelecaoItem = listaSelecaoItem;
    }
    
    public static Comparator<AgrupamentoLicitacao> getComparator(int order, String coluna) {
        return new Comparator<AgrupamentoLicitacao>() {
            @Override
            public int compare(AgrupamentoLicitacao o1, AgrupamentoLicitacao o2) {
                int valor = 0;

                if ("tipo".equalsIgnoreCase(coluna)) {
                    valor = o1.getTipoAgrupamentoLicitacao().compareTo(o2.getTipoAgrupamentoLicitacao()) * order;
                } 
                return valor;
            }
        };
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

}