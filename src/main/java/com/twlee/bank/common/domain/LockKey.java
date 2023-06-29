package com.twlee.bank.common.domain;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public record LockKey(String value) {
    private static final String LOCK_PREFIX = "lock:";

    public static LockKey of(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return new LockKey(LOCK_PREFIX + parser.parseExpression(key).getValue(context, Object.class));
    }
}
