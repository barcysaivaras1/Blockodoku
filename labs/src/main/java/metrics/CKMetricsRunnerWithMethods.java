package metrics;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKMethodResult;
import com.github.mauricioaniche.ck.CKNotifier;

public class CKMetricsRunnerWithMethods {
    public static void main(String[] args) {
        // The path to the source code directory to analyze
        String pathToSource = "labs/src/main/java";

        // Create a CK instance and run it
        CK ck = new CK();
        CKNotifier notifier = new CKNotifier() {
            @Override
            public void notify(CKClassResult result) {
                System.out.println("Processing result: " + result);
                System.out.println("Class: " + result.getClassName());

                // Print basic metrics
                System.out.println("LOC: " + result.getLoc()); // Lines of code
                System.out.println("WMC: " + result.getWmc()); // Weighted method complexity
                System.out.println("DIT: " + result.getDit()); // Depth of inheritance tree
                System.out.println("NOC: " + result.getNoc()); // Number of children
                System.out.println("RFC: " + result.getRfc()); // Response for a class
                System.out.println("CBO: " + result.getCbo()); // Coupling between objects
                System.out.println("Ca / fanIn " + result.getFanin() ); // Afferent Couplings
                System.out.println("Ce / fanOut " + result.getFanout() ); // Efferent Couplings
                System.out.println("LCOM: " + result.getLcom()); // Lack of cohesion in methods
                System.out.println("Max nesting: " + result.getMaxNestedBlocks());

                // Print metrics for each method, including Cyclomatic Complexity
                for (CKMethodResult method : result.getMethods()) {
                    System.out.println("\t Method: " + method.getMethodName());
                    System.out.println("\t nLoops: " + method.getLoopQty());
                    System.out.println("\t Comparisons: " + method.getComparisonsQty());
                    // System.out.println("Branches: " + method.get
//                    System.out.println("\t Cyclomatic Complexity: " + method.getCyclomaticComplexity());
                }

                // Print any other useful metrics you want to track
                System.out.println("Number of Methods: " + result.getNumberOfMethods());
                System.out.println("Number of Fields: " + result.getNumberOfFields());
                System.out.println("Number of Public Methods: " + result.getNumberOfPublicMethods());


                // Print a separator for readability
                System.out.println("----------------------------------------");

            }
        };
        ck.calculate(pathToSource, notifier);
    }
}

