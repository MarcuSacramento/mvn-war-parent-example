package br.gov.mj.side.web.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.TagBem;
import br.gov.mj.side.web.dao.BemDAO;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BemService {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private BemDAO bemDAO;

    @Inject
    private KitService kitService;

    @Inject
    private ProgramaService programaService;

    public List<Bem> buscarTodos() {
        return buscar(new Bem());
    }

    public List<Bem> buscar(Bem bem) {
        return bemDAO.buscar(bem);
    }

    public Bem buscarPeloId(Long id) {
        return bemDAO.buscarPeloId(id);
    }

    public Bem incluirAlterar(Bem bem) {

        Bem bemRetorno = null;

        if (bem == null) {
            throw new IllegalArgumentException("Parâmetro bem não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /*
         * Verificar se já existe bem com o mesmo nome cadastrado, pois nesse
         * caso não será permitida a inclusão (regra de negócio)
         */
        Bem b = genericPersister.findByUniqueProperty(Bem.class, "nomeBem", bem.getNomeBem());
        if (b != null && !b.equals(bem)) {
            ex.addErrorMessage("MN001", bem.getNomeBem()); // msg cadastrada no
            // arquivo de
            // propriedades
        }

        /*
         * Verificar se houve alteração no valor estimado ou na data da
         * estimativa, pois nesse caso será gravado o histórico da alteração
         */
        if (b != null && bem.getId() != null) {
            if ((!b.getDataEstimativa().equals(bem.getDataEstimativa())) || !(b.getValorEstimadoBem().equals(bem.getValorEstimadoBem()))) {
                if (bem.getDataEstimativa().isBefore(b.getDataEstimativa())) {
                    ex.addErrorMessage("MN033", b.getDataEstimativa().getMonthValue() + "/" + b.getDataEstimativa().getYear());
                }
            }
        }

        /*
         * Verificar se já existe atributos repetidos.Não será permitida a
         * inclusão/alteração (regra de negócio)
         */
        if (!bem.getTags().isEmpty()) {
            for (TagBem t : bem.getTags()) {
                int i = 0;
                for (TagBem tag : bem.getTags()) {
                    if (t.getNomeTag().equals(tag.getNomeTag())) {
                        i++;
                    }
                }
                if (i > 1) {
                    ex.addErrorMessage("MN005", t.getNomeTag()); // msg
                    // cadastrada
                    // no arquivo
                    // de
                    // propriedades
                }
            }
        }

        if (ex.hasErrorMessages()) {
            throw ex;
        }

        if (bem.getId() == null) {
            bemRetorno = bemDAO.incluir(bem);
        } else {
            bemRetorno = bemDAO.alterar(bem);
        }
        return bemRetorno;
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe bem com esse id cadastrado */

        Bem b = genericPersister.findByUniqueProperty(Bem.class, "id", id);
        if (b == null) {
            ex.addErrorMessage("MN003", id); // msg cadastrada no arquivo de
            // propriedades
            throw ex;
        }

        if (!kitService.pesquisar(null, b, null).isEmpty()) {
            ex.addErrorMessage("MSG005"); /*
                                           * msg cadastrada no arquivo de
                                           * propriedades
                                           */
            throw ex;
        }

        ProgramaPesquisaDto programaPesquisaDto = new ProgramaPesquisaDto();
        programaPesquisaDto.setBem(b);
        if (programaService.buscarSemPaginacao(programaPesquisaDto).size() > 0) {

            ex.addErrorMessage("MSG005"); /*
                                           * msg cadastrada no arquivo de
                                           * propriedades
                                           */
            throw ex;
        }

        bemDAO.excluir(b);

    }

    public List<Bem> buscarPaginado(Bem bem, int first, int size) {
        return bemDAO.buscarPaginado(bem, first, size);
    }

    public List<Bem> buscarPaginado(Bem bem, int first, int size, EnumOrder order, String propertyOrder) {
        return bemDAO.buscarPaginado(bem, first, size, order, propertyOrder);
    }

    public Long contarPaginado(Bem bem) {
        return bemDAO.contarPaginado(bem);
    }

}
