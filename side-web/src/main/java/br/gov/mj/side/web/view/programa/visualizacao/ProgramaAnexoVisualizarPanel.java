package br.gov.mj.side.web.view.programa.visualizacao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import br.gov.mj.infra.wicket.component.ComponentFactory;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaAnexo;
import br.gov.mj.side.web.dto.AnexoDto;
import br.gov.mj.side.web.service.AnexoProgramaService;
import br.gov.mj.side.web.util.SideUtil;

public class ProgramaAnexoVisualizarPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private Programa programa;
    private List<ProgramaAnexo> listAnexos = new ArrayList<ProgramaAnexo>();
    private Form<Programa> form;

    @Inject
    private ComponentFactory componentFactory;

    @Inject
    private AnexoProgramaService anexoService;

    public ProgramaAnexoVisualizarPanel(String id, Programa programa) {
        super(id);
        this.programa = programa;
        listAnexos = SideUtil.convertAnexoDtoToEntityProgramaAnexo(anexoService.buscarPeloIdPrograma(programa.getId()));
        
        
        form = componentFactory.newForm("form", new CompoundPropertyModel<Programa>(programa));
        add(form);

        form.add(getDataViewAnexos()); // dataViewAnexos
    }

    private DataView<ProgramaAnexo> getDataViewAnexos() {

        return new DataView<ProgramaAnexo>("dataViewAnexos", new ListDataProvider<ProgramaAnexo>(listAnexos)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<ProgramaAnexo> item) {
                item.add(new Label("nomeAnexo", item.getModelObject().getNomeAnexo()));
                Button btnDownload = componentFactory.newButton("btnDonwload", () -> download(item));
                btnDownload.setEnabled(true);
                item.add(btnDownload);
            }
        };
    }

    private void download(Item<ProgramaAnexo> item) {
        ProgramaAnexo a = item.getModelObject();
        if (a.getId() != null) {
            AnexoDto anexo =  anexoService.buscarPeloId(a.getId());
            SideUtil.download(anexo.getConteudo(), anexo.getNomeAnexo());
        } else {
            SideUtil.download(a.getConteudo(), a.getNomeAnexo());
        }
    }

    public List<ProgramaAnexo> getListAnexos() {
        return listAnexos;
    }

    public void setListAnexos(List<ProgramaAnexo> listAnexos) {
        this.listAnexos = listAnexos;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }
}