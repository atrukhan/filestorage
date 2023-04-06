package com.company.methodsHandler;

import com.company.methods.Method;
import com.company.methods.MethodDelete;

public class MethodDeleteHandler extends MethodsHandler{
    @Override
    public Method getObject() {
        return new MethodDelete();
    }
}
