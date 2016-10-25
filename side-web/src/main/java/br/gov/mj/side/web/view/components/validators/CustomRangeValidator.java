package br.gov.mj.side.web.view.components.validators;

import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.RangeValidator;

public class CustomRangeValidator extends RangeValidator<BigDecimal> {
    private static final long serialVersionUID = 1L;

    public CustomRangeValidator(BigDecimal minimum, BigDecimal maximum) {
        super(minimum, maximum);
    }

    @Override
    public void validate(IValidatable<BigDecimal> validatable) {
        boolean skip = RequestCycle.get().find(AjaxRequestHandler.class) != null;
        if (skip) {
            return;
        }
        super.validate(validatable);
    }

}
