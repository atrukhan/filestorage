package com.company.methodsHandler;

import com.company.methods.Method;
import com.company.methods.MethodMove;

public class MethodMoveHandler extends MethodsHandler{
    @Override
    public Method getObject() {
        return new MethodMove();
    }
}
