package com.doktorthe2nd.nyax.luaj;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LuaProxyFactory {

    /**
     * Создаёт прокси для заданного интерфейса.
     *
     * @param interfaceClass класс интерфейса (например, java.awt.event.ActionListener.class)
     * @param luaObject      Lua-объект (таблица с функциями или одна функция)
     * @return прокси-объект
     */
    public static Object createProxy(Class<?> interfaceClass, LuaValue luaObject) {
        // Проверяем, что передан именно интерфейс
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("Аргумент должен быть интерфейсом");
        }

        // Создаём обработчик вызовов
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // Получаем имя метода
                String methodName = method.getName();

                // Ищем соответствующую Lua-функцию
                LuaValue luaFunction = null;
                if (luaObject.istable()) {
                    luaFunction = luaObject.get(methodName);
                } else if (luaObject.isfunction()) {
                    // Если передан просто функция, используем её для всех методов
                    luaFunction = luaObject;
                }

                // Если функция не найдена, пробуем найти метод с суффиксом (опционально)
                if (luaFunction == null || luaFunction.isnil()) {
                    // Можно вернуть значение по умолчанию или бросить исключение
                    return defaultValue(method.getReturnType());
                }

                // Конвертируем аргументы Java -> Lua
                LuaValue[] luaArgs = new LuaValue[args == null ? 0 : args.length];
                for (int i = 0; i < luaArgs.length; i++) {
                    luaArgs[i] = CoerceJavaToLua.coerce(args[i]);
                }

                // Вызываем Lua-функцию
                LuaValue result = luaFunction.invoke(luaArgs).arg1();

                // Конвертируем результат Lua -> Java
                return CoerceLuaToJava.coerce(result, method.getReturnType());
            }
        };

        // Создаём прокси
        return Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[] { interfaceClass },
                handler
        );
    }

    // Возвращает значение по умолчанию для примитивных типов
    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        return null;
    }
}