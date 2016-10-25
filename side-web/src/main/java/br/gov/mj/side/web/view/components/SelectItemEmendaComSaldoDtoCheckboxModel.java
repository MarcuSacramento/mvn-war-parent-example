package br.gov.mj.side.web.view.components;

import java.util.List;

import org.apache.wicket.model.IModel;

import br.gov.mj.side.web.dto.EmendaComSaldoDto;

public class SelectItemEmendaComSaldoDtoCheckboxModel implements IModel<Boolean> {

    private static final long serialVersionUID = 1L;

    private List<EmendaComSaldoDto> listEntity;
    private EmendaComSaldoDto entity;

    public SelectItemEmendaComSaldoDtoCheckboxModel(List<EmendaComSaldoDto> listEntity, EmendaComSaldoDto entity) {
        this.listEntity = listEntity;
        this.entity = entity;
    }

    @Override
    public void detach() {
        // Verificar a usabilidade desse método
    }

    @Override
    public Boolean getObject() {
        for (EmendaComSaldoDto emendaComSaldoDto : listEntity) {
            if (emendaComSaldoDto.getEmendaParlamentar().getNumeroEmendaParlamantar().equals(entity.getEmendaParlamentar().getNumeroEmendaParlamantar())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setObject(Boolean object) {
        if (object) {
            listEntity.add(entity);
        } else {
            listEntity.remove(entity);
        }
    }
}
