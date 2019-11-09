package com.apollographql.apollo.gradle.api

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface CompilerParams {
  val graphqlSourceDirectorySet: SourceDirectorySet

  val schemaFile: RegularFileProperty
  fun schemaFile(path: Any)

  val generateKotlinModels: Property<Boolean>
  fun generateKotlinModels(generateKotlinModels: Boolean)
  fun setGenerateKotlinModels(generateKotlinModels: Boolean)

  val generateTransformedQueries: Property<Boolean>
  fun generateTransformedQueries(generateTransformedQueries: Boolean)
  fun setGenerateTransformedQueries(generateTransformedQueries: Boolean)

  val customTypeMapping: Property<Map<String, String>>
  fun customTypeMapping(customTypeMapping: Map<String, String>)
  fun setCustomTypeMapping(customTypeMapping: Map<String, String>)

  val suppressRawTypesWarning: Property<Boolean>
  fun suppressRawTypesWarning(suppressRawTypesWarning: Boolean)
  fun setSuppressRawTypesWarning(suppressRawTypesWarning: Boolean)

  val useSemanticNaming: Property<Boolean>
  fun useSemanticNaming(useSemanticNaming: Boolean)
  fun setUseSemanticNaming(useSemanticNaming: Boolean)

  val nullableValueType: Property<String>
  fun nullableValueType(nullableValueType: String)
  fun setNullableValueType(nullableValueType: String)

  val generateModelBuilder: Property<Boolean>
  fun generateModelBuilder(generateModelBuilder: Boolean)
  fun setGenerateModelBuilder(generateModelBuilder: Boolean)

  val useJavaBeansSemanticNaming: Property<Boolean>
  fun useJavaBeansSemanticNaming(useJavaBeansSemanticNaming: Boolean)
  fun setUseJavaBeansSemanticNaming(useJavaBeansSemanticNaming: Boolean)

  val generateVisitorForPolymorphicDatatypes: Property<Boolean>
  fun generateVisitorForPolymorphicDatatypes(generateVisitorForPolymorphicDatatypes: Boolean)
  fun setGenerateVisitorForPolymorphicDatatypes(generateVisitorForPolymorphicDatatypes: Boolean)

  val rootPackageName: Provider<String>
  fun rootPackageName(rootPackageName: String)
}
