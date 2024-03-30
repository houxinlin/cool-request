package com.cool.request.lib.openapi;

import com.cool.request.lib.openapi.media.Encoding;
import com.cool.request.lib.openapi.media.EncodingProperty;
import com.cool.request.lib.openapi.media.Schema;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class DeserializationModule31 extends SimpleModule {

    public DeserializationModule31() {

        this.addDeserializer(Schema.class, new Model31Deserializer());
        this.addDeserializer(Parameter.class, new Parameter31Deserializer());
        this.addDeserializer(Header.StyleEnum.class, new HeaderStyleEnumDeserializer());
        this.addDeserializer(Encoding.StyleEnum.class, new EncodingStyleEnumDeserializer());
        this.addDeserializer(EncodingProperty.StyleEnum.class, new EncodingPropertyStyleEnumDeserializer());

        this.addDeserializer(SecurityScheme.class, new SecurityScheme31Deserializer());

        this.addDeserializer(ApiResponses.class, new ApiResponses31Deserializer());
        this.addDeserializer(Paths.class, new Paths31Deserializer());
        this.addDeserializer(Callback.class, new Callback31Deserializer());

        this.setDeserializerModifier(new BeanDeserializerModifier()
        {
            @Override public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                if (beanDesc.getBeanClass() == OpenAPI.class) {
                    return new OpenAPI31Deserializer(deserializer);
                }
                return deserializer;
            }
        });
    }
}
