package com.company.methodsHandler;

import com.company.methods.Method;
import com.company.methods.MethodKey;

public class MethodKeyHandler extends MethodsHandler{
    @Override
    public Method getObject() {
        return new MethodKey();
    }
}
