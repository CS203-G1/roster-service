package csd.roster.util;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import csd.roster.exception.NoOptimalSolutionException;
import csd.roster.model.Employee;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class SchedulerUtil {
    public Map<Integer, List<UUID>> solve(List<UUID> employeeList) throws NoOptimalSolutionException{
        Loader.loadNativeLibraries();
        final int numNurses = employeeList.size();
        final int numDays = 5;
        final int numShifts = 1;

        final int[] allNurses = IntStream.range(0, numNurses).toArray();
        final int[] allDays = IntStream.range(0, numDays).toArray();
        final int[] allShifts = IntStream.range(0, numShifts).toArray();

        final int[][][] shiftRequests = new int[numNurses][numDays][numShifts];

        // Commented out because shift requests feature will not be up so soon
//        final int[][][] shiftRequests = new int[][][]{
//                {
//                        {0, 0, 1},
//                        {0, 0, 0},
//                        {0, 0, 0},
//                        {0, 0, 0},
//                        {0, 0, 1},
//                        {0, 1, 0},
//                        {0, 0, 1},
//                },
//                {
//                        {0, 0, 0},
//                        {0, 0, 0},
//                        {0, 1, 0},
//                        {0, 1, 0},
//                        {1, 0, 0},
//                        {0, 0, 0},
//                        {0, 0, 1},
//                },
//                {
//                        {0, 1, 0},
//                        {0, 1, 0},
//                        {0, 0, 0},
//                        {1, 0, 0},
//                        {0, 0, 0},
//                        {0, 1, 0},
//                        {0, 0, 0},
//                },
//                {
//                        {0, 0, 1},
//                        {0, 0, 0},
//                        {1, 0, 0},
//                        {0, 1, 0},
//                        {0, 0, 0},
//                        {1, 0, 0},
//                        {0, 0, 0},
//                },
//                {
//                        {0, 0, 0},
//                        {0, 0, 1},
//                        {0, 1, 0},
//                        {0, 0, 0},
//                        {1, 0, 0},
//                        {0, 1, 0},
//                        {0, 0, 0},
//                },
//        };

        // Creates the model.
        CpModel model = new CpModel();

        // Creates shift variables.
        // shifts[(n, d, s)]: nurse 'n' works shift 's' on day 'd'.
        IntVar[][][] shifts = new IntVar[numNurses][numDays][numShifts];
        for (int n : allNurses) {
            for (int d : allDays) {
                for (int s : allShifts) {
                    shifts[n][d][s] = model.newBoolVar("shifts_n" + n + "d" + d + "s" + s);
                }
            }
        }

        // Each shift is assigned to exactly one nurse in the schedule period.
        for (int d : allDays) {
            for (int s : allShifts) {
                IntVar[] x = new IntVar[numNurses];
                for (int n : allNurses) {
                    x[n] = shifts[n][d][s];
                }
                model.addEquality(LinearExpr.sum(x), numNurses / 2);
            }
        }

        // Each nurse works at most one shift per day.
        for (int n : allNurses) {
            for (int d : allDays) {
                IntVar[] x = new IntVar[numShifts];
                for (int s : allShifts) {
                    x[s] = shifts[n][d][s];
                }
                model.addLessOrEqual(LinearExpr.sum(x), 1);
            }
        }

        // Try to distribute the shifts evenly, so that each nurse works
        // minShiftsPerNurse shifts. If this is not possible, because the total
        // number of shifts is not divisible by the number of nurses, some nurses will
        // be assigned one more shift.
        int minShiftsPerNurse = (int) Math.floor(numShifts / 2);
        int maxShiftsPerNurse = 3;

        for (int n : allNurses) {
            IntVar[] numShiftsWorked = new IntVar[numDays * numShifts];
            for (int d : allDays) {
                for (int s : allShifts) {
                    numShiftsWorked[d * numShifts + s] = shifts[n][d][s];
                }
            }
            model.addLinearConstraint(
                    LinearExpr.sum(numShiftsWorked), minShiftsPerNurse, maxShiftsPerNurse);
        }

        IntVar[] flatShifts = new IntVar[numNurses * numDays * numShifts];
        int[] flatShiftRequests = new int[numNurses * numDays * numShifts];
        for (int n : allNurses) {
            for (int d : allDays) {
                for (int s : allShifts) {
                    flatShifts[n * numDays * numShifts + d * numShifts + s] = shifts[n][d][s];
                    flatShiftRequests[n * numDays * numShifts + d * numShifts + s] = shiftRequests[n][d][s];
                }
            }
        }
        model.maximize(LinearExpr.scalProd(flatShifts, flatShiftRequests));

        // Creates a solver and solves the model.
        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        Map<Integer, List<UUID>> roster_employees = new HashMap<>();
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            for (int d : allDays) {
                for (int n : allNurses) {
                    for (int s : allShifts) {
                        if (solver.value(shifts[n][d][s]) == 1L) {
                            List<UUID> li = roster_employees.getOrDefault(d, new LinkedList<UUID>());
                            li.add(employeeList.get(n));
                            roster_employees.put(d, li);
                        }
                    }
                }
            }
            return roster_employees;
        } else {
            throw new NoOptimalSolutionException();
        }
    }

}
