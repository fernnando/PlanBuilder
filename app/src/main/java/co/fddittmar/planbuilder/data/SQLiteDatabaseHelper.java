package co.fddittmar.planbuilder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import co.fddittmar.planbuilder.data.model.Exercise;
import co.fddittmar.planbuilder.data.model.Program;

/**
 * Class responsible to control our SQLite database.
 */

public class SQLiteDatabaseHelper extends SQLiteOpenHelper implements ProgramRepository {

    private static SQLiteDatabaseHelper sInstance;

    // Logcat tag
    private static final String LOG = "SQLiteDatabaseHelper";

    // SQLiteDatabaseHelper Version
    private static final int DATABASE_VERSION = 1;

    // SQLiteDatabaseHelper Name
    private static final String DATABASE = "planbuilder.db";

    // Table Names
    private static final String TABLE_PROGRAMS = "programs";
    private static final String TABLE_EXERCISES = "exercises";

    // Common column names
    private static final String KEY_ID = "_id";

    // PROGRAMS Table - column nmaes
    private static final String KEY_TITLE = "title";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";

    // EXERCISES Table - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_REPS = "reps";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_PROGRAM_ID = "program_id";


    // ***** TABLE CREATE STATEMENTS *****

    // Programs table create statement
    private static final String CREATE_TABLE_PROGRAMS = "CREATE TABLE "+ TABLE_PROGRAMS + "("
            + KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
            + KEY_TITLE + " TEXT,"
            + KEY_START_DATE + " TEXT,"
            + KEY_END_DATE + " TEXT" + ")";

    // Exercises table create statement
    private static final String CREATE_TABLE_EXERCISES = "CREATE TABLE "+ TABLE_EXERCISES + "("
            + KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
            + KEY_NAME + " TEXT,"
            + KEY_REPS + " INTEGER,"
            + KEY_WEIGHT + " INTEGER,"
            + KEY_PROGRAM_ID + " INTEGER" + ")";


    public SQLiteDatabaseHelper(Context context){
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating required tables
        db.execSQL(CREATE_TABLE_PROGRAMS);
        db.execSQL(CREATE_TABLE_EXERCISES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop old tables
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_PROGRAMS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_EXERCISES);

        //Create new tables
        onCreate(db);
    }

    @Override
    public void saveProgram(String title, String startDate, String endDate, List<Exercise> exercises) {
        createProgram(new Program(title, startDate, endDate, exercises));
    }

    /**
     * Create a Program
     */
    private long createProgram(Program program) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, program.getTitle());
        values.put(KEY_START_DATE, program.getStart_date());
        values.put(KEY_END_DATE, program.getEnd_date());

        // insert row
        long program_id = db.insert(TABLE_PROGRAMS, null, values);

        for (Exercise exercise : program.getExercises()) {
            createExercise(exercise, program_id);
        }

        return program_id;
    }

    /**
     * Get a single Program
     */
    public Program getProgram(long program_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_PROGRAMS + " WHERE "
                + KEY_ID + " = " + program_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Program program = new Program();
        program.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        program.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
        program.setStart_date(c.getString(c.getColumnIndex(KEY_START_DATE)));
        program.setEnd_date(c.getString(c.getColumnIndex(KEY_END_DATE)));

        c.close();

        return program;
    }

    /**
     * Fetch all Programs from the database
     */
    public List<Program> fetchAllPrograms() {
        List<Program> programs = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PROGRAMS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Program program = new Program();
                program.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                program.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                program.setStart_date(c.getString(c.getColumnIndex(KEY_START_DATE)));
                program.setEnd_date(c.getString(c.getColumnIndex(KEY_END_DATE)));

                //getting all exercises from program_id
                program.setExercises(getAllExercisesByProgram(program.getId()));

                // adding to program list
                programs.add(program);
            } while (c.moveToNext());
        }
        c.close();

        return programs;
    }


    /**
     * Delete a Program from the database
     */
    public void deleteProgram(long program_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] programReference = new String[] { String.valueOf(program_id) };

        db.delete(TABLE_PROGRAMS, KEY_ID + " = ?", programReference);
        db.delete(TABLE_EXERCISES, KEY_PROGRAM_ID + " = ?", programReference);
    }

    /**
     * Creating an Exercise
     */
    public long createExercise(Exercise exercise, long program_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, exercise.getName());
        values.put(KEY_REPS, exercise.getReps());
        values.put(KEY_WEIGHT, exercise.getWeight());
        values.put(KEY_PROGRAM_ID, program_id);

        //Return insert row id
        return db.insert(TABLE_EXERCISES, null, values);
    }

    /**
     * Getting all Exercises of a Program
     */
    public List<Exercise> getAllExercisesByProgram(long program_id) {
        List<Exercise> exercises = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES
                + " WHERE " + KEY_PROGRAM_ID + " = " + program_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Exercise exercise = new Exercise();
                exercise.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                exercise.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                exercise.setReps((c.getInt(c.getColumnIndex(KEY_REPS))));
                exercise.setWeight(c.getInt(c.getColumnIndex(KEY_WEIGHT)));

                // adding to exercises list
                exercises.add(exercise);
            } while (c.moveToNext());
        }
        c.close();

        return exercises;
    }

    /**
     * Ensures that only one DatabaseHelper will ever exist at any given time. If the sInstance
     * object has not been initialized, one will be created. If one has already been created then
     * it will simply be returned.
     * Source: http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
     */
    public static synchronized SQLiteDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new SQLiteDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

}

