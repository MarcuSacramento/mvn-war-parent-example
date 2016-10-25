package br.gov.mj.side.web.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mj.infra.negocio.exception.BusinessException;
import br.gov.mj.infra.negocio.persistencia.EnumOrder;
import br.gov.mj.infra.negocio.persistencia.IGenericPersister;
import br.gov.mj.side.entidades.Bem;
import br.gov.mj.side.entidades.enums.EnumStatusPrograma;
import br.gov.mj.side.entidades.programa.Programa;
import br.gov.mj.side.entidades.programa.ProgramaBem;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAcompanhamento;
import br.gov.mj.side.entidades.programa.ProgramaCriterioAvaliacao;
import br.gov.mj.side.entidades.programa.ProgramaCriterioElegibilidade;
import br.gov.mj.side.entidades.programa.ProgramaHistoricoPublicizacao;
import br.gov.mj.side.entidades.programa.ProgramaKit;
import br.gov.mj.side.entidades.programa.ProgramaPotencialBeneficiarioUf;
import br.gov.mj.side.entidades.programa.ProgramaRecursoFinanceiro;
import br.gov.mj.side.entidades.programa.licitacao.AgrupamentoLicitacao;
import br.gov.mj.side.web.dao.ProgramaDAO;
import br.gov.mj.side.web.dto.ProgramaComboDto;
import br.gov.mj.side.web.dto.ProgramaPesquisaDto;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProgramaService {

    @Inject
    private IGenericPersister genericPersister;

    @Inject
    private ProgramaDAO programaDAO;

    public List<ProgramaComboDto> buscarProgramas() {
        return programaDAO.buscarProgramas();
    }

    public List<AgrupamentoLicitacao> buscarProgramaAgrupamentoLicitacao(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return programaDAO.buscarProgramaAgrupamentoLicitacao(programa.getId());
    }

    public List<ProgramaCriterioElegibilidade> buscarProgramaCriterioElegibilidade(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return programaDAO.buscarProgramaCriterioElegibilidade(programa.getId());
    }

    public List<ProgramaCriterioAcompanhamento> buscarProgramaCriterioAcompanhamento(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return programaDAO.buscarProgramaCriterioAcompanhamento(programa.getId());
    }

    public List<ProgramaKit> buscarProgramakit(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return programaDAO.buscarProgramakit(programa.getId());
    }

    public List<ProgramaBem> buscarProgramaBem(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return programaDAO.buscarProgramaBem(programa.getId());
    }

    public List<ProgramaPotencialBeneficiarioUf> buscarProgramaPotencialBeneficiarioUf(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return programaDAO.buscarProgramaPotencialBeneficiarioUf(programa.getId());
    }

    public List<ProgramaHistoricoPublicizacao> buscarHistoricoPublicizacao(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return programaDAO.buscarHistoricoPublicizacao(programa.getId());
    }

    public List<ProgramaCriterioAvaliacao> buscarProgramaCriterioAvaliacao(Programa programa) {
        if (programa == null || programa.getId() == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }
        return programaDAO.buscarProgramaCriterioAvaliacao(programa.getId());
    }

    public List<Programa> buscarPaginado(ProgramaPesquisaDto programaPesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {
        return programaDAO.buscarPaginado(programaPesquisaDto, first, size, order, propertyOrder);
    }

    public List<Programa> buscarPublicados(ProgramaPesquisaDto programaPesquisaDto, int first, int size, EnumOrder order, String propertyOrder) {
        return programaDAO.buscarPaginado(programaPesquisaDto, first, size, order, propertyOrder);
    }

    public List<Programa> buscarPublicadosSemPaginacao(ProgramaPesquisaDto programaPesquisaDto) {
        return programaDAO.buscarSemPaginacao(programaPesquisaDto);
    }

    public List<Programa> buscarSemPaginacao(ProgramaPesquisaDto programaPesquisaDto) {
        return programaDAO.buscarSemPaginacao(programaPesquisaDto);
    }

    public List<Programa> buscarSemPaginacaoOrdenado(ProgramaPesquisaDto programaPesquisaDto, EnumOrder order, String propertyOrder) {
        return programaDAO.buscarSemPaginacaoOrdenado(programaPesquisaDto, order, propertyOrder);
    }

    public List<Programa> buscar(Programa programa) {
        return programaDAO.buscar(programa);
    }

    public Long contarPaginado(ProgramaPesquisaDto programaPesquisaDto) {
        return programaDAO.contarPaginado(programaPesquisaDto);
    }

    public Programa buscarPeloId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Programa com esse id cadastrado */

        Programa p = programaDAO.buscarPeloId(id);

        if (p == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN008", id);
            throw ex;
        }
        return p;
    }

    public Programa incluirAlterar(Programa programa, String usuarioLogado) {

        Programa programaRetorno = null;

        validarObjetoNulo(programa);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        validarProgramaComMesmoNome(programa, ex);
        validarPotenciaisBeneficiarios(programa, ex);
        validarFinalizadoComListasVazias(programa, ex);
        validarListaBensOuKitsComQuantidadesVazias(programa, ex);

        if (ex.hasErrorMessages()) {
            throw ex;
        }

        if (programa.getId() == null) {
            programaRetorno = programaDAO.incluir(programa, usuarioLogado);
        } else {
            programaRetorno = programaDAO.alterar(programa, usuarioLogado);
        }
        return programaRetorno;
    }

    /*
     * Se for necessário atualizar somente informações basicas relacionadas ao
     * programa e não for necessário buscar as varias listas internas do
     * Programa.
     */
    public Programa atualizarInformacoesBasicasPrograma(Programa programa, String usuarioLogado) {

        validarObjetoNulo(programa);

        BusinessException ex = new BusinessException();

        if (StringUtils.isBlank(usuarioLogado)) {
            ex.addErrorMessage("Usuario logado obrigatório");
            throw ex;
        }

        validarProgramaComMesmoNome(programa, ex);

        if (ex.hasErrorMessages()) {
            throw ex;
        }
        return programaDAO.atualizarInformacoesBasicasPrograma(programa, usuarioLogado);
    }

    private void validarProgramaComMesmoNome(Programa programa, BusinessException ex) {
        /*
         * Verificar se já existe programa com o mesmo nome cadastrado, pois
         * nesse caso não será permitida a inclusão (regra de negócio)
         */
        Programa p = genericPersister.findByUniqueProperty(Programa.class, "nomePrograma", programa.getNomePrograma());
        if (p != null && !p.equals(programa)) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN009", programa.getNomePrograma());
        }
    }

    private void validarObjetoNulo(Programa programa) {
        if (programa == null) {
            throw new IllegalArgumentException("Parâmetro programa não pode ser null");
        }
    }

    private void validarPotenciaisBeneficiarios(Programa programa, BusinessException ex) {
        /*
         * verifica se a limitacao geografica está true e se existe uf
         * selecionada
         */
        if (!programa.getPossuiLimitacaoGeografica()) {
            programa.getPotenciaisBeneficiariosUf().clear();
            programa.setPossuiLimitacaoMunicipalEspecifica(false);
        } else {
            if (programa.getPotenciaisBeneficiariosUf().isEmpty()) {
                /* msg cadastrada no arquivo de propriedades */
                ex.addErrorMessage("MN010");
            } else {
                if (!programa.getPossuiLimitacaoMunicipalEspecifica()) {
                    /* limpa municipios */
                    limpaMunicipiosDaListaDeUfs(programa);
                } else {
                    if (!existeMunicipio(programa.getPotenciaisBeneficiariosUf())) {
                        /* msg cadastrada no arquivo de propriedades */
                        ex.addErrorMessage("MN011");
                    }
                }

            }
        }
    }

    private void validarFinalizadoComListasVazias(Programa programa, BusinessException ex) {
        /*
         * verifica se caso o programa está com a situação de finalizado, ele
         * deve possuir pelo menos um tiem em recurso financeiro, pelo menos um
         * item em lista de bem e kit, pelo menos um item em criterio de
         * elegibilidade e pelo menos um item em critério de acompanhamento
         */

        if (programa.getStatusPrograma() != null && (programa.getStatusPrograma().equals(EnumStatusPrograma.FORMULADO) || programa.getStatusPrograma().equals(EnumStatusPrograma.PUBLICADO))) {
            if (programa.getRecursosFinanceiros().isEmpty()) {
                /* msg cadastrada no arquivo de propriedades */
                ex.addErrorMessage("MN012");
            }

            if (programa.getProgramaBens().isEmpty() && programa.getProgramaKits().isEmpty()) {
                /* msg cadastrada no arquivo de propriedades */
                ex.addErrorMessage("MN013");
            }

            if (programa.getCriteriosElegibilidade().isEmpty()) {
                /* msg cadastrada no arquivo de propriedades */
                ex.addErrorMessage("MN014");
            }

            if (programa.getCriteriosAcompanhamento().isEmpty()) {
                /* msg cadastrada no arquivo de propriedades */
                ex.addErrorMessage("MN015");
            }

            if (programa.getCriteriosAvaliacao().isEmpty()) {
                /* msg cadastrada no arquivo de propriedades */
                ex.addErrorMessage("MN043");
            }

        }

    }

    private void validarListaBensOuKitsComQuantidadesVazias(Programa programa, BusinessException ex) {
        /*
         * verifica se caso exista um bem ou kit selecionado em suas respectivas
         * listas, a quantidade não pode ser vazia ou zero
         */
        if (possuiProgramaKitsComQuantidadeVazia(programa) || possuiProgramaBensComQuantidadeVazia(programa)) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN016");
        }

    }

    private boolean possuiProgramaKitsComQuantidadeVazia(Programa programa) {
        if (!programa.getProgramaKits().isEmpty()) {
            for (ProgramaKit programaKit : programa.getProgramaKits()) {
                if ((programaKit.getQuantidade() == null || programaKit.getQuantidade() <= 0)) {
                    return true;

                }
            }

        }

        return false;
    }

    private boolean possuiProgramaBensComQuantidadeVazia(Programa programa) {
        if (!programa.getProgramaBens().isEmpty()) {
            for (ProgramaBem programaBem : programa.getProgramaBens()) {
                if ((programaBem.getQuantidade() == null || programaBem.getQuantidade() <= 0)) {
                    return true;
                }
            }

        }

        return false;
    }

    public void excluir(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Parâmetro id não pode ser null");
        }

        BusinessException ex = new BusinessException();

        /* Verificar se existe Programa com esse id cadastrado */

        Programa p = genericPersister.findByUniqueProperty(Programa.class, "id", id);
        if (p == null) {
            /* msg cadastrada no arquivo de propriedades */
            ex.addErrorMessage("MN008", id);
            throw ex;
        }

        programaDAO.excluir(p);

    }

    private void limpaMunicipiosDaListaDeUfs(Programa programa) {
        List<ProgramaPotencialBeneficiarioUf> listaNova = new ArrayList<ProgramaPotencialBeneficiarioUf>();
        for (ProgramaPotencialBeneficiarioUf programaPotencialBeneficiarioUf : programa.getPotenciaisBeneficiariosUf()) {
            programaPotencialBeneficiarioUf.getPotencialBeneficiarioMunicipios().clear();
            listaNova.add(programaPotencialBeneficiarioUf);
        }
        programa.setPotenciaisBeneficiariosUf(listaNova);
    }

    private boolean existeMunicipio(List<ProgramaPotencialBeneficiarioUf> ufs) {
        for (ProgramaPotencialBeneficiarioUf uf : ufs) {
            if (!uf.getPotencialBeneficiarioMunicipios().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<ProgramaRecursoFinanceiro> buscarProgramaRecursoFinanceiroPeloIdPrograma(Programa programa) {
        return programaDAO.buscarProgramaRecursoFinanceiroPeloIdPrograma(programa.getId());
    }

    public List<Programa> buscarProgramaPorStatus(EnumStatusPrograma statusPrograma) {
        return programaDAO.buscarPorStatus(statusPrograma);
    }
    
    public List<Bem> buscarTodosOsBensDoPrograma(Programa programa){
        
        if (programa == null) {
            throw new IllegalArgumentException("Parâmetro programa não pode ser null");
        }        
        return programaDAO.buscarTodosOsBensDoPrograma(programa);
    }
}
