package com.bmc.gibraltar.automation.items;

import org.apache.log4j.Logger;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface CommonHandlers {
    Logger logger = Logger.getLogger(CommonHandlers.class);
    List<String> list = new ArrayList<>();

    // JSON Handlers START

    /**
     * Safety method! If element present in the array, the method removes it. Othervice returns the same array.
     *
     * @param element the element, is must be removed.
     * @param arr     The array, where it should be find the element
     * @param <T>     type of passed params.
     * @return Array copy without 1 element (if is it present there)
     */
    static <T> T[] remove(T element, T... arr) {
        try {
            T[] newArr = Arrays.copyOf(arr, arr.length - 1);
            int i = 0;
            for (T elem : arr) {
                if (elem != element)
                    newArr[i++] = elem;
            }
            return newArr;
        } catch (Exception e) {
        }
        return arr;
    }

    /**
     * Safety method! Verify, is contains element into the arr (array)
     *
     * @param element is must be verified on presents in the array.
     * @param arr     The array, where it should be find the element
     * @param <T>     type of passed params.
     * @return if element present -> true
     */
    static <T> boolean contains(T element, T... arr) {
        List<T> list = Arrays.asList(arr);
        if (list.contains(element))
            return true;
        return false;
    }

    static String getRegex(String expression, String pattern, int group) {
        String result = expression;
        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(expression);
            result = m.find() ? m.group(group) : expression;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    // JSON Handlers FINISH

    /**
     * Easy to use! Just implement the Interface, call this method and put key and JSON.
     * Example parsJsonAndGetByKey("Name", json) - returns all values, wich has tag "Name"
     *
     * @param key  tag from JSON. JSON = { Name: "Jonee"}, key = "Name".
     * @param json just eny JSON (String).
     * @return Example parsJsonAndGetByKey("Name",  "{ Name: "Jonee"}")  returns "Jonee".
     */
    default List<String> parsJsonAndGetByKey(String key, String json) {
        list.clear();
        Map<String, String> mainJSONMap = jsonParser(json);
        getByKey(key, mainJSONMap);
        return list;
    }

    /**
     * Please don`t use this. It is only for inner using.
     *
     * @param key
     * @param collection
     */
    default void getByKey(String key, Object collection) {
        Map<Object, Object> map;
        if (collection instanceof LinkedHashMap && ((LinkedHashMap) collection).size() != 0) {
            map = (LinkedHashMap) collection;
            for (Object obj : map.keySet()) {
                if (key.equals("" + obj)) {
                    list.add("" + map.get(obj));
                }
                getByKey(key, map.get(obj));
            }
        }
        if (collection instanceof LinkedList && ((LinkedList) collection).size() != 0) {
            for (Object elem : (LinkedList) collection) {
                getByKey(key, elem);
            }
        }
    }

    /**
     * Please don`t use this. It is only for inner using.
     *
     * @param json
     * @return
     */
    default Map jsonParser(String json) {
        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory() {
            public List creatArrayContainer() {
                return new LinkedList();
            }

            public Map createObjectContainer() {
                return new LinkedHashMap();
            }
        };

        try {
            Map jsonMap = (Map) parser.parse(json, containerFactory);
            Iterator iter = jsonMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
            }
            return jsonMap;
        } catch (ParseException pe) {
            System.out.println("There is no any JSON. Error:" + pe);
        }
        return null;
    }

    /**
     * Compare real list of some text names with the same ordered.
     *
     * @param listForVerifying list, should by verify on alphabetical order
     * @return true, if list is sorted alphabetical, else returns false
     */
    default boolean isAlphabetical(List<String> listForVerifying) {
        for (int i = 0; i < listForVerifying.size() - 1; i++) {
            if (listForVerifying.get(i).compareToIgnoreCase(listForVerifying.get(i + 1)) > 0) {
                logger.warn("\n Order is NOT alphabetical:" + listForVerifying);
                return false;
            }
        }
        return true;
    }

    /**
     * Intefaces  fo Lambda Functions.
     * Naming Conventions:
     * Always starts with Func
     * If passed params more than 1, next Two or Three (2 and 3 params respectively)
     * If returns void -> Void (FuncVoid),
     * if primitive -> primitive name (int -> Int  , example FuncInt),
     * if type -> nothing add (FuncThree<T, U, R>  T apply(U u, R r).
     *
     * @param <T> type of passed param.
     * @param <U> type of passed param.
     * @param <R> type of passed param.
     */
    @FunctionalInterface
    interface FuncThreeVoid<T, U, R> {
        @Step
        void that(T t, U u, R r);
    }

    @FunctionalInterface
    interface FuncTwoVoid<T, U> {
        @Step
        void that(T t, U u);
    }

    @FunctionalInterface
    interface FuncVoid<T> {
        @Step
        void test(T t);
    }

    @FunctionalInterface
    interface FuncInt<T> {
        @Step
        int test(T t);
    }

    @FunctionalInterface
    interface FuncThree<T, U, R> {
        @Step
        T apply(U u, R r);
    }
}
