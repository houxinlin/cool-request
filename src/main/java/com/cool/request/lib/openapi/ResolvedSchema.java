package com.cool.request.lib.openapi;


import com.cool.request.lib.openapi.media.Schema;

import java.util.Map;

public class ResolvedSchema {
    public Schema schema;
    public Map<String, Schema> referencedSchemas;
}
