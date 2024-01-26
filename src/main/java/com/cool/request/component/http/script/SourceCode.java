package com.cool.request.component.http.script;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;
public class SourceCode extends SimpleJavaFileObject {
	private String contents = null;
	private final String className;
	public SourceCode(String className, String contents) throws Exception {
		super(URI.create("string:///" + className.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.contents = contents;
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		return contents;
	}
}
