package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.authorityvalidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AuthorityValidator implements ConstraintValidator<Authority, Integer> {

    private int min;
    private int max;

    @Override
    public void initialize(Authority autority) {
        this.min = autority.min();
        this.max = autority.max();
    }

    @Override
    public boolean isValid(Integer authority, ConstraintValidatorContext context) {
        return authority >= min && authority <= max;
    }

}
