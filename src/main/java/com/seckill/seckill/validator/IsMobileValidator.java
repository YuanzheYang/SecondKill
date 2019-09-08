package com.seckill.seckill.validator;

import com.seckill.seckill.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    public boolean required = false;
    public void initialize(IsMobile constraintAnnotation) {
        required =  constraintAnnotation.required();
    }
    public boolean isValid(String value, ConstraintValidatorContext context){
        if(required)  {
            return ValidatorUtil.isMobile(value);
        }else{
            if(StringUtils.isEmpty(value)) {
                return true;
            }else{
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
