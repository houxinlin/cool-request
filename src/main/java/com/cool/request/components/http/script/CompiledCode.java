package com.cool.request.components.http.script;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class CompiledCode extends SimpleJavaFileObject {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final String className;

    public CompiledCode(String className) throws Exception {
        super(new URI(className), Kind.CLASS);
        this.className = className;
    }
    
    public String getClassName() {
		return className;
	}

    @Override
    public OutputStream openOutputStream() throws IOException {
        return baos;
    }
    public byte[] getByteCode() {
        return baos.toByteArray();
    }
}
