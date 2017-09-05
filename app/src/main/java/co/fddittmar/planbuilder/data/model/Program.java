package co.fddittmar.planbuilder.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Program POJO.
 */

public class Program implements Serializable {

    private int id;
    private String title; //program title
    private String start_date;
    private String end_date;
    private List<Exercise> exercises; //list of exercises

    public Program(){}

    public Program(String title, String start_date, String end_date, List<Exercise> exercises) {
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.exercises = exercises;
    }

    public Program(int id, String title, String start_date, String end_date, List<Exercise> exercises) {
        this.id = id;
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.exercises = exercises;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}