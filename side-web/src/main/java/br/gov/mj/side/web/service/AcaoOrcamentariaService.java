package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.AcaoOrcamentaria;
import br.gov.mj.side.entidades.BeneficiarioEmendaParlamentar;
import br.gov.mj.side.entidades.EmendaParlamentar;
import br.gov.mj.side.web.dao.AcaoOrcamentariaDAO;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AcaoOrcamentariaService {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private AcaoOrcamentariaDAO acaoOrcamentariaDAO;

    @Inject
    private ProgramaService programaService;

    public AcaoOrcamentaria buscarPeloId(Long id) {
        return acaoOrcamentariaDAO.buscarPeloId(id);
    }

    public List<EmendaParlamentar> buscarEmendaParlamentar(AcaoOrcamentaria acaoOrcamentaria) {

        if (acaoOrcamentaria == null || acaoOrcamentaria.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return acaoOrcamentariaDAO.buscarEmendaParlamentar(acaoOrcamentaria);
    }

    public List<BeneficiarioEmendaParlamentar> buscarBeneficiarioEmendaParlamentar(EmendaParlamentar emendaParlamentar) {

        if (emendaParlamentar == null || emendaParlamentar.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        return acaoOrcamentariaDAO.buscarBeneficiarioEmendaParlamentar(emendaParlamentar);
    }

    public AcaoOrcamentaria incluirAlterar(AcaoOrcamentaria acaoOrcamentaria, String usuarioLogado) {

        AcaoOrcamentaria acaoOrcamentariaRetorno = null;

        if (acaoOrcamentaria == null) {
            throw new IllegalArgumentException("Parâmetro acaoOrcamentaria não pode ser null");
        }

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        /*
         * Verificar se já existe acaoOrcamentaria com o mesmo nome e número
         * cadastrado, pois nesse caso não será permitida a inclusão (regra de
         * negócio)
         */
        if (!acaoOrcamentariaDAO.buscarDuplicado(acaoOrcamentaria, true, true, false, false).isEmpty()) {
            ex.addErrorMessage("MN006", acaoOrcamentaria.getAnoAcaoOrcamentaria(), acaoOrcamentaria.getNumeroAcaoOrcamentaria()); // msg
            // cadastrada
            // no arquivo
            // de
            // propriedades
        }

        // REGRA RETIRADA POR SOLICITAÇÃO DO P.O.
        // /*
        // * Verificar se já existe acaoOrcamentaria com o mesmo nome e número
        // PPA
        // * cadastrado, pois nesse caso não será permitida a inclusão (regra de
        // * negócio)
        // */
        // if (!acaoOrcamentariaDAO.buscarDuplicado(acaoOrcamentaria, false,
        // false, true, true).isEmpty()) {
        // ex.addErrorMessage("MN018", acaoOrcamentaria.getNomeProgramaPPA(),
        // acaoOrcamentaria.getNumeroProgramaPPA()); // msg
        // // cadastrada
        // // no arquivo
        // // de
        // // propriedades
        // }

        /* verificar para cada emenda se existe uma emenda duplicada */
        for (EmendaParlamentar emenda : acaoOrcamentaria.getEmendasParlamentares()) {
            for (EmendaParlamentar emenda2 : acaoOrcamentaria.getEmendasParlamentares()) {
                if (emenda.getNumeroEmendaParlamantar().equals(emenda2.getNumeroEmendaParlamantar()) && emenda.hashCode() != emenda2.hashCode()) {
                    ex.addErrorMessage("MN021", emenda.getNumeroEmendaParlamantar()); // msg
                }
            }
        }

        /*
         * verificar para cada emenda se existe vínculo com programa na inclusão
         * do recurso financeiro
         */
        if (acaoOrcamentaria.getId() != null) {
            List<EmendaParlamentar> emendasAtual = buscarPeloId(acaoOrcamentaria.getId()).getEmendasParlamentares();
            for (EmendaParlamentar emendaParlamentarAtual : emendasAtual) {
                if (!acaoOrcamentaria.getEmendasParlamentares().contains(emendaParlamentarAtual) && emendaParlamentarAtual.getId() != null) {
                    ProgramaPesquisaDto programaPesquisaDto = new ProgramaPesquisaDto();
                    programaPesquisaDto.setEmendaParlamentar(emendaParlamentarAtual);
                    if (!programaService.buscarSemPaginacao(programaPesquisaDto).isEmpty()) {
                        ex.addErrorMessage("MSG005"); /*
                                                       * msg cadastrada no
                                                       * arquivo de propriedades
                                                       */
                        throw ex;
                    }
                }
            }
        }

        if (ex.hasErrorMessages()) {
            throw ex;
        }

        if (acaoOrcamentaria.getId() == null) {
            acaoOrcamentariaRetorno = acaoOrcamentariaDAO.incluir(acaoOrcamentaria, usuarioLogado);
        } else {
            acaoOrcamentariaRetorno = acaoOrcamentariaDAO.alterar(acaoOrcamentaria, usuarioLogado);
        }
        return acaoOrcamentariaRetorno;
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe ação orçamentária com esse id cadastrado */

        AcaoOrcamentaria a = genericPersister.findByUniqueProperty(AcaoOrcamentaria.class, "id", id);
        if (a == null) {
            ex.addErrorMessage("MN007", id); // msg cadastrada no arquivo de
            // propriedades
            throw ex;
        }

        ProgramaPesquisaDto programaPesquisaDto = new ProgramaPesquisaDto();
        programaPesquisaDto.setAcaoOrcamentaria(a);
        if (programaService.buscarSemPaginacao(programaPesquisaDto).isEmpty()) {
            acaoOrcamentariaDAO.excluir(a);
        } else {
            ex.addErrorMessage("MSG005"); /*
                                           * msg cadastrada no arquivo de
                                           * propriedades
                                           */
            throw ex;
        }

    }

    public List<AcaoOrcamentaria> buscar(AcaoOrcamentaria acaoOrcamentaria) {
        return acaoOrcamentariaDAO.buscar(acaoOrcamentaria);
    }

    public List<AcaoOrcamentaria> buscar(AcaoOrcamentaria acaoOrcamentaria, Boolean emendasVinculadas, EmendaParlamentar emenda) {
        return acaoOrcamentariaDAO.buscar(acaoOrcamentaria, emendasVinculadas, emenda);
    }

    public List<AcaoOrcamentaria> buscarPaginado(AcaoOrcamentaria acaoOrcamentaria, Boolean emendasVinculadas, EmendaParlamentar emenda, int first, int size, EnumOrder order, String propertyOrder) {
        return acaoOrcamentariaDAO.buscarPaginado(acaoOrcamentaria, emendasVinculadas, emenda, first, size, order, propertyOrder);
    }

    public Long contarPaginado(AcaoOrcamentaria acaoOrcamentaria, Boolean emendasVinculadas) {
        return acaoOrcamentariaDAO.contarPaginado(acaoOrcamentaria, emendasVinculadas);
    }

    public List<EmendaParlamentar> buscarEmendaParlamentarUtilizada() {
        return acaoOrcamentariaDAO.buscarEmendaParlamentarUtilizada();
    }

    public List<AcaoOrcamentaria> buscarAcaoOrcamentariaUtilizada() {
        return acaoOrcamentariaDAO.buscarAcaoOrcamentariaUtilizada();
    }

    public List<EmendaParlamentar> buscarEmendas() {
        return genericPersister.findAll(EmendaParlamentar.class);
    }

}
