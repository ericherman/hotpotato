/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import hotpotato.util.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import org.apache.bcel.*;
import org.apache.bcel.classfile.*;

final class ReferencedClassFinder {
    private ClassUtil classes;
    boolean ignoreHotpotatoClasses;

    ReferencedClassFinder() {
        this(true);
    }

    ReferencedClassFinder(boolean ignoreHotpotatoClasses) {
        classes = new ClassUtil();
        this.ignoreHotpotatoClasses = ignoreHotpotatoClasses;
    }

    public Class[] find(Object obj) {
        Set references = new HashSet();
        List list = discoverObjectsForInspection(obj);
        try {
            for (int i = 0; i < list.size(); i++) {
                findReferences(references, list.get(i));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (Class[]) references.toArray(new Class[references.size()]);
    }

    List discoverObjectsForInspection(Object obj) {
        Set set = new HashSet();
        discoverFields(obj, set);
        return new ArrayList(set);
    }

    private void discoverFields(Object obj, Set set) {
        if (obj != null && !set.contains(obj)) {
            set.add(obj);
            Field[] fields = obj.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                try {
                    fields[i].setAccessible(true);
                    Object inner = fields[i].get(obj);
                    if (inner != null && !isJavaObject(inner))
                        discoverFields(inner, set);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private boolean isJavaObject(Object inner) {
        return classes.toResourceName(inner.getClass()).startsWith("java/");
    }

    void findReferences(Set done, Object obj)
        throws ClassNotFoundException, IOException {

        Set toDo = new HashSet();
        Class startingClass = obj.getClass();
        if (!shouldIgnore(classes.toResourceName(startingClass))) {
            toDo.add(startingClass);
        }
        while (toDo.size() > 0) {
            Class aClass = getOne(toDo);
            JavaClass jClass = toJavaClass(aClass);
            ConstantPool pool = jClass.getConstantPool();
            for (int i = 0; i < pool.getLength(); i++) {
                Constant con = pool.getConstant(i);
                if (con instanceof ConstantClass) {
                    String refClass =
                        pool.getConstantString(i, Constants.CONSTANT_Class);
                    if (!shouldIgnore(refClass)) {
                        ClassLoader cl = classes.classLoaderFor(aClass);
                        Class nextClass =
                            cl.loadClass(classes.toClassName(refClass));
                        if (!done.contains(nextClass)) {
                            toDo.add(nextClass);
                        }
                    }
                }
            }
            done.add(aClass);
            toDo.remove(aClass);
        }
    }

    private boolean shouldIgnore(String refClass) {
        return refClass.startsWith("java/")
            || refClass.startsWith("[")
            || (ignoreHotpotatoClasses && refClass.startsWith("hotpotato/"));
    }

    private Class getOne(Set set) {
        return (Class) set.iterator().next();
    }

    private JavaClass toJavaClass(Class aClass) throws IOException {
        String classResourceName = classes.toResourceName(aClass);
        ClassLoader cl = classes.classLoaderFor(aClass);
        InputStream str = cl.getResourceAsStream(classResourceName);
        ClassParser parser = new ClassParser(str, classResourceName);
        return parser.parse();
    }

}
