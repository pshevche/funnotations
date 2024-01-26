package io.github.pshevche.funnotation;

import io.github.pshevche.funnotation.internal.DeepLApiKey;
import io.github.pshevche.funnotation.internal.DeepLTranslator;
import io.github.pshevche.funnotation.internal.DefaultDeepLTranslator;
import io.github.pshevche.funnotation.internal.FunnotationException;

import javax.annotation.processing.AbstractProcessor;
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

@SupportedAnnotationTypes("io.github.pshevche.funnotation.Translate")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class TranslateProcessor extends AbstractProcessor {

    private final DeepLTranslator translator;

    public TranslateProcessor() {
        this(new DefaultDeepLTranslator(DeepLApiKey.createFromSystemProperty()));
    }

    TranslateProcessor(DeepLTranslator translator) {
        this.translator = translator;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var annotation : annotations) {
            var annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (var element : annotatedElements) {
                switch (element.getKind()) {
                    case CLASS -> translateClass((TypeElement) element);
                    default -> throw new FunnotationException("@Translate annotation can only be applied to a class");
                }
            }
        }

        return true;
    }

    private void translateClass(TypeElement element) {
        var methodsToTranslate = extractAccessibleMethods(element);
        createDelegateClass(element, methodsToTranslate);
    }

    private static List<ExecutableElement> extractAccessibleMethods(Element element) {
        return element.getEnclosedElements().stream()
            .filter(it -> it.getKind() == ElementKind.METHOD)
            .map(it -> (ExecutableElement) it)
            .filter(it -> !it.getModifiers().contains(Modifier.PRIVATE))
            .toList();
    }

    private void createDelegateClass(TypeElement delegateClass, List<ExecutableElement> methodsToTranslate) {
        var packageName = processingEnv.getElementUtils().getPackageOf(delegateClass).getQualifiedName().toString();
        var delegateClassName = delegateClass.getSimpleName();
        var newClassName = translateClassName(delegateClassName);
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
                    delegateMethodContent(methodsToTranslate)));
            }
        } catch (IOException e) {
            throw new FunnotationException("Could not process the @Translate annotation on class " + delegateClass.getSimpleName(), e);
        }

    }

    private String translateClassName(Name className) {
        var words = wordsFromPascalOrCamelCase(className.toString());
        var translatedWords = translator.translate(words);
        return toPascalCaseString(translatedWords);
    }

    private String translateMethodOrParameterName(Name methodName) {
        var words = wordsFromPascalOrCamelCase(methodName.toString());
        var translatedWords = translator.translate(words);
        return toCamelCaseString(translatedWords);
    }

    private String delegateMethodContent(List<ExecutableElement> methodsToTranslate) {
        return methodsToTranslate.stream()
            .map(this::delegateMethodContent)
            .collect(Collectors.joining("\n"));
    }

    private String delegateMethodContent(ExecutableElement method) {
        var modifiers = modifiers(method);
        var newMethodName = translateMethodOrParameterName(method.getSimpleName());
        var parameterNamesTranslations = method.getParameters().stream()
            .collect(toMap(
                VariableElement::getSimpleName,
                p -> translateMethodOrParameterName(p.getSimpleName())
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
