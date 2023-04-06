package com.company.methodsHandler;

import com.company.methods.Method;
import com.company.methods.MethodPost;

public class MethodPostHandler extends MethodsHandler{
    @Override
    public Method getObject() {
        return new MethodPost();
    }
}
