package com.company.methodsHandler;

import com.company.methods.Method;
import com.company.methods.MethodGet;

public class MethodGetHandler extends MethodsHandler {

    @Override
    public Method getObject() {
        return new MethodGet();
    }
}
