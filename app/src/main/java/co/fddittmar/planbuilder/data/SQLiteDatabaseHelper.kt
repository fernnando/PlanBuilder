package co.fddittmar.planbuilder.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import co.fddittmar.planbuilder.data.model.Exercise
import co.fddittmar.planbuilder.data.model.Program
import java.util.*

/**
 * Class responsible to control our SQLite database.
 */

class SQLiteDatabaseHelper(context: Context) : SQLiteOpenHelper(context, SQLiteDatabaseHelper.DATABASE, null, SQLiteDatabaseHelper.DATABASE_VERSION), ProgramRepository {

    override fun onCreate(db: SQLiteDatabase) {
        //Creating required tables
        db.execSQL(CREATE_TABLE_PROGRAMS)
        db.execSQL(CREATE_TABLE_EXERCISES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Drop old tables
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_PROGRAMS)
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_EXERCISES)

        //Create new tables
        onCreate(db)
    }

    override fun saveProgram(title: String, startDate: String, endDate: String, exercises: List<Exercise>) {
        createProgram(Program(title, startDate, endDate, exercises))
    }

    /*
    * Creating a Program
    */
    private fun createProgram(program: Program): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_TITLE, program.title)
        values.put(KEY_START_DATE, program.start_date)
        values.put(KEY_END_DATE, program.end_date)

        // insert row
        val program_id = db.insert(TABLE_PROGRAMS, null, values)

        for (exercise in program.exercises) {
            createExercise(exercise, program_id)
        }

        return program_id
    }

    /*
     * Get a single Program
     */
    fun getProgram(program_id: Long): Program {
        val db = this.readableDatabase

        val selectQuery = "SELECT  * FROM $TABLE_PROGRAMS WHERE $KEY_ID  = $program_id"

        Log.e(LOG, selectQuery)

        val c = db.rawQuery(selectQuery, null)

        c?.moveToFirst()

        val program = Program()
        program.id = c!!.getInt(c.getColumnIndex(KEY_ID))
        program.title = c.getString(c.getColumnIndex(KEY_TITLE))
        program.start_date = c.getString(c.getColumnIndex(KEY_START_DATE))
        program.end_date = c.getString(c.getColumnIndex(KEY_END_DATE))

        c.close()

        return program
    }

    /*
    * Getting all Programs
    * */
    override fun fetchAllPrograms(): ArrayList<Program> {
        val programs = ArrayList<Program>()
        val selectQuery = "SELECT  * FROM " + TABLE_PROGRAMS

        Log.e(LOG, selectQuery)

        val db = this.readableDatabase
        val c = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                val program = Program()
                program.id = c.getInt(c.getColumnIndex(KEY_ID))
                program.title = c.getString(c.getColumnIndex(KEY_TITLE))
                program.start_date = c.getString(c.getColumnIndex(KEY_START_DATE))
                program.end_date = c.getString(c.getColumnIndex(KEY_END_DATE))

                //getting all exercises from program_id
                program.exercises = getAllExercisesByProgram(program.id.toLong())

                // adding to program list
                programs.add(program)
            } while (c.moveToNext())
        }
        c.close()

        return programs
    }


    /*
        * Deleting a Program
        */
    override fun deleteProgram(program_id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_PROGRAMS, KEY_ID + " = ?",
                arrayOf(program_id.toString()))
    }

    /*
    * Creating a Exercise
    */
    fun createExercise(exercise: Exercise, program_id: Long): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_NAME, exercise.name)
        values.put(KEY_REPS, exercise.reps)
        values.put(KEY_WEIGHT, exercise.weight)
        values.put(KEY_PROGRAM_ID, program_id)

        //Return insert row id
        return db.insert(TABLE_EXERCISES, null, values)
    }

    /*
    * Getting all todos under single tag
    * */
    fun getAllExercisesByProgram(program_id: Long): List<Exercise> {
        val exercises = ArrayList<Exercise>()

        val selectQuery = "SELECT  * FROM $TABLE_EXERCISES WHERE $KEY_PROGRAM_ID = $program_id"

        Log.e(LOG, selectQuery)

        val db = this.readableDatabase
        val c = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                val exercise = Exercise()
                exercise.id = c.getInt(c.getColumnIndex(KEY_ID))
                exercise.name = c.getString(c.getColumnIndex(KEY_NAME))
                exercise.reps = c.getInt(c.getColumnIndex(KEY_REPS))
                exercise.weight = c.getInt(c.getColumnIndex(KEY_WEIGHT))

                // adding to exercises list
                exercises.add(exercise)
            } while (c.moveToNext())
        }
        c.close()

        return exercises
    }

    companion object {

        private var sInstance: SQLiteDatabaseHelper? = null

        // Logcat tag
        private val LOG = "SQLiteDatabaseHelper"

        // SQLiteDatabaseHelper Version
        private val DATABASE_VERSION = 1

        // SQLiteDatabaseHelper Name
        private val DATABASE = "planbuilder.db"

        // Table Names
        private val TABLE_PROGRAMS = "programs"
        private val TABLE_EXERCISES = "exercises"

        // Common column names
        private val KEY_ID = "_id"

        // PROGRAMS Table - column nmaes
        private val KEY_TITLE = "title"
        private val KEY_START_DATE = "start_date"
        private val KEY_END_DATE = "end_date"

        // EXERCISES Table - column names
        private val KEY_NAME = "name"
        private val KEY_REPS = "reps"
        private val KEY_WEIGHT = "weight"
        private val KEY_PROGRAM_ID = "program_id"


        // ***** TABLE CREATE STATEMENTS *****

        // Programs table create statement
        private val CREATE_TABLE_PROGRAMS = "CREATE TABLE $TABLE_PROGRAMS(" +
                "$KEY_ID INTEGER PRIMARY KEY NOT NULL," +
                "$KEY_TITLE TEXT," +
                "$KEY_START_DATE TEXT," +
                "$KEY_END_DATE TEXT)"

        // Exercises table create statement
        private val CREATE_TABLE_EXERCISES = "CREATE TABLE $TABLE_EXERCISES(" +
                "$KEY_ID INTEGER PRIMARY KEY NOT NULL," +
                "$KEY_NAME TEXT," +
                "$KEY_REPS INTEGER," +
                "$KEY_WEIGHT INTEGER," +
                "$KEY_PROGRAM_ID INTEGER)"

        @Synchronized fun getInstance(context: Context): SQLiteDatabaseHelper? {
            val instance = if (sInstance == null) SQLiteDatabaseHelper(context.applicationContext) else sInstance
            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.

            return instance
        }
    }

}

