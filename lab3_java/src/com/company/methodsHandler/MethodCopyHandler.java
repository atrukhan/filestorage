package com.company.methodsHandler;

import com.company.methods.Method;
import com.company.methods.MethodCopy;

public class MethodCopyHandler extends MethodsHandler{
    @Override
    public Method getObject() {
        return new MethodCopy();
    }
}
