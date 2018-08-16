/*
 * Copyright (C) 2018 lbtrace(coder.wlb@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lbtrace.ashmemservice;

import android.support.annotation.NonNull;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * Reflect Utils
 */
public class ReflectUtil {
    /**
     * Reflect get special constructor of the class
     *
     * @param clazz     the class object
     * @param paramType the list of parameters
     * @return the special constructor
     */
    public static Constructor<?> getConstructor(@NonNull Class<?> clazz, Class<?>... paramType)
            throws NoSuchMethodException {
        Constructor<?> constructor = null;

        try {
            constructor = clazz.getDeclaredConstructor(paramType);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException(clazz.toString() + " no constructor with params " +
                    paramType.toString());
        }


        return constructor;
    }

    /**
     * Reflect get special method(public private or default) of the class
     *
     * @param clazz     the class object
     * @param name      the name of special method
     * @param paramType the list of parameters in special method
     * @return method object
     */
    public static Method getMethod(@NonNull Class<?> clazz, String name, Class<?>... paramType)
            throws NoSuchMethodException {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method m = clazz.getDeclaredMethod(name, paramType);

                if (!m.isAccessible()) {
                    m.setAccessible(true);
                }

                return m;
            } catch (NoSuchMethodException e) {
            }
        }

        throw new NoSuchMethodException(clazz.toString() +
                " no method: " + name);

    }

    /**
     * Reflect get special field(public private or default) of the class
     *
     * @param clazz the class object
     * @param name  the name of special field
     * @return field object
     */
    public static Field getField(@NonNull Class<?> clazz, String name)
            throws NoSuchFieldException {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field f = clazz.getDeclaredField(name);

                if (!f.isAccessible())
                    f.setAccessible(true);

                return f;
            } catch (NoSuchFieldException e) {
            }
        }

        throw new NoSuchFieldException(clazz.toString() +
                " no field: " + name);
    }

    /**
     * Reflect invoke special method(public private or default) of the object
     *
     * @param obj    the target object
     * @param method the special method
     * @param params the list of params
     * @return the result of dispatching method
     */
    public static Object invokeMethod(@NonNull Object obj, @NonNull Method method, Object... params)
            throws IllegalAccessException, InvocationTargetException {
        Object ret = null;

        if (!method.isAccessible())
            method.setAccessible(true);

        try {
            ret = method.invoke(obj, params);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException("Method " + method.getName() + " IllegalAccess");
        } catch (InvocationTargetException e) {
            throw new InvocationTargetException(e);
        }

        return ret;
    }

    /**
     * Replace original array field with a new array containing the original array plus
     * the extra array
     *
     * @param instance      the instance whose field is to be modified
     * @param fieldName     the field name
     * @param extraElements the extra array
     */
    public static void expandFieldArray(@NonNull Object instance, @NonNull String fieldName,
                                        Object[] extraElements) throws Exception {
        Field field = ReflectUtil.getField(instance.getClass(), fieldName);
        Object[] original = (Object[]) field.get(instance);
        Object[] combined = (Object[]) Array.newInstance(original.getClass().getComponentType(),
                original.length + extraElements.length);

        System.arraycopy(extraElements, 0, combined, 0, extraElements.length);
        System.arraycopy(original, 0, combined, extraElements.length, original.length);
        field.set(instance, combined);
    }
}
