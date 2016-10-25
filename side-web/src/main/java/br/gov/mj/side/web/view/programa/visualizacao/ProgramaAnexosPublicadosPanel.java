package br.gov.mj.side.web.view.programa.visualizacao;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.convert.IConverter;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaAvaliacaoPublicado;
import br.gov.mj.side.entidades.programa.inscricao.analise.ListaElegibilidadePublicado;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.ListaPublicadoService;
import br.gov.mj.side.web.util.SideUtil;
import br.gov.mj.side.web.view.components.converters.LocalDateTimeConverter;

public class ProgramaAnexosPublicadosPanel extends Panel {
    private static final long serialVersionUID = 1L;

    @Inject
    private ListaPublicadoService listaPublicadoService;

    @Inject
    private ComponentFactory componentFactory;

    private Programa programa;
    private Boolean mostrarPanelAnexosPublicados;

    public ProgramaAnexosPublicadosPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        add(newListViewListaElegibilidadePublicado());
        add(newListViewListaAvalicaoPublicado());
    }

    private PropertyListView<ListaElegibilidadePublicado> newListViewListaElegibilidadePublicado() {

        return new PropertyListView<ListaElegibilidadePublicado>("listaElegibilidade", getLista()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<ListaElegibilidadePublicado> item) {
                item.add(new Label("nomeArquivo"));
                item.add(new Label("dataCadastro") {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new LocalDateTimeConverter();
                    }
                });
                item.add(new Label("tamanhoArquivoEmMB"));
                item.add(newButtonDownload(item));

            }

            private Button newButtonDownload(ListItem<ListaElegibilidadePublicado> item) {
                Button btn = componentFactory.newButton("btnDownload", () -> downloadElegibilidade(item));
                btn.setDefaultFormProcessing(false);
                return btn;
            }
        };
    }

    private PropertyListView<ListaAvaliacaoPublicado> newListViewListaAvalicaoPublicado() {

        return new PropertyListView<ListaAvaliacaoPublicado>("listaAvaliacao", getListaAvaliacaoPublicado()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<ListaAvaliacaoPublicado> item) {
                item.add(new Label("nomeArquivo"));
                item.add(new Label("dataCadastro") {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unchecked")
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new LocalDateTimeConverter();
                    }
                });
                item.add(new Label("tamanhoArquivoEmMB"));
                item.add(newButtonDownload(item));

            }

            private Button newButtonDownload(ListItem<ListaAvaliacaoPublicado> item) {
                Button btn = componentFactory.newButton("btnDownload", () -> downloadAvaliacao(item));
                btn.setDefaultFormProcessing(false);
                return btn;
            }
        };
    }

    private List<ListaAvaliacaoPublicado> getListaAvaliacaoPublicado() {
        if(programa.getId()!= null){
            return SideUtil.convertAnexoDtoToEntityListaAvaliacaoPublicado(listaPublicadoService.buscarListaAvaliacaoPublicadoPeloIdPrograma(programa.getId()));
        }
        return Collections.emptyList();
    }

    private List<ListaElegibilidadePublicado> getLista() {
        if (programa.getId() == null) {
            mostrarPanelAnexosPublicados = false;
            return Collections.emptyList();
        }
        List<ListaElegibilidadePublicado> lista = SideUtil.convertAnexoDtoToEntityListaElegibilidadePublicado(listaPublicadoService.buscarListaElegibilidadePublicadoPeloIdPrograma(programa.getId()));
        if(lista.size()>0){
            mostrarPanelAnexosPublicados = true;
        }else{
            mostrarPanelAnexosPublicados = false;
        }
        
        return lista;
    }

    private void downloadElegibilidade(ListItem<ListaElegibilidadePublicado> item) {
        ListaElegibilidadePublicado lista = item.getModelObject();
        if (lista.getId() != null) {
            AnexoDto retorno = listaPublicadoService.buscarListaElegibilidadePublicadoPeloId(lista.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(lista.getConteudo(), lista.getNomeArquivo());
        }
        
    }

    private void downloadAvaliacao(ListItem<ListaAvaliacaoPublicado> item) {
        ListaAvaliacaoPublicado lista = item.getModelObject();
        if (lista.getId() != null) {
            AnexoDto retorno = listaPublicadoService.buscarListaAvaliacaoPublicadoPeloId(lista.getId());
            SideUtil.download(retorno.getConteudo(), retorno.getNomeAnexo());
        } else {
            SideUtil.download(lista.getConteudo(), lista.getNomeArquivo());
        }
    }

    public Boolean getMostrarPanelAnexosPublicados() {
        return mostrarPanelAnexosPublicados;
    }

    public void setMostrarPanelAnexosPublicados(Boolean mostrarPanelAnexosPublicados) {
        this.mostrarPanelAnexosPublicados = mostrarPanelAnexosPublicados;
    }
}
