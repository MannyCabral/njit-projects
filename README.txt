Collection of some coding assignments


--------------------------
SAT Solver (Uncommented)
--------------------------
-Rule.java is a class that contains indices for rules. For example, "a, b, c" represents "a OR b OR c"
-CNF.java contains the class for conjunctive normals forms and operations necessary to solve them. Contains an array of rules. Represents "Rule 1 AND Rule 2 AND Rule 3 AND ..." type contructions
-CNFSolver.java, finds out if CNF is satisfiable. If it is, returns a valid assignment.


Test cases
-ThreeInARow.java, translates 3 in a row game into a CNF.
-Sudoku.java, translates sudoku into a CNF

Notes:
-NP Complete, but don't use as an excuse to justify inefficiency




--------------------------
DFA (Deterministic Finite Automaton)
--------------------------
-Models DFAs

-current example recognizes .edu URLs, as noted in .jpg file




--------------------------
PDA (Pushdown Automaton)
--------------------------
-Models PDAs

-current example recognizes basic arithmetic expressions




--------------------------
Quiz Grader Functions (Not Fully commented)
--------------------------
-Json2Xml.php contains a function to convert a json object to an xml object.

-grader.php, primary function: gradeOE (grade Open Ended), contains functions to test java code for int, int array, string and int inputs and outputs. The student provides functions and a teacher provides test cases. Outputs grade and results, including compile and runtime errors.
-grading open ended.png displays general idea for grader.php