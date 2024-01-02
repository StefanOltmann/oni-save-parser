import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("plugin.serialization") version "1.9.21"
}

group = "de.stefan_oltmann"

kotlin {

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {

        moduleName = "app"

        browser {
            commonWebpackConfig {
                outputFileName = "app.js"
            }
        }

        binaries.executable()

        /* Use Binaryen optimization to make it smaller & faster */
        applyBinaryen()
    }

    /* Only to execute tests. */
    jvm()

    sourceSets {

        val wasmJsMain by getting

        sourceSets["commonMain"].kotlin.srcDir(file("build/generated/src/commonMain/kotlin"))

        commonMain.dependencies {

            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        wasmJsMain.dependencies {
            implementation(npm("pako", "2.1.0"))
        }
    }
}
