/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import hotpotato.util.*;

import java.io.*;
import java.util.*;

import org.apache.bcel.*;
import org.apache.bcel.classfile.*;

class ReferencedClassFinder {
    private Classes classes;

    ReferencedClassFinder() {
        classes = new Classes();
    }

    public Class[] find(Object obj) {
        Set references;
        try {
            references = findReferences(true, obj);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (Class[]) references.toArray(new Class[references.size()]);
    }

    Set findReferences(boolean ignoreHotpotatoClasses, Object obj)
        throws ClassNotFoundException, IOException {

        if (obj == null) {
            return new HashSet();
        }

        Set toDo = new HashSet();
        Class startingClass = obj.getClass();
        if (!shouldIgnore(new Classes().toResourceName(startingClass),
            ignoreHotpotatoClasses))
            toDo.add(startingClass);
        Set done = new HashSet();

        while (toDo.size() > 0) {
            Class aClass = getOne(toDo);
            JavaClass jClass = toJavaClass(aClass);
            ConstantPool pool = jClass.getConstantPool();
            for (int i = 0; i < pool.getLength(); i++) {
                Constant con = pool.getConstant(i);
                if (con instanceof ConstantClass) {
                    String refClass =
                        pool.getConstantString(i, Constants.CONSTANT_Class);
                    if (!shouldIgnore(refClass, ignoreHotpotatoClasses)) {
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
        return done;
    }

    private boolean shouldIgnore(
        String refClass,
        boolean ignoreHotpotatoClasses) {

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
