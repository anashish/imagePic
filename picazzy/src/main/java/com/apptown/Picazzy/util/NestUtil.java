package com.apptown.Picazzy.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by ${="Ashish"} on 15/1/17.
 */

public class NestUtil {

    public static Collection<Method> getMethods(Class<?> clazz) {
        Collection<Method> found = new ArrayList<Method>();
        while (clazz != null) {
            for (Method m1 : clazz.getDeclaredMethods()) {
                boolean overridden = false;

                for (Method m2 : found) {
                    if (m2.getName().equals(m1.getName())
                            && Arrays.deepEquals(m1.getParameterTypes(), m2
                            .getParameterTypes())) {
                        overridden = true;
                        break;
                    }
                }

                if (!overridden)
                    found.add(m1);
            }

            clazz = clazz.getSuperclass();
        }

        return found;
    }


    public static boolean getMatchMethod(String actual,String match){
        return Pattern.compile(Pattern.quote(match), Pattern.CASE_INSENSITIVE).matcher(actual).find();
    }

}
