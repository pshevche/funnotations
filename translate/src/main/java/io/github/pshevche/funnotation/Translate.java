package io.github.pshevche.funnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated type should be translated into the language of choice (see {@link Language}).
 * </p>
 * <ul>
 *     <li>When applied to a class, a delegate class will be created where all method and parameter names will be translated.</li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Translate {

    Language[] value();

}
