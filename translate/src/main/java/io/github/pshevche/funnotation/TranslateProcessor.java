package io.github.pshevche.funnotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("io.github.pshevche.funnotation.Translate")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class TranslateProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var annotation : annotations) {
            var annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (var element : annotatedElements) {
                switch (element.getKind()) {
                    case CLASS -> translateClass((TypeElement) element);
                    default ->
                        throw new IllegalArgumentException("@Translate annotation can only be applied to a class");
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
        var delegateClassName = delegateClass.getSimpleName().toString();
        var newClassName = "Translated" + delegateClassName;
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
            throw new RuntimeException("Could not process the @Translate annotation on class " + delegateClass.getSimpleName(), e);
        }

    }

    private String delegateMethodContent(List<ExecutableElement> methodsToTranslate) {
        return methodsToTranslate.stream()
            .map(this::delegateMethodContent)
            .collect(Collectors.joining("\n"));
    }

    private String delegateMethodContent(ExecutableElement method) {
        var modifiers = modifiers(method);
        return """
                %s%s %s(%s) {
                    this.delegate.%s(%s);
                }
            """.formatted(modifiers.isBlank() ? "" : modifiers + " ",
            method.getReturnType(),
            method.getSimpleName(),
            parameters(method),
            method.getSimpleName(),
            parameterNames(method));
    }

    private String modifiers(ExecutableElement method) {
        return method.getModifiers().stream()
            .map(Modifier::toString)
            .collect(Collectors.joining(" "));
    }

    private String parameters(ExecutableElement method) {
        return method.getParameters().stream()
            .map(it -> "%s %s".formatted(it.asType().toString(), it.getSimpleName()))
            .collect(Collectors.joining(", "));
    }

    private String parameterNames(ExecutableElement method) {
        return method.getParameters().stream()
            .map(VariableElement::getSimpleName)
            .collect(Collectors.joining(", "));
    }
}
