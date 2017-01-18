package com.hpe.sb.mobile.app.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.Reader;
import java.lang.reflect.Type;

public class JsonTranslation {

    private static Gson gson = new GsonBuilder().create();
    private static Gson gsonWithNulls = new GsonBuilder().serializeNulls().create();

    private JsonTranslation() {
        throw new AssertionError();
    }

    public static String object2JsonStringWithNulls(Object object) {
        return gsonWithNulls.toJson(object);
    }

    public static <T> T jsonString2ObjectWithNulls(Reader reader, Type type) {
        return gsonWithNulls.fromJson(reader, type);
    }

    public static <T> T jsonString2ObjectWithNulls(String content, Class<T> clazz) {
        return gsonWithNulls.fromJson(content, clazz);
    }

    public static <T> T jsonString2ObjectWithNulls(String content, Type type) {
        return gsonWithNulls.fromJson(content, type);
    }

    public static JsonElement object2JsonElement(Object object) {
        return gson.toJsonTree(object);
    }

    public static String object2JsonString(Object object) {
        return gson.toJson(object);
    }

    public static <T> T jsonString2Object(String content, Class<T> clazz) {
        return gson.fromJson(content, clazz);
    }

    public static <T> T jsonString2Object(JsonElement content, Class<T> clazz) {
        return gson.fromJson(content, clazz);
    }

    public static <T> T jsonString2Object(Reader reader, Class<T> clazz) {
        return gson.fromJson(reader, clazz);
    }

    public static String object2JsonString(Object object, Type type) {
        return gson.toJson(object, type);
    }

    public static <T> T jsonString2Object(String content, Type type) {
        return gson.fromJson(content, type);
    }

    public static <T> T jsonString2Object(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }

    public static <T> T jsonElement2Object(JsonElement jsonElement, Class<T> clazz) {
        return gson.fromJson(jsonElement, clazz);
    }
}
