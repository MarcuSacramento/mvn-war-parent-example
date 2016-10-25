package br.gov.mj.side.web.view.components.validators;

import org.apache.wicket.validation.validator.PatternValidator;

import br.gov.mj.side.web.util.Constants;

public class AlphaNumericValidator extends PatternValidator {

    private static final long serialVersionUID = 1L;

    public AlphaNumericValidator() {
        super(Constants.REGEX_ALFANUMERICO);
    }

    public AlphaNumericValidator(String pattern) {
        super(pattern);
    }

}
