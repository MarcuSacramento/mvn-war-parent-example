package br.gov.mj.side.web.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.web.dao.RecursoFinanceiroDAO;
import br.gov.mj.side.web.dto.AcaoEmendaComSaldoDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class RecursoFinanceiroService {

    @Inject
    private RecursoFinanceiroDAO recursoFinanceiroDAO;

    @Inject
    private IGenericPersister genericPersister;

    public AcaoEmendaComSaldoDto buscarSaldoAcaoOrcamentaria(AcaoOrcamentaria acaoOrcamentaria) {

        if (acaoOrcamentaria == null || acaoOrcamentaria.getId() == null) {
            throw new IllegalArgumentException("Parâmetro AcaoOrcamentaria não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Ação Orçamentária com esse id cadastrado */

        AcaoOrcamentaria ao = genericPersister.findByUniqueProperty(AcaoOrcamentaria.class, "id", acaoOrcamentaria.getId());
        if (ao == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN017", acaoOrcamentaria.getId());
            throw ex;
        }

        return new AcaoEmendaComSaldoDto(recursoFinanceiroDAO.buscarEmendasComSaldo(acaoOrcamentaria.getId()), recursoFinanceiroDAO.buscarSaldoAcaoOrcamentaria(acaoOrcamentaria.getId()));

    }

}
