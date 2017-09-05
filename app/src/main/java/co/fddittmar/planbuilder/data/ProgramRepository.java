package co.fddittmar.planbuilder.data;

import java.util.List;

import co.fddittmar.planbuilder.data.model.Exercise;
import co.fddittmar.planbuilder.data.model.Program;

/**
 * Interface for all repositories that manipulate Programs.
 */

public interface ProgramRepository {

    List<Program> fetchAllPrograms();
    void saveProgram(String title, String startDate, String endDate, List<Exercise> exercises);
    void deleteProgram(long program_id);
}
