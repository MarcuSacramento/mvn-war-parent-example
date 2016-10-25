package br.gov.mj.side.web.view.components.validators;

import org.apache.wicket.validation.validator.PatternValidator;

import br.gov.mj.side.web.util.Constants;

public class NumericValidator extends PatternValidator {

    private static final long serialVersionUID = 1L;

    public NumericValidator(String pattern) {
        super(pattern);
    }

    public NumericValidator() {
        super(Constants.REGEX_NUMEROS);
    }

}
