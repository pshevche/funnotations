package io.github.pshevche.funnotation;

import com.google.auto.service.AutoService;
import io.github.pshevche.funnotation.internal.DeepLApiKey;
import io.github.pshevche.funnotation.internal.DeepLTranslationService;
import io.github.pshevche.funnotation.internal.FunnotationException;
import io.github.pshevche.funnotation.internal.TranslationService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.pshevche.funnotation.internal.Tokenizer.toCamelCaseString;
import static io.github.pshevche.funnotation.internal.Tokenizer.toPascalCaseString;
import static io.github.pshevche.funnotation.internal.Tokenizer.wordsFromPascalOrCamelCase;
import static java.util.stream.Collectors.toMap;

/**
 * Processes {@link Translate} annotation by creating a delegating class where names of all methods and parameters are
 * replaced with their translated version.
 * <p>
 * Uses <a href="https://github.com/DeepLcom/deepl-java}">DeepL Java library</a> for performing the actual translation.
 */
@SupportedAnnotationTypes("io.github.pshevche.funnotation.Translate")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class TranslateProcessor extends AbstractProcessor {

    private final TranslationService translator;

    public TranslateProcessor() {
        this(new DeepLTranslationService(DeepLApiKey.createFromSystemProperty()));
    }

    TranslateProcessor(TranslationService translator) {
        this.translator = translator;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var element : roundEnv.getElementsAnnotatedWith(Translate.class)) {
            var language = element.getAnnotation(Translate.class).value();
            switch (element.getKind()) {
                case CLASS -> translateClass((TypeElement) element, language);
                default -> throw new FunnotationException("@Translate annotation can only be applied to a class");
            }
        }

        return true;
    }

    private void translateClass(TypeElement element, Language language) {
        var methodsToTranslate = extractAccessibleMethods(element);
        createDelegateClass(element, methodsToTranslate, language);
    }

    private static List<ExecutableElement> extractAccessibleMethods(Element element) {
        return element.getEnclosedElements().stream()
            .filter(it -> it.getKind() == ElementKind.METHOD)
            .map(it -> (ExecutableElement) it)
            .filter(it -> !it.getModifiers().contains(Modifier.PRIVATE))
            .toList();
    }

    private void createDelegateClass(TypeElement delegateClass, List<ExecutableElement> methodsToTranslate, Language language) {
        var packageName = processingEnv.getElementUtils().getPackageOf(delegateClass).getQualifiedName().toString();
        var delegateClassName = delegateClass.getSimpleName();
        var newClassName = translateClassName(delegateClassName, language);
        var newClassFQN = packageName + "." + newClassName;
        var filer = processingEnv.getFiler();

        try {
            var fileObject = filer.createSourceFile(newClassFQN, delegateClass);
            try (var writer = new PrintWriter(fileObject.openWriter())) {
                writer.println("""
                    package %s;
                                        
                    public class %s {
                        
                        private final %s delegate;
                        
                        public %s(%s delegate) {
                            this.delegate = delegate;
                        }
                        
                    %s
                    }""".formatted(packageName,
                    newClassName,
                    delegateClassName,
                    newClassName,
                    delegateClassName,
                    delegateMethodContent(methodsToTranslate, language)));
            }
        } catch (IOException e) {
            throw new FunnotationException("Could not process the @Translate annotation on class " + delegateClass.getSimpleName(), e);
        }

    }

    private String translateClassName(Name className, Language language) {
        var words = wordsFromPascalOrCamelCase(className.toString());
        var translatedWords = translator.translate(words, language.getCode());
        return toPascalCaseString(translatedWords);
    }

    private String translateMethodOrParameterName(Name methodName, Language language) {
        var words = wordsFromPascalOrCamelCase(methodName.toString());
        var translatedWords = translator.translate(words, language.getCode());
        return toCamelCaseString(translatedWords);
    }

    private String delegateMethodContent(List<ExecutableElement> methodsToTranslate, Language language) {
        return methodsToTranslate.stream()
            .map(it -> delegateMethodContent(it, language))
            .collect(Collectors.joining("\n"));
    }

    private String delegateMethodContent(ExecutableElement method, Language language) {
        var modifiers = modifiers(method);
        var newMethodName = translateMethodOrParameterName(method.getSimpleName(), language);
        var parameterNamesTranslations = method.getParameters().stream()
            .collect(toMap(
                VariableElement::getSimpleName,
                p -> translateMethodOrParameterName(p.getSimpleName(), language)
            ));

        return """
                %s%s %s(%s) {
                    this.delegate.%s(%s);
                }
            """.formatted(modifiers.isBlank() ? "" : modifiers + " ",
            method.getReturnType(),
            newMethodName,
            parameters(method, parameterNamesTranslations),
            method.getSimpleName(),
            parameterNames(method, parameterNamesTranslations));
    }

    private String modifiers(ExecutableElement method) {
        return method.getModifiers().stream()
            .map(Modifier::toString)
            .collect(Collectors.joining(" "));
    }

    private String parameters(ExecutableElement method, Map<Name, String> parameterNameTranslations) {
        return method.getParameters().stream()
            .map(it -> "%s %s".formatted(it.asType().toString(), parameterNameTranslations.get(it.getSimpleName())))
            .collect(Collectors.joining(", "));
    }

    private String parameterNames(ExecutableElement method, Map<Name, String> parameterNameTranslations) {
        return method.getParameters().stream()
            .map(VariableElement::getSimpleName)
            .map(parameterNameTranslations::get)
            .collect(Collectors.joining(", "));
    }
}
