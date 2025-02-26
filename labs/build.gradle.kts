import java.net.URL
import java.io.FileOutputStream


plugins {
    java
    application
}

application {
    // Default main class, which you can override with -PmainClass
    // run at the CL in labs dir with:  ./gradlew run
    mainClass.set("shapes.TangramPuzzle")
}

tasks.named<JavaExec>("run") {
    // e.g. to run blocks puzzle, in labs dir:  ./gradlew run -PmainClass=blocks.Controller
    val mainClassName: String? = project.findProperty("mainClass") as String?
    if (mainClassName != null) {
        mainClass.set(mainClassName)
    }
}

group = "ecs658"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    // JUnit Jupiter API and Engine for testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.junit.platform:junit-platform-launcher:1.10.0")
    implementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")


    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // Other dependencies
    implementation("com.github.mauricioaniche:ck:0.7.0")
    implementation("black.ninia:jep:4.2.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.29.0") // Use the latest stable version

}


tasks.withType<JavaExec> {
    systemProperty("java.library.path", "/Users/aivarasz/Desktop/FOOP Labs/assign1-barcysaivaras1/venv/lib/python3.12/site-packages/jep")
}

tasks.withType<JavaCompile> {
    // Ignore compilation errors
//    options.isFailOnError = false
}


sourceSets {
    main {
        java {
            srcDirs("src/main/java")
        }
    }
    test {
        java {
            srcDirs("src/test/java")
        }
    }
}

// example of how to exclude a file or package from the main source set
//sourceSets {
//    main {
//        java {
//            exclude("**/HelloWorld.java")
//        }
//    }
//}
//
tasks.register<JavaExec>("ckMetrics") {
    group = "verification"
    description = "Run CK Metrics"

    // Ensure classes are compiled before running CK Metrics
    dependsOn(tasks.classes)

    // Set the classpath to include all dependencies
    classpath = sourceSets.main.get().runtimeClasspath

    // Set the main class to your CKMetricsRunner class
    mainClass.set("metrics.CKMetricsRunnerWithMethods")
}


// New JDT Complexity Task
tasks.register<JavaExec>("jdtComplexity") {
    group = "verification"
    description = "Run JDT Cyclomatic Complexity Analysis"

    // Ensure classes are compiled before running JDT Complexity
    dependsOn(tasks.classes)

    // Set the classpath to include all dependencies
    classpath = sourceSets.main.get().runtimeClasspath

    // Set the main class to your CyclomaticComplexityCalculator class
    mainClass.set("metrics.CyclomaticComplexityCalculator")
}


tasks.test {
    ignoreFailures = true
    useJUnitPlatform()
}


tasks.check {
    dependsOn(
        tasks.test,
        tasks.named("ckMetrics"), // Use named to reference ckMetrics properly
        tasks.named("jdtComplexity"),
    )
}

