package co.fddittmar.planbuilder.data

import co.fddittmar.planbuilder.data.model.Exercise
import co.fddittmar.planbuilder.data.model.Program

/**
 * Interface for all repositories that manipulate Programs.
 */

interface ProgramRepository {

    fun fetchAllPrograms(): ArrayList<Program>
    fun saveProgram(title: String, startDate: String, endDate: String, exercises: List<Exercise>)
    fun deleteProgram(program_id: Long)
}
