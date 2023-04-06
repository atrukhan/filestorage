package com.company.methodsHandler;

import com.company.methods.Method;
import com.company.methods.MethodPut;

public class MethodPutHandler extends MethodsHandler{
    @Override
    public Method getObject() {
        return new MethodPut();
    }
}
