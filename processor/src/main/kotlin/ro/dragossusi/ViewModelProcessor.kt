package ro.dragossusi

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import org.kodein.di.DI
import ro.dragossusi.kodein.DIInject
import ro.dragossusi.kodein.DITag
import ro.dragossusi.kodein.DIViewModel
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ViewModelProcessor : AbstractProcessor() {

    private val annotation = DIViewModel::class

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(annotation.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        val elements = roundEnvironment.getElementsAnnotatedWith(annotation.java)
        if (elements.isEmpty()) return true
        val kaptGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        if (kaptGeneratedDir.isNullOrEmpty()) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Can't find the target directory for generated Kotlin files."
            )
            return false
        }
        val targets = elements
            //get classes only
            .mapNotNull { element ->
                if (element.kind != ElementKind.CLASS) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR, "@Loggable can't be applied to $element: must be a Kotlin class",
                        element
                    )
                    null
                } else element as TypeElement
            }
            //map to targets
            .map { element ->
                generateClass(kaptGeneratedDir, element)
            }
        writeModule(targets, kaptGeneratedDir)
        return true
    }

    fun generateClass(kaptGeneratedDir: String, element: Element): TargetViewModel {
        val packageName = processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()
        val constructor: ExecutableElement? = element.enclosedElements.firstOrNull {
            it.getAnnotation(DIInject::class.java) != null && it.kind == ElementKind.CONSTRUCTOR
        } as ExecutableElement?
        constructor ?: throw Exception("Constructor not annotated")
        return TargetViewModel(
            processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString(),
            element.simpleName.toString(),
            constructor.parameters.map {
                generateTargetParameter(it)
            }
        )
    }

    fun generateTargetParameter(element: VariableElement): TargetParameter {
        val type = element.asType() as DeclaredType
        val typeElement = type.asElement()
        return TargetParameter(
            processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString(),
            typeElement.simpleName.toString(),
            element.getAnnotation(DITag::class.java)?.tag?.ifEmpty { null }
        )
    }

    fun writeModule(items: List<TargetViewModel>, kaptGeneratedDir: String) {
        val file = File(kaptGeneratedDir).apply {
            mkdir()
        }
        FileSpec.builder("ro.dragossusi.kodein", "viewModelModule")
            .addImport("org.kodein.di", "DI", "bind", "provider", "instance")
            .addImports(items)
            .addProperty(
                PropertySpec.builder(
                    "viewModelModule",
                    DI.Module::class,
                ).initializer(
                    CodeBlock.builder()
                        .add("DI.Module(\"viewModels\") {\n")
                        .addBindStatements(items)
                        .add("}")
                        .build()
                )
                    .build()
            )
            .build()
            .writeTo(file)
    }

    fun CodeBlock.Builder.addBindStatements(items: List<TargetViewModel>) = apply {
        items.forEach {
            addStatement(createModuleStatement(it))
        }
    }

    fun FileSpec.Builder.addImports(items: List<TargetViewModel>): FileSpec.Builder = apply {
        val imports = mutableListOf<TargetParameter>()
        items.forEach { vm ->
            addImport(vm.pack, vm.name)
            vm.parameters.forEach { param ->
                imports += param
            }
        }
        imports.distinctBy {
            it.className
        }.forEach {
            addImport(it.pack, it.name)
        }
    }

    private fun createModuleStatement(target: TargetViewModel): String {
        return "\tbind<${target.name}>() with provider { ${target.toConstructorInvocation()} }"
    }

}

const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"