package br.gov.mj.side.web.view.programa.inscricao;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.converter.MoneyBigDecimalConverter;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.web.service.ProgramaService;
import br.gov.mj.side.web.util.DataUtil;

public class DadosProgramaPanel extends Panel{
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProgramaService programaService;
    
    private Programa programa;

    public DadosProgramaPanel(String id, Programa programa) {
        super(id);
       this.programa = programa;

       initComponents();
       
    }
    
    private void initComponents() {
        add(newLabelCodigoPrograma());
        add(newLabelNomePrograma());
        add(newLabelPeriodoRecebimentoProposta());
        add(newLabelValorTotalPrograma());
        add(newOrgaoExecutor());
    }

    private Label newLabelCodigoPrograma() {
        return new Label("codigoIdentificadorProgramaPublicado",programa.getCodigoIdentificadorProgramaPublicado());
    }
    
    private Label newLabelNomePrograma() {
        return new Label("nomePrograma",programa.getNomePrograma());
    }
    
    private Label newLabelPeriodoRecebimentoProposta() {
         String periodo = "-";
         List<ProgramaHistoricoPublicizacao> historico = programaService.buscarHistoricoPublicizacao(programa);
         if (!historico.isEmpty()) {
             ProgramaHistoricoPublicizacao publicizacao = historico.get(0);
             periodo = DataUtil.converteDataDeLocalDateParaString(publicizacao.getDataInicialProposta(),"dd/MM/yyyy") + " a " + DataUtil.converteDataDeLocalDateParaString(publicizacao.getDataFinalProposta(),"dd/MM/yyyy");
         }
        return new Label("periodoRecebimentoProposta", periodo);
    }
    
    private Label newLabelValorTotalPrograma(){
        return new Label("valorTotalPrograma",getValorTotalPrograma()){
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new MoneyBigDecimalConverter();
            }
        };
    }
    
    private BigDecimal getValorTotalPrograma() {
        BigDecimal total = BigDecimal.ZERO;
        List<ProgramaRecursoFinanceiro> recursos = programaService.buscarProgramaRecursoFinanceiroPeloIdPrograma(programa);
        if(!recursos.isEmpty()){
            for (ProgramaRecursoFinanceiro recurso : recursos) {
                total = total.add(recurso.getTotal());
            }
        }
        return total;
    }
    
    private Label newOrgaoExecutor(){
        return new Label("orgaoExecutor",programa.getUnidadeExecutora().getOrgao().getNomeOrgao());
        
    }
    
}
