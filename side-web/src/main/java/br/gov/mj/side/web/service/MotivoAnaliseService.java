package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.MotivoAnalise;
import br.gov.mj.side.web.dao.MotivoAnaliseDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MotivoAnaliseService {

    @Inject
    private MotivoAnaliseDAO motivoAnaliseDAO;

    public MotivoAnalise buscarPeloId(Long id) {
        return motivoAnaliseDAO.buscarPeloId(id);
    }

    public List<MotivoAnalise> buscarTodos() {
        return motivoAnaliseDAO.buscarTodos();
    }

}
