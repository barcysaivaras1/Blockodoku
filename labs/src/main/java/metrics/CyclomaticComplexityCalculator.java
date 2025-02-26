package metrics;

import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class CyclomaticComplexityCalculator {

    public static void main(String[] args) {
        // Specify the directory containing Java source files
        String sourceDirectory = "src/main/java/"; // Update this path as needed
        String filePattern = args.length > 0 ? args[0] : "";

        System.out.println("File pattern: " + filePattern);
        processSourceDirectory(sourceDirectory, filePattern);
        System.exit(0);
    }

    public static void processSourceDirectory(String sourceDirectory, String filePattern) {
        System.out.println("Calculating Cyclomatic Complexity for Java files in: " + sourceDirectory);
        File dir = new File(sourceDirectory);
        if (dir.exists() && dir.isDirectory()) {
            processDirectoryRecursively(dir, filePattern);
        } else {
            System.out.println("Invalid directory: " + sourceDirectory);
        }
    }

    private static void processDirectoryRecursively(File dir, String filePattern) {
        Map<String, Integer> classComplexities = new HashMap<>();
        List<Map.Entry<String, Integer>> allMethods = new ArrayList<>();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                // Recursively process subdirectories
                processDirectoryRecursively(file, filePattern);
            } else if (file.getName().endsWith(".java") &&
                    (filePattern.isEmpty() || file.getName().contains(filePattern))) {
                // Process Java files matching the pattern
                processJavaFile(file, classComplexities, allMethods);
            }
        }

        // Print aggregated class-level complexities only if there are results
        if (!classComplexities.isEmpty()) {
            System.out.println("\nClass-Level Cyclomatic Complexities:");
            classComplexities.forEach((className, complexity) ->
                    System.out.println("Class: " + className + ", Total Complexity: " + complexity));
        }

        // Print sorted method complexities only if there are results
        if (!allMethods.isEmpty()) {
            System.out.println("\nMethod Complexities:");
            allMethods.sort(Map.Entry.comparingByValue());
            allMethods.forEach(entry ->
                    System.out.println("Method: " + entry.getKey() + ", Cyclomatic Complexity: " + entry.getValue()));
        }
    }

    private static void processJavaFile(File file, Map<String, Integer> classComplexities,
                                        List<Map.Entry<String, Integer>> allMethods) {
        try {
            String source = new String(Files.readAllBytes(file.toPath()));

            ASTParser parser = ASTParser.newParser(AST.JLS_Latest); // Adjust for your JDK version
            parser.setSource(source.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);

            CompilationUnit cu = (CompilationUnit) parser.createAST(null);

            MethodCyclomaticComplexityVisitor visitor = new MethodCyclomaticComplexityVisitor();
            cu.accept(visitor);

            // Aggregate complexities for the current class
            String className = file.getName().replace(".java", "");
            int totalComplexity = visitor.getMethodComplexities().values().stream().mapToInt(Integer::intValue).sum();
            if (totalComplexity > 0) {
                classComplexities.put(className, classComplexities.getOrDefault(className, 0) + totalComplexity);
            }

            // Add all method complexities to the global list
            visitor.getMethodComplexities().forEach((methodName, complexity) ->
                    allMethods.add(new AbstractMap.SimpleEntry<>(className + "." + methodName, complexity)));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing file: " + file.getPath());
            e.printStackTrace();
        }
    }

    // Visitor class to calculate Cyclomatic Complexity at the method level
    static class MethodCyclomaticComplexityVisitor extends ASTVisitor {
        private final Map<String, Integer> methodComplexities = new HashMap<>();
        private int complexity;

        @Override
        public boolean visit(MethodDeclaration node) {
            // Reset complexity for this method
            complexity = 1;

            if (node.getBody() != null) {
                node.getBody().accept(this);
                methodComplexities.put(node.getName().toString(), complexity);
            }
            return false;
        }

        @Override
        public boolean visit(IfStatement node) {
            complexity++;
            return super.visit(node);
        }

        @Override
        public boolean visit(SwitchStatement node) {
            complexity += node.statements().size();
            return super.visit(node);
        }

        @Override
        public boolean visit(ForStatement node) {
            complexity++;
            return super.visit(node);
        }

        @Override
        public boolean visit(WhileStatement node) {
            complexity++;
            return super.visit(node);
        }

        @Override
        public boolean visit(DoStatement node) {
            complexity++;
            return super.visit(node);
        }

        @Override
        public boolean visit(ConditionalExpression node) {
            complexity++;
            return super.visit(node);
        }

        public Map<String, Integer> getMethodComplexities() {
            return methodComplexities;
        }
    }
}
