package com.cool.request.plugin.apipost;

import com.google.gson.annotations.SerializedName;


public class ParameterDTO {
    @SerializedName("is_checked")
    private Integer isChecked;
    @SerializedName("key")
    private String key;
    @SerializedName("type")
    private String type;
    @SerializedName("not_null")
    private String notNull;
    @SerializedName("field_type")
    private String fieldType;
    @SerializedName("value")
    private String value;

    public Integer getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Integer isChecked) {
        this.isChecked = isChecked;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotNull() {
        return notNull;
    }

    public void setNotNull(String notNull) {
        this.notNull = notNull;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static final class ParameterDTOBuilder {
        private Integer isChecked;
        private String key;
        private String type;
        private String notNull;
        private String fieldType;
        private String value;

        private ParameterDTOBuilder() {
        }

        public static ParameterDTOBuilder aParameterDTO() {
            return new ParameterDTOBuilder();
        }

        public ParameterDTOBuilder withIsChecked(Integer isChecked) {
            this.isChecked = isChecked;
            return this;
        }

        public ParameterDTOBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public ParameterDTOBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public ParameterDTOBuilder withNotNull(String notNull) {
            this.notNull = notNull;
            return this;
        }

        public ParameterDTOBuilder withFieldType(String fieldType) {
            this.fieldType = fieldType;
            return this;
        }

        public ParameterDTOBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public ParameterDTO build() {
            ParameterDTO parameterDTO = new ParameterDTO();
            parameterDTO.setIsChecked(isChecked);
            parameterDTO.setKey(key);
            parameterDTO.setType(type);
            parameterDTO.setNotNull(notNull);
            parameterDTO.setFieldType(fieldType);
            parameterDTO.setValue(value);
            return parameterDTO;
        }
    }
}
