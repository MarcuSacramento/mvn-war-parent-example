package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.apoio.entidades.Elemento;
import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.Kit;
import br.gov.mj.side.web.dao.KitDAO;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class KitService {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private ProgramaService programaService;

    @Inject
    private KitDAO kitDAO;

    public List<Kit> buscarPaginado(Kit kit, Bem bem, Elemento elemento, int first, int size, String coluna, int order) {
        return kitDAO.buscarPaginado(kit, bem, elemento, first, size, coluna, order);
    }

    public List<Kit> buscar(Kit kit) {
        return kitDAO.buscar(kit);
    }

    public Kit buscarPeloId(Long id) {
        return kitDAO.buscarPeloId(id);
    }

    public List<Kit> pesquisar(Kit kit, Bem bem, Elemento elemento) {
        return kitDAO.pesquisar(kit, bem, elemento);
    }

    public Kit incluirAlterar(Kit kit) {

        Kit kitRetorno = null;

        if (kit == null) {
            throw new IllegalArgumentException("Parâmetro bem não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /*
         * Verificar se já existe kit com o mesmo nome cadastrado, pois nesse
         * caso não será permitida a inclusão (regra de negócio)
         */
        Kit k = genericPersister.findByUniqueProperty(Kit.class, "nomeKit", kit.getNomeKit());
        if (k != null && !k.equals(kit)) {
            ex.addErrorMessage("MN002", kit.getNomeKit()); // msg cadastrada no
            // arquivo de
            // propriedades
        }

        if (ex.hasErrorMessages()) {
            throw ex;
        }

        if (kit.getId() == null) {
            kitRetorno = kitDAO.incluir(kit);
        } else {
            kitRetorno = kitDAO.alterar(kit);
        }
        return kitRetorno;
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe kit com esse id cadastrado */

        Kit k = genericPersister.findByUniqueProperty(Kit.class, "id", id);
        if (k == null) {
            ex.addErrorMessage("MN004", id); // msg cadastrada no arquivo de
            // propriedades
            throw ex;
        }

        ProgramaPesquisaDto programaPesquisaDto = new ProgramaPesquisaDto();
        programaPesquisaDto.setKit(k);
        if (programaService.buscarSemPaginacao(programaPesquisaDto).isEmpty()) {
            kitDAO.excluir(k);
        } else {
            ex.addErrorMessage("MSG005"); /*
                                           * msg cadastrada no arquivo de
                                           * propriedades
                                           */
            throw ex;
        }

    }

}
