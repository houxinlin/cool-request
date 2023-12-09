package com.hxl.plugin.springboot.invoke.script;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.spi.*;
import java.math.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.text.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.*;
import java.util.random.*;
import java.util.zip.*;
import java.util.*;
import java.time.*;
import java.time.chrono.*;
import java.time.format.*;
import java.time.temporal.*;
import java.lang.reflect.*;
import java.lang.invoke.*;
import java.lang.runtime.*;
public class ResponseApi extends Utils {
    private Response response;

    public ResponseApi(ILog iLog,Response response) {
        super(iLog);
        this.response = response;
    }

    public void handlerResponse() {
        ${body}
    }
    private void save(String path){
        saveResponse(path,this.response);
    }
    private void saveHeader(String path){
        writeFile(path,this.response.getHeaderAsString());
    }
    private void saveBody(String path){
        saveResponseBody(path,this.response);
    }
}