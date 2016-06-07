package com.bmc.gibraltar.automation.items.component;

public enum CheckboxExpressions {

    AT_ALL_TIMES("At all times"),
    WHEN_CONDITION_IS_TRUE("When condition is true");

    private String expression;

    CheckboxExpressions(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
